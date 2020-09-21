package com.example.upscquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    private TextView Score,QuestionCount;
    private Button doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Score = findViewById(R.id.score);
        QuestionCount = findViewById(R.id.questionCount);
        doneBtn = findViewById(R.id.done);

        Score.setText(String.valueOf(getIntent().getIntExtra("score",0)));
        QuestionCount.setText(String.valueOf("Out of "+getIntent().getIntExtra("questionCount",0)));

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}