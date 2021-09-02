package com.luck.vullkleprikon.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import com.luck.vullkleprikon.R
import com.luck.vullkleprikon.custom_text_view.GradientTextView
import com.luck.vullkleprikon.utils.OnFragmentClosed
import com.luck.vullkleprikon.view_model.MusicViewModel

class HelpFragment(private val onFragmentClosed: OnFragmentClosed) : Fragment() {

    companion object{
        @JvmStatic
        fun newInstance(music: Boolean, sound: Boolean, onFragmentClosed: OnFragmentClosed) =
            HelpFragment(onFragmentClosed).apply {
                arguments = Bundle().apply {
                    putBoolean("IS_PLAYING_MUSIC", music)
                    putBoolean("IS_PLAYING_SOUND", sound)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isPlayingMusic = it.getBoolean("IS_PLAYING_MUSIC")
            isPlayingSound = it.getBoolean("IS_PLAYING_SOUND")
        }
    }

    private lateinit var close: AppCompatButton
    private lateinit var music: SwitchCompat
    private lateinit var sound: SwitchCompat
    private lateinit var combo: GradientTextView
    private lateinit var musicViewModel: MusicViewModel
    private var isPlayingMusic = true
    private var isPlayingSound = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        close = view.findViewById(R.id.close)
        music = view.findViewById(R.id.music_btn)
        sound = view.findViewById(R.id.sound_btn)
        combo = view.findViewById(R.id.combo)
        musicViewModel = ViewModelProvider(requireActivity()).get(MusicViewModel::class.java)
    }


    override fun onStart() {
        super.onStart()
        music.isChecked = isPlayingMusic
        sound.isChecked = isPlayingSound
        combo.setColors(R.color.yellow, R.color.orange)
        close.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
            onFragmentClosed.onFragmentClose()
        }
        music.setOnCheckedChangeListener { _, isChecked ->
            musicViewModel.isMusicPlaying.value = isChecked
        }
        sound.setOnCheckedChangeListener { _, isChecked ->
            musicViewModel.isSoundPlaying.value = isChecked
        }
    }
}