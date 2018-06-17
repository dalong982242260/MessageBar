package com.dl.messagebar.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dl.messagebar.BottomMessageBar;
import com.dl.messagebar.MessageBar;
import com.dl.messagebar.TopMessageBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.top).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopMessageBar.make((ViewGroup) findViewById(R.id.parent), "从上往下的消息", MessageBar.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomMessageBar.make((ViewGroup) findViewById(R.id.parent), "从下往上的消息", MessageBar.LENGTH_LONG).show();
            }
        });
    }
}
