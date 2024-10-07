package com.kimnlee.mobipay.presentation.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kimnlee.cardmanagement.data.api.CardManagementApiService
import com.kimnlee.cardmanagement.data.model.AutoPaymentCardRequest
import com.kimnlee.cardmanagement.data.model.CardDetailResponse
import com.kimnlee.cardmanagement.data.model.OwnedCard
import com.kimnlee.cardmanagement.data.model.RegisterCardRequest
import com.kimnlee.cardmanagement.data.model.RegisteredCard
import com.kimnlee.cardmanagement.presentation.viewmodel.OwnedCardUiState
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.common.network.ApiClient
import com.kimnlee.common.network.NaverMapService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val apiClient: ApiClient,
) : ViewModel() {
    // 네이버 맵 서비스
    private val _naverMapService = MutableStateFlow<NaverMapService?>(apiClient.naverMapService)
    val naverMapService: StateFlow<NaverMapService?> = _naverMapService

    private val _selectedCards = MutableStateFlow<Set<OwnedCard>>(emptySet())
    val selectedCards: StateFlow<Set<OwnedCard>> = _selectedCards.asStateFlow()

    // 소유 카드 리스트 상태
    private val _cardState = MutableStateFlow<OnboardOwnedCardState>(OnboardOwnedCardState.Loading)
    val cardState: StateFlow<OnboardOwnedCardState> = _cardState.asStateFlow()

    private val _isAllSelected = MutableStateFlow(false)
    val isAllSelected: StateFlow<Boolean> = _isAllSelected.asStateFlow()

    // 등록된 카드 ID를 저장하는 Set
    private val registeredCardIds = mutableSetOf<String>()

    private val cardManagementService: CardManagementApiService =
        apiClient.authenticatedApi.create(CardManagementApiService::class.java)

    private val _ownedCards = MutableStateFlow<List<OwnedCard>>(emptyList())
    val ownedCards: StateFlow<List<OwnedCard>> = _ownedCards

    private val _registrationStatus = MutableStateFlow<String?>(null)
    val registrationStatus: StateFlow<String?> = _registrationStatus

    // 다이어로그 보이기
    private val _showDialog = MutableStateFlow<Boolean>(false)
    val showDialog: StateFlow<Boolean> = _showDialog.asStateFlow()

    // 등록 카드 리스트 상태
    private val _registeredCardState =
        MutableStateFlow<OnboardRegisteredCardState>(OnboardRegisteredCardState.Loading)
    val registeredCardState: StateFlow<OnboardRegisteredCardState> = _registeredCardState

    private val _cardDetail = MutableStateFlow<CardDetailResponse?>(null)
    val cardDetail: StateFlow<CardDetailResponse?> = _cardDetail

    private val _registeredCards = MutableStateFlow<List<RegisteredCard>>(emptyList())
    val registeredCards: StateFlow<List<RegisteredCard>> = _registeredCards.asStateFlow()

    private val _autoPaymentMessage = MutableStateFlow<String?>(null)
    val autoPaymentMessage: StateFlow<String?> = _autoPaymentMessage.asStateFlow()

    private var messageJob: Job? = null

    private var isFirstCardRegistration = true

    init {
        getBoardOwnedCards()
        getRegisteredCards()
    }

    // 내 소유의 카드 불러오기
    private fun getBoardOwnedCards() {
        viewModelScope.launch {
            _cardState.value = OnboardOwnedCardState.Loading
            try {
                val response = cardManagementService.getOwnedCards()
                if (response.isSuccessful) {
                    val cardList = response.body()?.items ?: emptyList()
                    _cardState.value = OnboardOwnedCardState.Success(cardList)
                    Log.d(TAG, "카드 목록 받아오기 성공: ${cardList.size} 개의 카드")
                    Log.d(TAG, "내 소유의 카드 목록 $cardList")
                } else {
                    _cardState.value =
                        OnboardOwnedCardState.Error("Failed to fetch cards: ${response.code()}")
                }
            } catch (e: Exception) {
                _cardState.value =
                    OnboardOwnedCardState.Error("Failed to fetch cards: ${e.message}")
            }
        }
    }
    // 등록된 카드 불러오기
    fun getRegisteredCards() {
        viewModelScope.launch {
            _registeredCardState.value = OnboardRegisteredCardState.Loading
            try {
                val response = cardManagementService.getRegistrationCards()
                if (response.isSuccessful) {
                    val cardList = response.body()?.items ?: emptyList()
                    _registeredCards.value = cardList
                    _registeredCardState.value = OnboardRegisteredCardState.Success(cardList)
                    Log.d(TAG, "등록된 카드 목록 받아오기 성공: ${cardList.size} 개의 카드")
                    Log.d(TAG, "등록된 카드 목록: ${response.body()}")
                } else {
                    _registeredCardState.value =
                        OnboardRegisteredCardState.Error("Failed to fetch cards: ${response.code()}")
                }
            } catch (e: Exception) {
                _registeredCardState.value =
                    OnboardRegisteredCardState.Error("Failed to fetch cards: ${e.message}")
            }
        }
    }

    // 등록된 카드가 있는지 확인
    private fun shouldSetAutoPayment(): Boolean {
        return registeredCards.value.isEmpty()
    }

    // 내 소유의 카드 중에서 사용할 카드 등록
    fun registerCards(cards: List<RegisterCardRequest>) {
        Log.d(TAG, "registerCards 호출")
        viewModelScope.launch {
            cards.forEachIndexed { index, cardInfo ->
                try {
                    val request = RegisterCardRequest(cardInfo.ownedCardId, cardInfo.oneTimeLimit)
                    val response = cardManagementService.registerCard(request)
                    if (response.isSuccessful) {
                        val registeredCard = response.body()
                        if (registeredCard != null) {
                            if (isFirstCardRegistration && index == 0) {
                                setAutoPaymentCard(cardInfo.ownedCardId, true)
                                isFirstCardRegistration = false
                            }
                        } else {
                            Log.d(TAG, "카드 등록 실패: ${response.code()}")
                            _registrationStatus.value = "카드 등록 실패: 응답 데이터 없음"
                        }
                    } else {
                        Log.d(TAG, "카드 등록 실패: ${response.code()}")
                        _registrationStatus.value = "카드 등록 실패: ${response.code()}"
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "카드 등록 실패 Exception")
                    _registrationStatus.value = "카드 등록 실패: ${e.message}"
                }
            }
            // 모든 카드 등록 후 등록된 카드 목록을 갱신
            getRegisteredCards()
            _registrationStatus.value = "카드가 성공적으로 등록되었습니다."
        }
    }

    // 카드 정보 불러오기
    fun loadCardDetail(cardId: Int) {
        viewModelScope.launch {
            try {
                val response = cardManagementService.getCardDetail(cardId)
                if (response.isSuccessful) {
                    _cardDetail.value = response.body()
                } else {
                    Log.e(TAG, "Failed to load card detail: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while loading card detail: ${e.message}")
            }
        }
    }

    // 카드 상세 정보 초기화
    fun clearCardDetail() {
        _cardDetail.value = null
    }

    // 자동 결제 등록
    fun setAutoPaymentCard(ownedCardId: Int, autoPayStatus: Boolean) {
        viewModelScope.launch {
            try {
                val request = AutoPaymentCardRequest(ownedCardId, autoPayStatus)
                val response = cardManagementService.registerAutoPaymentCard(request)
                if (response.isSuccessful) {
                    val previousAutoPayCard = _registeredCards.value.find { it.autoPayStatus }
                    _registeredCards.update { cards ->
                        cards.map { card ->
                            card.copy(autoPayStatus = card.ownedCardId == ownedCardId && autoPayStatus)
                        }
                    }

                    if (autoPayStatus) {
                        if (previousAutoPayCard != null && previousAutoPayCard.ownedCardId != ownedCardId) {
                            showAutoPaymentMessage("자동결제카드가 변경되었어요")
                        } else {
                            showAutoPaymentMessage("자동결제카드로 등록되었어요")
                        }
                    } else {
                        showAutoPaymentMessage("자동결제가 해제되었어요")
                    }

                    Log.d(TAG, "Auto payment card set successfully")
                } else {
                    Log.e(TAG, "Failed to set auto payment card: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while setting auto payment card: ${e.message}")
            }
        }
    }

    // 자동결제 등록을 했을 때 토스트 메세지 출력
    private fun showAutoPaymentMessage(message: String) {
        messageJob?.cancel()
        messageJob = viewModelScope.launch {
            _autoPaymentMessage.value = message
            delay(3000)
            _autoPaymentMessage.value = null
        }
    }

    // 등록되어 있는 카드인지 확인
    fun isCardRegistered(ownedCardId: Int): Boolean {
        return registeredCards.value.any { it.ownedCardId == ownedCardId }
    }

    fun setSelectedCards(cards: List<OwnedCard>) {
        _selectedCards.value = cards.toSet()
    }

    fun selectCard(card: OwnedCard) {
        _selectedCards.value = _selectedCards.value + card
        updateAllSelectedState()
    }

    fun unselectCard(card: OwnedCard) {
        _selectedCards.value = _selectedCards.value - card
        updateAllSelectedState()
    }

    fun toggleCardSelection(card: OwnedCard, isSelected: Boolean) {
        if (!isCardRegistered(card.id)) {
            _selectedCards.value = if (isSelected) {
                _selectedCards.value + card
            } else {
                _selectedCards.value - card
            }
            updateAllSelectedState()
        }
    }
    fun toggleAllSelected(isChecked: Boolean) {
        _isAllSelected.value = isChecked
        if (cardState.value is OnboardOwnedCardState.Success) {
            val selectableCards = (cardState.value as OnboardOwnedCardState.Success).cards.filter { !isCardRegistered(it.id) }
            _selectedCards.value = if (isChecked) selectableCards.toSet() else emptySet()
        }
    }
    fun isCardRegistered(cardId: String): Boolean {
        return cardId in registeredCardIds
    }
    private fun updateAllSelectedState() {
        if (cardState.value is OnboardOwnedCardState.Success) {
            val selectableCards = (cardState.value as OnboardOwnedCardState.Success).cards.filter { !isCardRegistered(it.id) }
            _isAllSelected.value = _selectedCards.value.size == selectableCards.size
        }
    }
}

sealed class OnboardOwnedCardState {
    object Loading : OnboardOwnedCardState()
    data class Success(val cards: List<OwnedCard>) : OnboardOwnedCardState()
    data class Error(val message: String) : OnboardOwnedCardState()
}

sealed class OnboardRegisteredCardState {
    object Loading : OnboardRegisteredCardState()
    data class Success(val cards: List<RegisteredCard>) : OnboardRegisteredCardState()
    data class Error(val message: String) : OnboardRegisteredCardState()
}
