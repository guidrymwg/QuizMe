package com.lightcone.quizme;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by guidry on 8/13/16.
 */
public class AstroQA extends AppCompatActivity {

    public static final String TAG = "WEBSTREAM";

    // Questioner data holders
    private String qnum;
    protected static String question;
    protected static String answer[] = new String[5];
    protected static String chapter = "4";
    private TextView questionView;
    private Bundle postData;
    private RadioButton[] answerButton = new RadioButton[5];
    protected static String coran = null;
    protected static String amplification = null;
    protected static int selectedButton = -1;
    protected static String answerArray[] = {"A", "B", "C", "D", "E"};
    private Button submitButton;
    protected static int numberRight = 0;
    protected static int numberWrong = 0;
    private int numberQuestions = 0;
    protected static float score = 0;
    protected static boolean isCorrect;
    protected static int correctIndex = -1;
    protected static SharedPreferences prefs;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.astroqa);

        // Set up a TextView to hold the question
        questionView = (TextView)findViewById(R.id.TextView01);

        // Set up an array of RadioButtons for the five possible answers that are displayed
        answerButton[0] = (RadioButton) findViewById(R.id.answerA);
        answerButton[1] = (RadioButton) findViewById(R.id.answerB);
        answerButton[2] = (RadioButton) findViewById(R.id.answerC);
        answerButton[3] = (RadioButton) findViewById(R.id.answerD);
        answerButton[4] = (RadioButton) findViewById(R.id.answerE);

        // Add click listeners to the RadioButtons. Will process with inner class event_listener below
        for(int i=0; i<5; i++){
            answerButton[i].setOnClickListener(event_listener);
            answerButton[i].setVisibility(View.INVISIBLE);   // Hide buttons until question is displayed
        }

        // Set up the scoring button
        submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(event_listener);
        submitButton.setVisibility(View.INVISIBLE);

        // Set up a SharedPreferences to store scores so they will persist. Variable prefs
        // is protected static, so it can be accessed from other classes in this package.

        prefs = this.getApplicationContext().getSharedPreferences("prefs", 0);

        // Set up the name-value data pairs that will be transmitted as part of the POST request
        // as elements of a Bundle.

        postData = new Bundle();
        postData.putString("chapter", chapter);

        // Execute the POST request on a background thread
        progressBar = (ProgressBar) findViewById(R.id.qa_bar);
        new BackgroundLoad().execute(host_url);

    }

    @Override
    protected void onPause(){
        super.onPause();
        // To prevent navigation back to previous answer screen
        finish();
    }

    // Process button clicks for possible question answers
    private OnClickListener event_listener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            // Set int selectedButton to the index of the answer chosen if the choice
            // was one of the radio buttons, or execute the scoring method if the
            // submit button was pressed.

            switch(v.getId()){
                case R.id.answerA:
                    selectedButton = 0;
                    break;
                case R.id.answerB:
                    selectedButton = 1;
                    break;
                case R.id.answerC:
                    selectedButton = 2;
                    break;
                case R.id.answerD:
                    selectedButton = 3;
                    break;
                case R.id.answerE:
                    selectedButton = 4;
                    break;
                case R.id.submit_button:
                    processAnswer(selectedButton);
                    break;
            }

            Log.i(TAG,"Button "+selectedButton+" chosen");
        }
    };

    // Method to process and score answer
    private void processAnswer(int selectedButton){
        Log.i(TAG,"ProcessAnswer, selected button = "+selectedButton);

        // If no answer given, warn but do nothing
        if(selectedButton < 0){
            Toast.makeText(AstroQA.this, R.string.noAnswer, Toast.LENGTH_LONG).show();
            return;
        }

        // Process and score
        String ansS = answerArray[selectedButton];
        isCorrect = (selectedButton == correctIndex);

        // Retrieve current score parameters from shared preferences
        numberRight = prefs.getInt("numberRight", 0);
        numberWrong = prefs.getInt("numberWrong", 0);
        numberQuestions = prefs.getInt("numberQuestions", 0);
        score = prefs.getFloat("score",(float) 0.0);

        numberQuestions ++;
        if(isCorrect){
            numberRight ++;
        } else {
            numberWrong ++;
        }
        score = (float)numberRight/(float)numberQuestions;
        Log.i(TAG,"+++ coran="+coran+" ansS="+ansS+" isCorrect="+isCorrect
                +" right="+numberRight+" wrong="+numberWrong+" questions="+numberQuestions
                +" score="+(int)(score*100)+"%");

        // Store new values in shared preferences
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("numberRight", numberRight);
        edit.putInt("numberWrong", numberWrong);
        edit.putInt("numberQuestions", numberQuestions);
        edit.putFloat("score", score);
        edit.commit();

        // Define an Intent to launch an answer screen
        Intent i = new Intent(this, AnswerScreen.class);
        startActivity(i);
    }


}
