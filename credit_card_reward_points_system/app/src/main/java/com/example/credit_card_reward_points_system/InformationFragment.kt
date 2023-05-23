package com.example.credit_card_reward_points_system

import android.annotation.SuppressLint
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

class InformationFragment : Fragment() {

    private val viewModel : MainActivityViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.information_fragment, container, false)

        val informationDetail = """
            * Rule 1: 500 points for every $75 spend at Sport Check, $25 spend at Tim Hortons and $25 spend at Subway<br><br>
            * Rule 2: 300 points for every $75 spend at Sport Check and $25 spend at Tim Hortons<br><br>
            * Rule 3: 200 points for every $75 spend at Sport Check<br><br>
            * Rule 4: 150 points for every $25 spend at Sport Check, $10 spend at Tim Hortons and $10 spend at Subway<br><br>
            * Rule 5: 75 points for every $25 spend at Sport Check and $10 spend at Tim Hortons<br><br>
            * Rule 6: 75 point for every $20 spend at Sport Check<br><br>
            * Rule 7: 1 points for every $1 spend for all other purchases (including leftover amount)
        """.trimIndent()

        root.findViewById<TextView>(R.id.ruleText).text = Html.fromHtml(informationDetail, HtmlCompat.FROM_HTML_MODE_LEGACY)

        root.findViewById<FloatingActionButton>(R.id.goToMainButton).apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_informationFragment_to_viewRewardPointFragment)
            }
        }

        return root
    }
}