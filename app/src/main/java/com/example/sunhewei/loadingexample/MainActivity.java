package com.example.sunhewei.loadingexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.sunhewei.loadingexample.loading_view.LoadingCircleView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout main_ll;

    private Button main_start,main_stop;

    LoadingCircleView loadingCircleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_start = (Button) findViewById(R.id.main_start);
        main_stop = (Button) findViewById(R.id.main_stop);
        main_ll = (LinearLayout) findViewById(R.id.main_ll);
        loadingCircleView = new LoadingCircleView(this);
        main_ll.addView(loadingCircleView);

        main_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingCircleView.start();
            }
        });

        main_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingCircleView.stop();
            }
        });
    }
}
