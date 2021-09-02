package com.luck.vullkleprikon.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.luck.vullkleprikon.R
import com.luck.vullkleprikon.activities.SmsActivity
import com.luck.vullkleprikon.utils.AUTORISATION
import com.luck.vullkleprikon.utils.IS_MODERATOR
import com.luck.vullkleprikon.utils.OnFragmentClosed
import com.luck.vullkleprikon.utils.setSharedBoolean
import kotlinx.android.synthetic.main.fragment_jackpot.*

class JackpotFragment (private val onFragmentClosed: OnFragmentClosed): Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_jackpot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ImageButton>(R.id.next_btn).setOnClickListener {
            setSharedBoolean(AUTORISATION, false, view.context)
            startActivity(Intent(requireContext(), SmsActivity::class.java))
            requireActivity().finish()
        }

        close.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            onFragmentClosed.onFragmentClose()
        }
    }
}