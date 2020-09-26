package com.example.upscquiz;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {
    public static final String FILE_NAME = "UPSC_QUIZ";
    public static final String KEY_NAME = "QUESTIONS";
    private TextView Question,noindicator;
    private FloatingActionButton bookmarkBtn;
    private LinearLayout optionsContainer;
    private Button shareBtn,nextBtn;
    int count = 0;
    private List<QuestionModel> list;
    private int position = 0;
    private int score = 0;
    private String category;
    private int setNo;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private List<QuestionModel> Bmlist;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    private Dialog loadingDialog;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loadingdialogue);
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.roundedcorner));

        Question = findViewById(R.id.question);
        noindicator = findViewById(R.id.noIndicator);
        bookmarkBtn = findViewById(R.id.bookmarkButton);
        optionsContainer = findViewById(R.id.optionsContainer);
        shareBtn = findViewById(R.id.share_btn);
        nextBtn = findViewById(R.id.next_btn);
        
        list = new ArrayList<>();

        preference = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preference.edit();
        gson = new Gson();
        getBookmarks();

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ifBookmarked()){
                    Toast.makeText(QuestionsActivity.this,"Already Bookmarked",Toast.LENGTH_LONG).show();
                }else{
                   Bmlist.add(list.get(position));
                }
            }
        });


        category = getIntent().getStringExtra("category");
        setNo = getIntent().getIntExtra("sets",1);

        loadingDialog.show();
        myRef.child("SETS").child(category).child("questions").orderByChild("setNo").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot datasnapshot : snapshot.getChildren()){
                    list.add(datasnapshot.getValue(QuestionModel.class));
                }
                if(list.size() > 0){
                    for(int i = 0;i < 4; i++){
                        optionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View v) {
                                checkAnswer((Button)v);
                            }
                        });
                    }

                    playAnim(Question, 0, list.get(position).getQuestion());
                    noindicator.setText((position+1)+"/"+list.size());
                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View v) {
                            enableOption(true);
                            nextBtn.setEnabled(false);
                            nextBtn.setAlpha(0.7f);
                            position++;
                            if(position == list.size()) {
                                Intent scoreIntent = new Intent(QuestionsActivity.this,ScoreActivity.class);
                                scoreIntent.putExtra("score",score);
                                scoreIntent.putExtra("questionCount",list.size());
                                startActivity(scoreIntent);
                                finish();
                                return;
                            }
                            count = 0;
                            playAnim(Question, 0, list.get(position).getQuestion());
                            noindicator.setText((position+1)+"/"+list.size());
                        }
                    });
                    shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String shareString = list.get(position).getQuestion()+"\n"+"Choose the correct one :"+"\n1."+list.get(position).getOption1();
                            Intent shareIntenet = new Intent(Intent.ACTION_SEND);
                            shareIntenet.setType("text/plain");
                            shareIntenet.putExtra(Intent.EXTRA_SUBJECT,"Quizzer Challenge");
                            shareIntenet.putExtra(Intent.EXTRA_TEXT,shareString);
                            startActivity(Intent.createChooser(shareIntenet,"share via"));
                        }
                    });
                }
                else{

                    Toast.makeText(QuestionsActivity.this,"no questions added",Toast.LENGTH_LONG);

                    finish();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }

    private void playAnim(final View view, final int value, final String data){
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(value == 0 && count < 4){
                    String option = "";
                    if(count == 0){
                        option = list.get(position).getOption1();
                    }else if(count == 1){
                        option = list.get(position).getOption2();
                    }else if(count == 2){
                        option = list.get(position).getOption3();
                    }else if(count == 3){
                        option = list.get(position).getOption4();
                    }
                    playAnim(optionsContainer.getChildAt(count),0,option);
                    count++;
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
              if(value == 0){
                  try{
                      ((TextView)view).setText(data);
                  }catch (ClassCastException ex){
                      ((Button)view).setText(data);
                  }
                  view.setTag(data);
                  playAnim(view,1,data);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkAnswer(Button selectedOption){
            String scoretext;
            enableOption(false);
            nextBtn.setEnabled(true);
            nextBtn.setAlpha(1);
            if(selectedOption.getText().toString().equals(list.get(position).getOptionCorrect())){
                score++;
                selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            }else{
                //incorrect
                selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                Button correctoption = (Button) optionsContainer.findViewWithTag(list.get(position).getOptionCorrect());
                correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

            }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void enableOption(boolean enable){
        for(int i = 0;i < 4; i++){
            optionsContainer.getChildAt(i).setEnabled(enable);
            if(enable){
                optionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
            }
        }
    }
    private void getBookmarks(){
        String json =  preference.getString(KEY_NAME,"");
        Type type = new TypeToken<List<QuestionModel>>(){}.getType();
        Bmlist = gson.fromJson(json,type);
        if(Bmlist == null){
            Bmlist = new ArrayList<>();
        }
    }
    private void storeBookmarks(){
        String json = gson.toJson(Bmlist);
        editor.putString(KEY_NAME,json);
        editor.commit();
    }
    private boolean ifBookmarked(){
        boolean match = false;
        for(QuestionModel model : Bmlist){
            if(model.getQuestion().equals(list.get(position).getQuestion())){
                match = true;
            }
        }
        return match;
    }
}