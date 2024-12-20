package com.example.mobipay.domain.postpayments.service;

import com.example.mobipay.domain.approvalwaiting.entity.ApprovalWaiting;
import com.example.mobipay.domain.approvalwaiting.repository.ApprovalWaitingRepository;
import com.example.mobipay.domain.merchant.entity.Merchant;
import com.example.mobipay.domain.merchant.error.MerchantNotFoundException;
import com.example.mobipay.domain.merchanttransaction.entity.MerchantTransaction;
import com.example.mobipay.domain.merchanttransaction.repository.MerchantTransactionRepository;
import com.example.mobipay.domain.mobiuser.entity.MobiUser;
import com.example.mobipay.domain.ownedcard.entity.OwnedCard;
import com.example.mobipay.domain.postpayments.dto.ApprovalPaymentRequest;
import com.example.mobipay.domain.postpayments.dto.ApprovalPaymentResponse;
import com.example.mobipay.domain.postpayments.dto.PaymentContext;
import com.example.mobipay.domain.postpayments.dto.cardtransaction.CardTransactionRequest;
import com.example.mobipay.domain.postpayments.dto.cardtransaction.CardTransactionResponse;
import com.example.mobipay.domain.postpayments.dto.paymentresult.PaymentResultRequest;
import com.example.mobipay.domain.postpayments.error.CardTransactionServerError;
import com.example.mobipay.domain.postpayments.error.NotEqualPaymentBalanceException;
import com.example.mobipay.domain.postpayments.error.TransactionAlreadyApprovedException;
import com.example.mobipay.domain.postpayments.util.PaymentValidator;
import com.example.mobipay.domain.registeredcard.entity.RegisteredCard;
import com.example.mobipay.oauth2.dto.CustomOAuth2User;
import com.example.mobipay.util.RestClientUtil;
import jakarta.persistence.LockModeType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostPaymentsApprovalService {

    private static final String CARD_TRANSACTION_REQUEST = "createCreditCardTransaction";

    private final PaymentValidator paymentValidator;
    private final ApprovalWaitingRepository approvalWaitingRepository;
    private final MerchantTransactionRepository merchantTransactionRepository;
    private final RestClientUtil restClientUtil;

    @Value("${ssafy.api.key}")
    private String ssafyApiKey;

    @Value("${mobi.mer.api.key}")
    private String mobiToMerApiKey;

    // Android로부터 결제요청이 온 경우 결제 로직 시작
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public ApprovalPaymentResponse processPaymentApproval(ApprovalPaymentRequest request, CustomOAuth2User oAuth2User) {

        // oauth2User 검증
        MobiUser mobiUser = paymentValidator.validateOAuth2User(oAuth2User);

        // request에서 approved가 false이면 return
        if (!request.getApproved()) {
            return ApprovalPaymentResponse.from(request);
        }

        try {
            // approvalWaiting 검증
            ApprovalWaiting approvalWaiting = paymentValidator.validateApprovalWaiting(request);
            // merchant 검증
            Merchant merchant = paymentValidator.validateMerchant(request);
            // cardNo 및 registeredCard 검증
            OwnedCard ownedCard = paymentValidator.validateCardNo(request);
            RegisteredCard registeredCard = paymentValidator.validateRegisteredCard(mobiUser, ownedCard);
            // 결제 금액이 일회 결제 금액 한도를 넘어가는지 검증
            paymentValidator.validateOneTimeLimit(request, registeredCard);
            // SSAFY_API 결제 진행 요청
            PaymentContext context = PaymentContext.builder()
                    .request(request)
                    .mobiUser(mobiUser)
                    .ownedCard(ownedCard)
                    .registeredCard(registeredCard)
                    .merchant(merchant)
                    .approvalWaiting(approvalWaiting)
                    .build();
            processTransaction(context);

            // paymentBalance가 일치하지 않거나, 올바르지 않은 merchantId일 경우 결제 실패 응답을 Merchant Server로 보내지 않는다.
            // 검증되지 않은 정보이기 때문.
            // 결제금액 일회 한도 초과의 경우 올바른 결제라고 판단하여 결제 실패 응답을 Merchant Server로 보낸다.
        } catch (TransactionAlreadyApprovedException | NotEqualPaymentBalanceException | MerchantNotFoundException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendTransactionFailedResult(request);
            throw e;
        }
        sendTransactionSuccessResult(request);

        return ApprovalPaymentResponse.from(request);
    }

    // 결제 성공 결과 요청
    private void sendTransactionSuccessResult(ApprovalPaymentRequest request) {
        Map<String, String> additionalHeaders = Map.of("merApiKey", mobiToMerApiKey);
        PaymentResultRequest paymentResultRequest = new PaymentResultRequest(
                true, request.getMerchantId(), request.getPaymentBalance(), request.getInfo());

        ResponseEntity<Void> response = restClientUtil.sendResultToMerchantServer(paymentResultRequest,
                additionalHeaders);
        log.info("[Transaction Success] merchant server response: {}", response.getStatusCode());
    }

    // 결제 실패 결과 요청
    private void sendTransactionFailedResult(ApprovalPaymentRequest request) {
        Map<String, String> additionalHeaders = Map.of("merApiKey", mobiToMerApiKey);
        PaymentResultRequest paymentResultRequest = new PaymentResultRequest(
                false, request.getMerchantId(), request.getPaymentBalance(), request.getInfo());

        ResponseEntity<Void> response = restClientUtil.sendResultToMerchantServer(paymentResultRequest,
                additionalHeaders);
        log.info("[Transaction Failed] merchant server response: {}", response.getStatusCode());
    }

    // SSAFY_API 결제 진행 및 응답 처리
    private void processTransaction(PaymentContext context) {

        ResponseEntity<CardTransactionResponse> response = getCardTransactionResponse(context);
        // 결제 승인 성공 시 MerchantTransaction 생성 및 저장
        boolean responseSuccess = response.getStatusCode().is2xxSuccessful();
        if (responseSuccess) {
            createMerchantTransaction(context, response);
            changeApprovedStatus(context.getApprovalWaiting());
            return;
        }
        // 결제 실패 처리
        handleTransactionFailure(response);
    }

    // SSAFY_API 결제 요청
    private ResponseEntity<CardTransactionResponse> getCardTransactionResponse(PaymentContext context) {
        CardTransactionRequest cardTransactionRequest = new CardTransactionRequest(
                CARD_TRANSACTION_REQUEST,
                ssafyApiKey,
                context.getMobiUser().getSsafyUser().getUserKey(),
                context.getOwnedCard(),
                context.getRequest()
        );

        return restClientUtil.processCardTransaction(cardTransactionRequest, CardTransactionResponse.class);
    }

    // MerchantTransaction 생성 및 저장
    private void createMerchantTransaction(PaymentContext context, ResponseEntity<CardTransactionResponse> response) {
        MerchantTransaction merchantTransaction = MerchantTransaction.of(context.getRequest(), response);
        merchantTransaction.addRelations(context.getRegisteredCard(), context.getMerchant());
        merchantTransactionRepository.save(merchantTransaction);
    }

    // ApprovalWaiting의 approved 상태 변경
    private void changeApprovedStatus(ApprovalWaiting approvalWaiting) {
        approvalWaiting.activateApproved();
        approvalWaitingRepository.save(approvalWaiting);
    }

    // 결제 실패 시 처리
    private void handleTransactionFailure(ResponseEntity<CardTransactionResponse> response) {
        CardTransactionResponse responseBody = response.getBody();
        log.info("cardTransaction ResponseCode : {}", responseBody.getResponseCode());

        if (responseBody.getResponseCode().contains("H1") || responseBody.getResponseCode().contains("Q")) {
            throw new CardTransactionServerError();
        }
    }
}
