package eu.hanna.workoutappdemo

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_exercise.*
import kotlinx.android.synthetic.main.dialog_custom_back_confirmation.*
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var restTimer: CountDownTimer ?= null // Variable for Rest Timer and later on we will initialize it.
    private var restProgress = 1 //  Variable for timer progress. As initial value the rest progress is set to 0. As we are about to start.
    private var restTimerDuration: Long = 10 // change to 10

    private var exerciseTimer: CountDownTimer ?= null
    private var exerciseProgress = 1
    private var exerciseTimerDuration: Long = 30 // change to 30

    //The Variable for the exercise list and current position of exercise here it is -1 as the list starting element is 0.
    private var exerciseList:ArrayList<ExerciseModel>?= null
    private var currentExercisePosition = -1

    // Variable for TextToSpeech
    private var testToSpeak: TextToSpeech? = null

    private var player: MediaPlayer? = null

    // Declaring a variable of an adapter class to bind it to recycler view
    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        // Setting up the action bar using the toolbar and adding a back arrow button to it
        setSupportActionBar(toolbar_exercise_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_exercise_activity.setNavigationOnClickListener {
           // onBackPressed()
            customDialogForBackButton()
        }
        // Initialize the Text to Speech
        testToSpeak = TextToSpeech(this,this)

        // Initializing and Assigning a default exercise list to our list variable
        exerciseList = Constants.defaultExerciseList()


        setupRestView()

        // call function of where we have bound the adaper to recycler view to show the data in the UI
        setupExerciseStatusRecyclerView()
    }


    // - Setting up the 10 seconds timer for rest view and updating it continuously.)-->
    //START
    /**
     * Function is used to set the progress of timer using the progress
     */
    private fun setRestProgressBar () {
        progressBar.progress = restProgress // Sets the current progress to the specified value.
        // Here we have started a timer of 10 seconds so the 10000 is milliseconds is 10 seconds and the countdown interval is 1 second so it 1000.
        restTimer = object : CountDownTimer(restTimerDuration * 1000, 1000)  {
            override fun onTick(p0: Long) {
                restProgress ++ // It is increased by 1
                progressBar.progress = 10 - restProgress // Indicates progress bar progress
                tvTimer.text = (10 - restProgress).toString() // Current progress is set to text view in terms of seconds.

            }
            override fun onFinish() {
               // Increasing the current position of the exercise after rest view
               currentExercisePosition++

                //When we are getting an updated position of exercise set that item in the list as selected and notify the adapter class.)
                exerciseList!![currentExercisePosition].setIsSelected(true)   // Current Item is selected
                exerciseAdapter!!.notifyDataSetChanged() //Notified the current item to adapter class to reflect it into UI.

               setupExerciseView()
            }
        }.start()
    }
    private fun setExerciseProgressBar () {
        progressBarExercise.progress = exerciseProgress // Sets the current progress to the specified value.
        // Here we have started a timer of 10 seconds so the 10000 is milliseconds is 10 seconds and the countdown interval is 1 second so it 1000.
        exerciseTimer = object : CountDownTimer(exerciseTimerDuration * 1000, 1000)  {
            override fun onTick(p0: Long) {
                exerciseProgress ++ // It is increased by 1
                progressBarExercise.progress = 30 - exerciseProgress // Indicates progress bar progress
                tvExerciseTimer.text = (30 - exerciseProgress).toString() // Current progress is set to text view in terms of seconds.

            }

            override fun onFinish() {



                // When the 10 seconds will complete this will be executed.
               // Toast.makeText(this@ExerciseActivity, "Here now we will start the exercise.", Toast.LENGTH_SHORT).show()
                // Updating the view after completing the 30 seconds exercise
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    // We have changed the status of the selected item and updated the status of that, so that the position is set as completed in the exercise list.)
                    exerciseList!![currentExercisePosition].setIsSelected(false) // exercise is completed so selection is set to false
                    exerciseList!![currentExercisePosition].setIsCompleted(true) // updating in the list that this exercise is completed
                    exerciseAdapter!!.notifyDataSetChanged()  // Notifying the adapter class.
                    /*llRestView.visibility = View.VISIBLE
                    llExerciseView.visibility = View.GONE*/
                    setupRestView()
                } else {
                    finish()
                    //Toast.makeText(this@ExerciseActivity, "Congratulations! You have completed the 7 minutes workout.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

    // Setting up the Get Ready View with 10 seconds of timer.)-->
    /**
     * Function is used to set the timer for REST.
     */
    private fun setupRestView () {
        // Playing a notification sound when the exercise is about to start when you are in the rest state the sound file is added in the raw folder as resource
        try {
            val soundURI = Uri.parse("android.resource://com.sevenminuteworkout/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, R.raw.press_start)
            player!!.isLooping = false // Sets the player to be looping or non-looping.
            player!!.start() // Starts Playback.
        } catch (e: Exception) {
            e.printStackTrace()
        }
        llRestView.visibility = View.VISIBLE
        llExerciseView.visibility = View.GONE
        /**
         * Here firstly we will check if the timer is running the and it is not null then cancel the running timer and start the new one.
         * And set the progress to initial which is 0.
         */
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
            tvUpcomingExerciseName.text = exerciseList!![currentExercisePosition+1].getName()

        // This function is used to set the progress details.
        setRestProgressBar()
    }

    // Setting up the Exercise View with a 30 seconds timer.)
    /**
     * Function is used to set the progress of the timer using the progress for Exercise View.
     */
    private fun setupExerciseView () {
    // Here according to the view make it visible as this is Exercise View so exercise view is visible and rest view is not.
        llRestView.visibility = View.GONE
        llExerciseView.visibility= View.VISIBLE
        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        // Setting up the current exercise name and image to view to the UI element.
        ivImage.setImageResource(exerciseList!![currentExercisePosition].getImage())
        tvExerciseName.text = exerciseList!![currentExercisePosition].getName()
        speakOut(tvExerciseName.text.toString())

       setExerciseProgressBar()
    }

    // Destory the timer when closing the activity.Here in the Destroy function we will reset the rest timer if it is running.
    public override fun onDestroy() {
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }
        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }
        if (testToSpeak!=null) {
            testToSpeak!!.stop()
            testToSpeak!!.shutdown()
        }
        if(player != null){
            player!!.stop()
        }
        super.onDestroy()
    }

//Called to signal the completion of the TextToSpeech engine initialization.
    override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
        // set US English as language for tts
        val result = testToSpeak!!.setLanguage(Locale.US)

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "The Language specified is not supported!")
        }

    } else {
        Log.e("TTS", "Initialization Failed!")
    }
    }

    // Function to speak the text
    private fun speakOut (text:String) {
        testToSpeak!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }

    // Function is used to set up the recycler view to UI and  asigning the Layout Manager and Adaper calss is attached to it
    // Binding adapter class to recycler view and setting the recycler view layout manager and passing a list to the adapter.)
    private fun setupExerciseStatusRecyclerView () {
        // used a LinearLayout Manager with horizontal scroll
        rvExerciseStatus.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        // As the adapter expects the exercises list and context so initialize it passing it
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!,this)

        // Adapter class is attached to recycler view
        rvExerciseStatus.adapter = exerciseAdapter
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog (this)
        customDialog.setContentView(R.layout.dialog_custom_back_confirmation)
        customDialog.tvYes.setOnClickListener {
            finish()
            customDialog.dismiss()
        }
        customDialog.tvNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }
}