package com.example.merchant.util.mobipay;

import com.example.merchant.global.enums.MerchantType;
import com.example.merchant.util.credential.CredentialUtil;
import com.example.merchant.util.mobipay.dto.CancelTransactionRequest;
import com.example.merchant.util.mobipay.dto.CancelTransactionResponse;
import com.example.merchant.util.mobipay.dto.MerchantTransactionRequest;
import com.example.merchant.util.mobipay.dto.MerchantTransactionResponse;
import com.example.merchant.util.mobipay.dto.MobiPaymentRequest;
import com.example.merchant.util.mobipay.dto.MobiPaymentResponse;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class MobiPayImpl implements MobiPay{

    @Value("${mobipay.url}")
    private String MOBI_PAY_URL;

    private final Validator validator;
    private final RestClient restClient = RestClient.create();
    private final CredentialUtil credentialUtil;

    @Override
    public ResponseEntity<MobiPaymentResponse> request(MobiPaymentRequest request,
                                                       Class<MobiPaymentResponse> responseClass) {
        final String url = MOBI_PAY_URL + "/postpayments/request";
        ResponseEntity<MobiPaymentResponse> responseEntity = post(request.getType(), request, url, responseClass);
        validate(responseEntity);

        return responseEntity;
    }

    @Override
    public ResponseEntity<MerchantTransactionResponse> getTransactionList(MerchantTransactionRequest request,
                                                                          Class<MerchantTransactionResponse> responseClass) {
//        final String url = MOBI_PAY_URL + "/merchants/" + request.getMerchantId() + "/transactions";
        return null;
    }

    @Override
    public ResponseEntity<CancelTransactionResponse> cancelTransaction(CancelTransactionRequest request,
                                                                       Class<CancelTransactionResponse> responseClass) {
//        final String url = MOBI_PAY_URL + "/merchants/" + request.getMerchantId() + "/cancelled-transactions/" + request.getTransactionUniqueNo();";
        return null;
    }

    private <T, R> ResponseEntity<R> post(MerchantType merchantType, T requestBody, String url, Class<R> responseClass) {
        return restClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header("mobiApiKey", credentialUtil.getMobiApiKeyByType(merchantType))
                .body(requestBody)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (request, response) -> ResponseEntity.status(response.getStatusCode()).build()
                )
                .toEntity(responseClass);
    }

    private <T> void validate(T object) {
        Set<ConstraintViolation<T>> validatedSet = validator.validate(object);
        if (!validatedSet.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }
}