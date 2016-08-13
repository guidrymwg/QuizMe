package com.lightcone.quizme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Click listeners for all buttons
        View checkButton = findViewById(R.id.astroQA_button);
        checkButton.setOnClickListener(this);
  /*      View xmlButton = findViewById(R.id.XML_button);
        xmlButton.setOnClickListener(this);
        View getButton = findViewById(R.id.GET_button);
        getButton.setOnClickListener(this);
        View postButton = findViewById(R.id.POST_button);
        postButton.setOnClickListener(this);*/
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.astroQA_button:
                Intent i = new Intent(this, AstroQA.class);
                startActivity(i);
                break;
/*
            case R.id.XML_button:
                Intent j = new Intent(this, XMLexample.class);
                startActivity(j);
                break;

            case R.id.GET_button:
                Intent k = new Intent(this, GETexample.class);
                startActivity(k);
                break;

            case R.id.POST_button:
                Intent m = new Intent(this, POSTexample.class);
                startActivity(m);
                break;*/
        }
    }
}
