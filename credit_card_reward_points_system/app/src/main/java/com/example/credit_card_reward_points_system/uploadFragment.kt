package com.example.credit_card_reward_points_system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

class uploadFragment : Fragment() {
    private val viewModel : MainActivityViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.upload_transaction, container, false)

        viewModel.curTransaction.observe(viewLifecycleOwner) {
            root.findViewById<EditText>(R.id.input_transaction).setText(viewModel.metadataCurTransaction, TextView.BufferType.EDITABLE)
        }

        root.findViewById<FloatingActionButton>(R.id.uploadPageGoToMainButton).apply {
            setOnClickListener {
                viewModel.updateTransaction(root.findViewById<EditText>(R.id.input_transaction).text.toString())
                findNavController().navigate(R.id.action_uploadFragment_to_viewRewardPointFragment)
            }
        }

        return root
    }
}