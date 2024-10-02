data class Card(
    val id: Int,
    val cardNo: String,         // 카드 번호
    val cvc: String,            // CVC 코드
    val withdrawalDate: String, // 출금일
    val cardExpiryDate: String, // 카드 만료일
    val created: String,        // 생성일
    val mobiUserId: Int,        // 사용자 ID
    val accountId: Int,         // 계좌 ID
    val cardUniqueNo: String       // 카드 고유 번호
)

data class CardListResponse(
    val items: List<Card>       // 카드 목록
)