package com.luck.vullkleprikon.activities

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random
import android.util.DisplayMetrics
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnLayout
import com.luck.vullkleprikon.R
import com.luck.vullkleprikon.custom_view.TutorialView
import com.luck.vullkleprikon.fragment.HelpFragment
import com.luck.vullkleprikon.fragment.JackpotFragment
import com.luck.vullkleprikon.image_view_scrolling.Common
import com.luck.vullkleprikon.image_view_scrolling.IEventEnd
import com.luck.vullkleprikon.image_view_scrolling.Util
import com.luck.vullkleprikon.pop_up.CustomPopUp
import com.luck.vullkleprikon.recycler_view.OnMenuItemClicked
import com.luck.vullkleprikon.utils.*
import com.luck.vullkleprikon.view_model.MusicViewModel


class MainActivity : AppCompatActivity(), IEventEnd, OnMenuItemClicked, OnTutorialViewClicked,
    OnFragmentClosed {

    private var countDown = 0
    private lateinit var media: MediaPlayer
    private lateinit var sound: MediaPlayer
    private lateinit var stakeSound: MediaPlayer
    private lateinit var musicViewModel: MusicViewModel
    private var isMusicPlaying = true
    private var isSoundPlaying = true
    private var isTutorialPassed = true
    private var count = 0
    private var firstClick = false
    private lateinit var tutorial: TutorialView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        musicViewModel = ViewModelProvider(this).get(MusicViewModel::class.java)

        musicViewModel.isMusicPlaying.observe(this, Observer {
            isMusicPlaying = it
            playMusic()
        })

        musicViewModel.isSoundPlaying.observe(this, {
            isSoundPlaying = it
        })

        initMediaPlayers()

        image1.setEventEnd(this)
        image2.setEventEnd(this)
        image3.setEventEnd(this)

        spin.setOnClickListener {
            if (isTutorialPassed) {
                count++
                setSharedBoolean(TUTORIAL_PASSED, false, this)
                isTutorialPassed = false
                constraint.removeView(tutorial)
                constraint.removeView(tutorialText)
                stake.isClickable = true
//                help.isClickable = true
                Handler(Looper.myLooper()!!).postDelayed({
                    if (!firstClick){
                        spinClick()
                    }
                }, 6000)

            } else if (Common.SCORE - Common.STAKE < 0) {
                Toast.makeText(this, "Недостаточно денег", Toast.LENGTH_SHORT).show()
            } else {
                firstClick = true
                spinClick()
                spin.isClickable = false
                if (isSoundPlaying) {
                    sound.start()
                }
            }
        }

        stake.setOnClickListener {
            CustomPopUp(this).showPopUpMenu(stake)
        }

//        help.setOnClickListener {
//            helpClick()
//        }
    }

    private fun initMediaPlayers() {
        media = MediaPlayer.create(this, R.raw.background_music)
        media.setVolume(20f, 20f)
        media.isLooping = true

        stakeSound = MediaPlayer.create(this, R.raw.coin_insert)
        stakeSound.setVolume(100f, 100f)

        sound = MediaPlayer.create(this, R.raw.slots_sound)
        sound.setVolume(100f, 100f)
        sound.isLooping = true

        isMusicPlaying = getSharedBoolean(MUSIC, this)
        isSoundPlaying = getSharedBoolean(SOUND, this)
    }

    private fun getViewTutorial(view: View) {
        getScreen()
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        val x = location[0]
        val y = location[1]

        tutorial.getCoordinates(
            x.toFloat(),
            view.width.toFloat(),
            y.toFloat(),
            view.height.toFloat(),
        )
    }

    override fun onStart() {
        super.onStart()
        score.doOnLayout {
            if (isTutorialPassed) {
                createTutView()
                tutorialText.bringToFront()
                getViewTutorial(score)
                startAnimation()
                spin.isClickable = false
                stake.isClickable = false
//                help.isClickable = false
                tutorialText.text = "Актуальный баланс"
            }

        }
    }

    private fun startAnimation() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.text_animation)
        tutorialText.startAnimation(animation)
    }


    private fun removeTutView() {
        constraint.removeView(tutorial)
    }

    private fun createTutView() {
        tutorial = TutorialView(this, null)
        tutorial.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        constraint.addView(tutorial)
    }

    private fun getScreen() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        tutorial.getScreen(height, width, this)
    }

    override fun eventEnd(result: Int, count: Int) {
        if (getSharedBoolean(IS_FIRST, this)) {
            if (!isTutorialPassed && countDown == 2) {
                this.count++
                if (getSharedBoolean(IS_MODERATOR, this))
                    if (this.count == 4) {
                        supportFragmentManager.beginTransaction().apply {
                            Handler(Looper.myLooper()!!).postDelayed({
                                spin.isClickable = false
                                stake.isClickable = false
//                                help.isClickable = false
                                setSharedBoolean(IS_MODERATOR, false, this@MainActivity)
                                replace(R.id.fram, JackpotFragment(this@MainActivity))
                                commit()
                                setSharedBoolean(IS_FIRST, false, this@MainActivity)
                            }, 500)
                        }
                    }
            }
        }
        if (countDown < 2)
            countDown++
        else {
            val first = image1.value
            val second = image2.value
            val third = image3.value
            countDown = 0
            if (first == second && second == third && (first == Util.leprichaun || first == Util.hat || first == Util.gold || first == Util.podkova)) {
                val multiplayer: Int = when (Common.STAKE) {
                    5 -> 3
                    25 -> 6
                    50 -> 15
                    else -> 40
                }
                Common.SCORE += Common.STAKE * multiplayer
                score.text = "Баланс: ${Common.SCORE}"
            } else if (Common.SCORE < 5) {
                startActivity(Intent(this, EndActivity::class.java))
                finish()
            }
            Common.STAKE = 5
            stake.text = "Ставка: ${Common.STAKE}"
            spin.isClickable = true
            if (sound.isPlaying)
                sound.pause()
        }
    }

    override fun onItemClick(money: Int) {
        Common.STAKE = money
        stake.text = "Ставка: ${Common.STAKE}"
        if (isSoundPlaying) {
            stakeSound.start()
        }
    }

    override fun onPopUpClose() {
        if (isTutorialPassed) {
            tutRemoveAdd()
            count++
            getViewTutorial(spin)
            tutorialText.text = "Нажмите чтобы начать игру"
            startAnimation()
            stake.isClickable = false
            spin.isClickable = true
        }
    }

    private fun spinClick() {
        if (Common.SCORE >= 5) {
            image1.setValueRandom(
                Random.nextInt(4), //because we have 4 pictures
                Random.nextInt(15 - 5 + 1) + 5
            )//we will get random rotate from 5~15
            image2.setValueRandom(
                Random.nextInt(4), //because we have 4 pictures
                Random.nextInt(15 - 5 + 1) + 5
            )//we will get random rotate from 5~15
            image3.setValueRandom(
                Random.nextInt(4), //because we have 4 pictures
                Random.nextInt(15 - 5 + 1) + 5
            )//we will get random rotate from 5~15

            Common.SCORE -= Common.STAKE
            score.text = "Баланс: ${Common.SCORE}"
        }
    }

    private fun helpClick() {
        spin.isClickable = false
        stake.isClickable = false
//        help.isClickable = false
        if (isTutorialPassed)
            fram.bringToFront()
        supportFragmentManager.beginTransaction().apply {
            replace(
                R.id.fram,
                HelpFragment.newInstance(isMusicPlaying, isSoundPlaying, this@MainActivity)
            )
            commit()
        }
    }

    override fun onStop() {
        super.onStop()
        setScore(SCORE, Common.SCORE, this)
        setSharedBoolean(MUSIC, isMusicPlaying, this)
        setSharedBoolean(SOUND, isSoundPlaying, this)
    }

    override fun onResume() {
        super.onResume()
        isTutorialPassed = getSharedBoolean(TUTORIAL_PASSED, this)
        Common.SCORE = getScore(this)
        score.text = "Баланс: ${Common.SCORE}"
        playMusic()
        if (Common.SCORE < 5) {
            startActivity(Intent(this, EndActivity::class.java))
            finish()
        }
    }

    private fun playMusic() {
        if (isMusicPlaying) {
            media.start()
        } else if (media.isPlaying) {
            media.pause()
        }
    }

    override fun onDestroy() {
        media.stop();
        media.release();
        sound.release()
        stakeSound.release()
        super.onDestroy()
    }

    override fun onPause() {
        media.pause()
        super.onPause()
    }

    override fun onTutorialViewClick() {
        when (count) {
            0 -> {
                tutRemoveAdd()
                count++
                getViewTutorial(stake)
                tutorialText.text = "Увеличить ставку"
                startAnimation()
//                tutRemoveAdd()
//                count++
//                getViewTutorial(help)
//                tutorialText.text = "Здесь ты можешь поменять настройки"
//                help.isClickable = true
//                startAnimation()

            }
            1 -> {
                tutRemoveAdd()
                count++
                getViewTutorial(spin)
                tutorialText.text = "Нажмите чтобы начать игру"
                startAnimation()
                stake.isClickable = false
                spin.isClickable = true
            }
        }
    }

    private fun tutRemoveAdd() {
        removeTutView()
        createTutView()
        tutorialText.bringToFront()
    }

    override fun onFragmentClose() {
        if (isTutorialPassed) {
            tutRemoveAdd()
            count++
            getViewTutorial(stake)
            tutorialText.text = "Увеличить ставку"
            startAnimation()
//            help.isClickable = false
            stake.isClickable = true
        } else {
            spin.isClickable = true
            stake.isClickable = true
//            help.isClickable = true
        }
    }
}