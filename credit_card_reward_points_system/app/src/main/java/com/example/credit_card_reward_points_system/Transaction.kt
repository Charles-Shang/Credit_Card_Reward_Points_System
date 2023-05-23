package com.example.credit_card_reward_points_system


data class Transaction(
    val id: String,
    val date: String,
    val merchantCode: String,
    val amountCents: Int,
    private val model: MainActivityViewModel
) {
    var backgroundColor = "#FFFFFF"
    var maxRewardPoint = 0

    init {
        backgroundColor = when(merchantCode) {
            "" -> "#FFFFFF"
            "sportcheck" -> "#FFD700"
            "tim_hortons" -> "#F08080"
            "subway" -> "#90EE90"
            else -> "#C0C0C0"
        }
    }

}