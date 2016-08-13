package com.lightcone.quizme;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

public class AnswerScreen extends Activity {

    public static final String TAG = "WEBSTREAM";
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private int numberRight;
    private int numberWrong;
    private int score;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answerscreen);
        numberRight = AstroQA.numberRight;
        numberWrong= AstroQA.numberWrong;
        score = (int)(100*AstroQA.score);
        String question = AstroQA.question;
        boolean isCorrect = AstroQA.isCorrect;
        String amplification = AstroQA.amplification;

        Log.i(TAG,"right="+numberRight+" wrong="+numberWrong+" score="+score+"%");
        Log.i(TAG,"question="+question);

        Button newButton = (Button) findViewById(R.id.nextQuestion_button);
        newButton.setOnClickListener(event_listener);

        Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setOnClickListener(event_listener);

        // Question
        tv1 = (TextView)findViewById(R.id.TextView02);
        tv1.append(question);
        // Possible answers
        tv2 = (TextView)findViewById(R.id.TextView03);
        for(int i=0; i<5; i++){
            tv2.append(AstroQA.answer[i]+"\n");
        }

        // Correct or incorrect
        tv3 = (TextView)findViewById(R.id.TextView04);
        String ans = "Your answer "+AstroQA.answerArray[AstroQA.selectedButton]+" is ";
        if(isCorrect){
            ans += "CORRECT. ";
            ans += "\n\n"+amplification;
        } else {
            ans += "INCORRECT. ";
            ans += " The correct answer is " + AstroQA.answerArray[AstroQA.correctIndex] +".";
        }
        tv3.append(ans);

        // Score
        tv4 = (TextView)findViewById(R.id.TextView05);
        String s = "Right: "+numberRight+"   Wrong: "+numberWrong+"   Score: " + score +"%";
        tv4.append(s);
    }


    @Override
    protected void onPause(){
        super.onPause();
        // To prevent navigation back to previous question
        finish();
    }

    // Process button clicks
    private OnClickListener event_listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.nextQuestion_button:
                    AstroQA.selectedButton = -1;    // So warning is issued if no answer selected
                    Intent i = new Intent(AnswerScreen.this, AstroQA.class);  // New question
                    startActivity(i);
                    break;
                case R.id.reset_button:
                    resetScores();
                    break;
            }
        }
    };

    // Method to reset scores
    private void resetScores(){
        numberRight = 0;
        numberWrong = 0;
        score = 0;

        SharedPreferences.Editor edit = AstroQA.prefs.edit();
        edit.putInt("numberRight", numberRight);
        edit.putInt("numberWrong", numberWrong);
        edit.putInt("numberQuestions", numberRight+numberWrong);
        edit.putFloat("score", score);
        edit.commit();

        String s = "Right: "+numberRight+"   Wrong: "+numberWrong+"   Score: " + score +"%";
        tv4.setText("");
        tv4.append(s);
    }
}

