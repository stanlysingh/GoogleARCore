package com.google.ar.core.examples.java;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.ar.core.examples.java.augmentedimage.MainActivity;
import com.google.ar.core.examples.java.augmentedimage.R;

public class ChooseActivity extends AppCompatActivity {


    TextView threedView,videoView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        videoView=findViewById(R.id.video);
        threedView=findViewById(R.id.threed);

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                call(1);

            }
        });


        threedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(2);


            }
        });

    }

    private void call(int i) {

        startActivity(new Intent(this, MainActivity.class).putExtra("call",i));

    }

}
