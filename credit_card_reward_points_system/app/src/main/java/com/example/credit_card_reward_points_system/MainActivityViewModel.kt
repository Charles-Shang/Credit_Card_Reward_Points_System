package com.example.credit_card_reward_points_system

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    val transactionList: MutableLiveData<MutableList<Transaction>> = MutableLiveData<MutableList<Transaction>>()
    private val metaDataTransactionList: MutableList<Transaction> = mutableListOf<Transaction>()
    var selectedMetaDataTransactionList: MutableList<Transaction> = mutableListOf<Transaction>()
    var maxRewardPointList: MutableList<Int> = mutableListOf()

    var totalAmount = 0
    var sportCheckAmount = 0
    var timHortonsAmount = 0
    var subwayAmount = 0
    var otherAmount = 0

    var totalPoint = 0

    val rewardRuleList = listOf<RewardRule>(
        RewardRule("Rule1", 75, 25, 25, 500),
        RewardRule("Rule2", 75, 25, 0, 300),
        RewardRule("Rule4", 25, 10, 10, 150),
        RewardRule("Rule6", 20, 0, 0, 75),
    ) // Rule 7 is not considered in this list for optimization of reward points

    var ruleAppliedNums = mutableListOf<Int>(0, 0, 0, 0, 0)
    var rule7AppliedNum = 0

    var metadataCurTransaction = """
        {
        "T01": {"date": "2021-05-01", "merchant_code" : "sportcheck", "amount_cents": 21000},
        "T02": {"date": "2021-05-02", "merchant_code" : "sportcheck", "amount_cents": 8700},
        "T03": {"date": "2021-05-03", "merchant_code" : "tim_hortons", "amount_cents": 323},
        "T04": {"date": "2021-05-04", "merchant_code" : "tim_hortons", "amount_cents": 1267},
        "T05": {"date": "2021-05-05", "merchant_code" : "tim_hortons", "amount_cents": 2116},
        "T06": {"date": "2021-05-06", "merchant_code" : "tim_hortons", "amount_cents": 2211},
        "T07": {"date": "2021-05-07", "merchant_code" : "subway", "amount_cents": 1853},
        "T08": {"date": "2021-05-08", "merchant_code" : "subway", "amount_cents": 2153},
        "T09": {"date": "2021-05-09", "merchant_code" : "sportcheck", "amount_cents": 7326},
        "T10": {"date": "2021-05-10", "merchant_code" : "tim_hortons", "amount_cents": 1321}
        }
    """.trimIndent()
    var curTransaction: MutableLiveData<String> = MutableLiveData<String>()

    init {
        // Initialize a series of transactions for testing and later we change to json text parsing
//        metaDataTransactionList.add(Transaction("T01", "2021-05-01", "sportcheck", 21000, this))
//        metaDataTransactionList.add(Transaction("T02", "2021-05-02", "sportcheck", 8700, this))
//        metaDataTransactionList.add(Transaction("T03", "2021-05-03", "tim_hortons", 323, this))
//        metaDataTransactionList.add(Transaction("T04", "2021-05-04", "tim_hortons", 1267, this))
//        metaDataTransactionList.add(Transaction("T05", "2021-05-05", "tim_hortons", 2116, this))
//        metaDataTransactionList.add(Transaction("T06", "2021-05-06", "tim_hortons", 2211, this))
//        metaDataTransactionList.add(Transaction("T07", "2021-05-07", "subway", 1853, this))
//        metaDataTransactionList.add(Transaction("T08", "2021-05-08", "subway", 2153, this))
//        metaDataTransactionList.add(Transaction("T09", "2021-05-09", "sportcheck", 7326, this))
//        metaDataTransactionList.add(Transaction("T10", "2021-05-10", "tim_hortons", 1321, this))
        updateTransaction(metadataCurTransaction)

    }

    /*
    * Refresh all meta data and perform appropriate calculations
    * */
    private fun refreshData() {
        selectedMetaDataTransactionList = metaDataTransactionList.sortedBy { it.id }.toMutableList()

        totalAmount = 0
        sportCheckAmount = 0
        timHortonsAmount = 0
        subwayAmount = 0
        otherAmount = 0

        selectedMetaDataTransactionList.forEach {
            when (it.merchantCode) {
                "sportcheck" -> sportCheckAmount += it.amountCents
                "tim_hortons" -> timHortonsAmount += it.amountCents
                "subway" -> subwayAmount += it.amountCents
                else -> otherAmount += it.amountCents
            }

            val rule6Num = it.amountCents / 2000
            if(it.merchantCode == "sportcheck") {
                it.maxRewardPoint = rewardRuleList[3].rewardPoint * rule6Num + (it.amountCents % 2000) / 100
            } else {
                it.maxRewardPoint = it.amountCents / 100
            }

        }

        totalAmount = sportCheckAmount + timHortonsAmount + subwayAmount + otherAmount

        calculateRewardPoints()

        selectedMetaDataTransactionList.add(Transaction("", "", "", -1, this))
        transactionList.value = selectedMetaDataTransactionList
    }

    /*
    * Calculate the max reward points the customer can obtain
    * */
    private fun calculateRewardPoints() {
        totalPoint = 0
        ruleAppliedNums = mutableListOf<Int>(0, 0, 0, 0, 0)
        rule7AppliedNum = 0

        val sportCheckNormDollar = sportCheckAmount / 500
        val timHortonsNormDollar = timHortonsAmount / 500
        val subwayNormDollar = subwayAmount / 500

        /*
        * opt[sc][th][su] defines a list of [# of rule1 applied, # of rule2 applied, # of rule4 applied, # of rule6 applied, max reward points]
        * Note that we only have 4 rules, opt[sc][th][su][4] used to store max reward points
        * */
        val opt = List(sportCheckNormDollar + 1) {
            List(timHortonsNormDollar + 1) {
                List(subwayNormDollar + 1) {
                    MutableList(rewardRuleList.size + 1) { 0 }
                }
            }
        }

        // we track the max reward points with optimal argument
        var maxPoints = 0
        var sportCheckMaxPoints = 0
        var timHortonsMaxPoints = 0
        var subwayMaxPoints = 0

        for ((ruleIndex, rule) in rewardRuleList.withIndex()) {
            for (sc in 0..sportCheckNormDollar) {
                for (th in 0..timHortonsNormDollar) {
                    for (su in 0..subwayNormDollar) {
                        // Only try to apply the rule when the requirement satisfies and we achieve higher reward points
                        if (sc >= rule.sportCheckNorm &&
                            th >= rule.timHortonsNorm &&
                            su >= rule.subwayNorm &&
                            opt[sc][th][su][4] < opt[sc - rule.sportCheckNorm][th - rule.timHortonsNorm][su - rule.subwayNorm][4] + rule.rewardPoint
                        ) {
                            opt[sc][th][su][4] =
                                opt[sc - rule.sportCheckNorm][th - rule.timHortonsNorm][su - rule.subwayNorm][4] + rule.rewardPoint
                            opt[sc][th][su][ruleIndex] =
                                opt[sc - rule.sportCheckNorm][th - rule.timHortonsNorm][su - rule.subwayNorm][ruleIndex] + 1
                        }

                        if (opt[sc][th][su][4] > maxPoints) {
                            maxPoints = opt[sc][th][su][4]
                            sportCheckMaxPoints = sc
                            timHortonsMaxPoints = th
                            subwayMaxPoints = su
                        }
                    }
                }
            }
        }


        val optimalSol = opt[sportCheckMaxPoints][timHortonsMaxPoints][subwayMaxPoints]
        ruleAppliedNums = mutableListOf(optimalSol[0], optimalSol[1], optimalSol[2], optimalSol[3])
        val usedAmount = mutableListOf(sportCheckMaxPoints, timHortonsMaxPoints, subwayMaxPoints)

        // All remaining dollars will be applied by rule 7
        rule7AppliedNum = (totalAmount - usedAmount.sum() * 500) / 100

        totalPoint = optimalSol[4] + rule7AppliedNum

    }

    /*
    * Parse the json text passed and create corresponding Transactions
    * */
    fun updateTransaction(jsonInput: String) {
        metaDataTransactionList.clear()
        Regex("""\".*\": \{.*\}""").findAll(jsonInput).forEach { it ->
            var allEntries =
                it.value.split(Regex("[:,]"))
                    .map { str -> str.replace(Regex("[\" {}]"), "") }.toMutableList()
            metaDataTransactionList.add(
                Transaction(
                    allEntries[0],
                    allEntries[2],
                    allEntries[4],
                    allEntries[6].toInt(),
                    this
                )
            )
        }

        metadataCurTransaction = jsonInput
        curTransaction.value = metadataCurTransaction
        refreshData()

    }
}