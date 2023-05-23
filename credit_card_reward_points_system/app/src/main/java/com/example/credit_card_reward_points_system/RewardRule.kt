package com.example.credit_card_reward_points_system

data class RewardRule(
    val ruleName : String,
    val sportCheckRequired : Int,
    val timHortonsRequired : Int,
    val subwayRequired : Int,
    val rewardPoint : Int,
) {
    /*
    * All money required in RewardRule are in 5 dollars.
    * */
    val sportCheckNorm = sportCheckRequired / 5
    val timHortonsNorm = timHortonsRequired / 5
    val subwayNorm = subwayRequired / 5

    override fun toString(): String {
        return "{$ruleName: $sportCheckRequired, $timHortonsRequired, $subwayRequired ($rewardPoint)}"
    }
}