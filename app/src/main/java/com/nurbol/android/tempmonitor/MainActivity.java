package com.nurbol.android.tempmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView start = (TextView) findViewById(R.id.start);
        Button sign_in_button = (Button) findViewById(R.id.sign_in_button);
        Button sign_up_button = (Button) findViewById(R.id.sign_up_button);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent numbersIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(numbersIntent);
            }
        });

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent numbersIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(numbersIntent);
            }
        });

        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent numbersIntent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(numbersIntent);
            }
        });
    }
}
