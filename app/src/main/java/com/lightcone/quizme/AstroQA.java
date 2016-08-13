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


}
