package com.rmd.media.ml.tf.facedetection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.rmd.media.ml.tf.facedetection.databinding.FragmentResultdialogBinding


class ResultDialog : DialogFragment() {

    private lateinit var binding: FragmentResultdialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_resultdialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentResultdialogBinding.bind(view)

        val bundle = arguments
        val resultText = bundle!!.getString(LCOFaceDetection.RESULT_TEXT).toString()

        binding.resultTextView.text = resultText

        binding.resultOkButton.setOnClickListener { dismiss() }
    }
}
