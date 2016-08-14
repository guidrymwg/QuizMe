package com.lightcone.quizme;

import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.StringTokenizer;


/**
 * Created by guidry on 8/13/16.
 */
public class AstroQA extends AppCompatActivity {

    public static final String TAG = "QUIZME";

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
    private int numberQuestions = 0;
    protected static boolean isCorrect;
    protected static int correctIndex = -1;
    private ProgressBar progressBar;
    private Context context;

    // Logic in Settings (Preferences) menu
    private boolean onSplash = true;
    private boolean speakQuestions;
    private boolean expertMode;
    private boolean onAnswerPage = false;
    private boolean hasAmplification = false;
    private boolean isRetrieving = false;

    // Scoring
    public static int numberRight = 0;
    public static int numberWrong = 0;
    public static int numberScored = 0;
    public static float score = 0;
    public static int qnumber = -1;

    // Shared preferences
    public static  SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    private boolean randomizeOrder = false;

    // JSON aray to hold questions as JSON objects once read in from data file
    private JSONArray arrayJSON;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.astroqa);

        context = getApplicationContext();

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

        // Execute the data load from raw/datafile on a background thread
        progressBar = (ProgressBar) findViewById(R.id.qa_bar);
        if(!isRetrieving) new BackgroundLoad().execute();
        isRetrieving = true;

        // Set up a SharedPreferences to store scores and preferences so they will
        // persist and a shared preferences editor to change these values.

        prefs = this.getApplicationContext().getSharedPreferences("prefs", 0);
        edit = prefs.edit();

        numberRight = prefs.getInt("numberRight",0);
        numberWrong = prefs.getInt("numberWrong", 0);
        numberScored = prefs.getInt("numberQuestions", 0);
        score = prefs.getFloat("score", 0);
        speakQuestions = prefs.getBoolean("speakQuestions", true);
        expertMode = prefs.getBoolean("expertMode", false);
        qnumber = prefs.getInt("qnumber", -1);

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
        edit.putInt("qnumber",qnumber);
        edit.commit();

        // Define an Intent to launch an answer screen
        Intent i = new Intent(this, AnswerScreen.class);
        startActivity(i);
    }

    /**************************************************************
     The following methods do the data access and processing
     of the string that is returned from the file in the raw directory. Done on
     background thread to avoid locking up the main UI thread.
     ***************************************************************/

    // Use AsyncTask to perform the data load on a background thread.  The three
    // argument types inside the < > are a type for the input parameters (Void in this case, since
    // there are no input parameters), a type for any published progress during the background
    // task (Void in this case,  because we aren't going to publish progress since the task should
    // be very short), and a type for the object returned from the background task (in this case it
    // is type String).

    private class BackgroundLoad extends AsyncTask <Void, Void, String>{

        // Executes the task on a background thread
        @Override
        protected String doInBackground(Void... params) {

            // The notation Void... params means that there are no input parameters.
            // In new BackgroundLoad().execute() above no parameter arguments
            // are passed.

            return readQuestionsResource(context);
        }

        // Executes before the thread run by doInBackground
        protected void onPreExecute () {

        }

        // Executes after the thread run by doInBackground has returned. The variable s
        // passed is the string value returned by doInBackground.

        @Override
        protected void onPostExecute(String s){
            // Parse the returned string
            //parseQuizData(s);
            // Stop the progress bar
            progressBar.setVisibility(View.GONE);

            // Parse the returned string
           // postParse(s);

            parseQuizData(s);

            //postParse();

            // Make buttons visible
			submitButton.setVisibility(View.VISIBLE);
			for(int i=0; i<5; i++){
				answerButton[i].setVisibility(View.VISIBLE);
			}

            displayQuestion();

        }
    }

    /**
     * Reads the text from res/raw/questions.json and returns it as a string. Adapted
     * from Glass GDK Compass example.
     */

    private static String readQuestionsResource(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.questions);
        StringBuffer buffer = new StringBuffer();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append('\n');
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not read questions resource", e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(TAG, "Could not close questions resource stream", e);
                }
            }
        }
        Log.i(TAG, "string"+buffer.toString());
        return buffer.toString();
    }

    // Method to parse data read in from the data file

    private  void parseQuizData (String s){

        Log.i(TAG,"\nRaw string:\n"+s);
        try {
            parseJSON(s);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // JSON parser.

    public void parseJSON (String resp) throws IllegalStateException,
            IOException, JSONException, NoSuchAlgorithmException {

        JSONObject response = new JSONObject(resp).getJSONObject("responseData");
        arrayJSON = response.getJSONArray("questions");
        numberQuestions = arrayJSON.length();

        Log.i(TAG,"numberQuestions="+numberQuestions);

        // Test random number generator
        //  testRandom(120000);

        isRetrieving = false;
    }

    // Method to extract a question from the JSON array of question objects and
    // display it as cards.  Called by tap on trackpad.

    private void displayQuestion(){

        // Decide whether to present questions in sequence or randomly
        if(randomizeOrder){
            qnumber = randomQuestion(numberQuestions);
        } else {
            if(qnumber < numberQuestions-1){
                qnumber = qnumber+1;
                Log.i(TAG,"noRandom: numberQuestions="+numberQuestions+" currentQuestion="+qnumber);
            } else {
                qnumber = 0;
            }
        }

        //Log.i(TAG,"numberQuestions="+numberQuestions+" currentQuestion="+qnumber);

        // Extract the question and assign data to variables
        try {
            question= capFirstLetter(arrayJSON.getJSONObject(qnumber).getString("q"));
            answer[0] = arrayJSON.getJSONObject(qnumber).getString("a");
            answer[1] = arrayJSON.getJSONObject(qnumber).getString("b");
            answer[2] = arrayJSON.getJSONObject(qnumber).getString("c");
            answer[3] = arrayJSON.getJSONObject(qnumber).getString("d");
            answer[4] = arrayJSON.getJSONObject(qnumber).getString("e");
            coran = capFirstLetter(arrayJSON.getJSONObject(qnumber).getString("coran"));
            amplification = capFirstLetter(arrayJSON.getJSONObject(qnumber).getString("amp"));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        Log.i(TAG,"\ndisplayQuestion() : q="+question);
        Log.i(TAG,"a1="+answer[0]);
        Log.i(TAG,"a2="+answer[1]);
        Log.i(TAG,"a3="+answer[2]);
        Log.i(TAG,"a4="+answer[3]);
        Log.i(TAG,"a5="+answer[4]);
        Log.i(TAG,"coran="+coran);
        Log.i(TAG,"amp="+amplification);

        if(amplification.length() > 0){
            hasAmplification = true;
        } else {
            hasAmplification = false;
        }

        // Assign  an integer index 0-4 to the correct answer corresponding to the letter A-E
        // for later convenience.

        if(coran.equalsIgnoreCase("A")){
            correctIndex = 0;
        } else if (coran.equalsIgnoreCase("B")){
            correctIndex = 1;
        } else if (coran.equalsIgnoreCase("C")){
            correctIndex = 2;
        } else if (coran.equalsIgnoreCase("D")){
            correctIndex = 3;
        } else if (coran.equalsIgnoreCase("E")){
            correctIndex = 4;
        }
        questionView.setText(question);
        answerButton[0].setText(answer[0]);
        answerButton[1].setText(answer[1]);
        answerButton[2].setText(answer[2]);
        answerButton[3].setText(answer[3]);
        answerButton[4].setText(answer[4]);
    }

    // Method to choose random integer representing question number
    // and return as int. Generally,
    //      int random = (min.value ) + (int)(Math.random()* ( Max - Min + 1));
    // will return random number between Min and Max, inclusive.  Thus, to return
    // a random number between 0 and qnum-1, where qnum is the number of questions,
    //      int random = 0 +  (int)(Math.random()*(qnum));
    // should do the job.

    private int randomQuestion(int qnum){
        int ch = (int)(Math.random()*(qnum));
        return ch;
    }

    // Utility to set the first letter of a string to upper case

    public String capFirstLetter(String string){
        int len = string.length();
        if(len<1) return string;
        String sub1 = string.substring(0,1);
        String sub2 = string.substring(1,len);
        return sub1.toUpperCase(Locale.US)+sub2;
    }

    // To process the string returned
    private void postParse(String s){
        Log.i(TAG,"postParse: "+s);
        StringTokenizer st = new StringTokenizer(s,"\n");
        String ts;
        Log.i(TAG,"\nFrom Tokenizer:\n");
        qnum = st.nextToken();

        qnum = qnum.substring(qnum.indexOf("=")+1).trim();
        Log.i(TAG,"qnum="+qnum);
        int iqnum = Integer.parseInt(qnum);
        question = st.nextToken();
        question = question.substring(question.indexOf("=")+1).trim();
        Log.i(TAG, "qnum="+iqnum);
        Log.i(TAG, "question="+question);
        for(int i=0; i<5; i++){
            ts = st.nextToken();
            answer[i] = answerArray[i]+".  "+ts.substring(ts.indexOf("=")+1).trim();
            Log.i(TAG, "Answer["+i+"]: "+ answer[i]);
            answerButton[i].setText(" "+answer[i]);
        }
        chapter = st.nextToken();
        chapter = chapter.substring(chapter.indexOf("=")+1).trim();
        Log.i(TAG, "chapter="+chapter);
        coran = st.nextToken();
        coran = coran.substring(coran.indexOf("=")+1).trim();
        Log.i(TAG,"coran="+coran);
        amplification = st.nextToken();
        amplification = amplification.substring(amplification.indexOf("=")+1).trim();
        Log.i(TAG,"amplification="+amplification);
        questionView.append("\n"+question);

        // Assign  an integer index 0-4 to the correct answer that can be compared with
        // the integer index selectedButton for the answer chosen

        if(coran.equalsIgnoreCase("A")){
            correctIndex = 0;
        } else if (coran.equalsIgnoreCase("B")){
            correctIndex = 1;
        } else if (coran.equalsIgnoreCase("C")){
            correctIndex = 2;
        } else if (coran.equalsIgnoreCase("D")){
            correctIndex = 3;
        } else if (coran.equalsIgnoreCase("E")){
            correctIndex = 4;
        }
    }


}
