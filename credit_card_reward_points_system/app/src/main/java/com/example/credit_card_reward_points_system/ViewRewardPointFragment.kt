package com.example.credit_card_reward_points_system

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.*


class ViewRewardPointFragment : Fragment() {

    private val viewModel: MainActivityViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.view_reward_point_fragment, container, false)

        // when our transaction list changes, we re-display the calculated information
        viewModel.transactionList.observe(viewLifecycleOwner) {
            val transactionListLayout = root.findViewById<LinearLayout>(R.id.transactionListContainer)
            transactionListLayout.removeAllViews()
            viewModel.selectedMetaDataTransactionList.forEach {
                val transactionEntry = inflater.inflate(R.layout.transaction_entry_view, container, false).apply {
                    if (it.amountCents == -1) {
                        findViewById<TextView>(R.id.transactionID).text = ""
                        findViewById<TextView>(R.id.transactionDate).text = ""
                        findViewById<TextView>(R.id.transactionMerchantCode).text = ""
                        findViewById<TextView>(R.id.transactionAmountCents).text = ""
                        setBackgroundColor(Color.parseColor(it.backgroundColor))
                    } else {
                        findViewById<TextView>(R.id.transactionID).text = it.id
                        findViewById<TextView>(R.id.transactionDate).text = it.date
                        findViewById<TextView>(R.id.transactionMerchantCode).text = it.merchantCode
                        findViewById<TextView>(R.id.transactionAmountCents).text = Html.fromHtml("¢${it.amountCents}(<font color=\"red\">${it.maxRewardPoint}</font>)", HtmlCompat.FROM_HTML_MODE_LEGACY)
                        setBackgroundColor(Color.parseColor(it.backgroundColor))
                    }
                }

                transactionListLayout.addView(transactionEntry)
            }


            var rewardDetail = ""
            viewModel.ruleAppliedNums.zip(viewModel.rewardRuleList).forEach { (appliedNum, rule) ->
                if (appliedNum == 1) {
                    rewardDetail += "- ${rule.ruleName}: $appliedNum time<br>"
                } else if (appliedNum > 1) {
                    rewardDetail += "- ${rule.ruleName}: $appliedNum times<br>"
                }
            }

            if (viewModel.rule7AppliedNum == 0) {
                rewardDetail += "- ${"Rule7"}: ${viewModel.rule7AppliedNum} time<br>"
            } else if (viewModel.rule7AppliedNum > 0) {
                rewardDetail += "- ${"Rule7"}: ${viewModel.rule7AppliedNum} times<br>"
            }

            var calculationDetail = """
                Total Reward Points: <font color="red"><b>${viewModel.totalPoint}</b></font><br>
                Reward Details:<br>
                $rewardDetail<br>
                Total Amount: ¢${viewModel.totalAmount}<br>
                Sport Check: ¢${viewModel.sportCheckAmount}<br>
                Tim Hortons: ¢${viewModel.timHortonsAmount}<br>
                Subway: ¢${viewModel.subwayAmount}<br>
                Other: ¢${viewModel.otherAmount}<br>
            """.trimIndent()

            root.findViewById<TextView>(R.id.calculationInfoLabel).text =
                Html.fromHtml(calculationDetail, HtmlCompat.FROM_HTML_MODE_LEGACY)

            root.findViewById<FloatingActionButton>(R.id.uploadTransactionFileFloatingButton).apply {
                setOnClickListener {
                    findNavController().navigate(R.id.action_viewRewardPointFragment_to_uploadFragment)
                }
            }
        }

        root.findViewById<FloatingActionButton>(R.id.goToInformationButton).apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_viewRewardPointFragment_to_informationFragment)
            }
        }


        return root
    }
}