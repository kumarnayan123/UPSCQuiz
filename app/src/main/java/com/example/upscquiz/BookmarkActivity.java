package com.example.upscquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.example.upscquiz.QuestionsActivity.FILE_NAME;
import static com.example.upscquiz.QuestionsActivity.KEY_NAME;


public class BookmarkActivity extends AppCompatActivity {
    //public static final String FILE_NAME = "UPSC_QUIZ";
    //public static final String KEY_NAME = "QUESTIONS";
    private RecyclerView Bookmarkrv;
    private List<QuestionModel> list;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;
    private Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.bookmarks);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bookmarkrv = findViewById(R.id.rv_bookmarked);
        LinearLayoutManager layoutmanager = new LinearLayoutManager(this);
        layoutmanager.setOrientation(RecyclerView.VERTICAL);

        Bookmarkrv.setLayoutManager(layoutmanager);

        preference = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = preference.edit();
        gson = new Gson();
        String json =  preference.getString(KEY_NAME,"");
        Type type = new TypeToken<List<QuestionModel>>(){}.getType();
        list = gson.fromJson(json,type);
        if(list == null){
            list = new ArrayList<>();
        }
        //list.add(new QuestionModel("dfdgf","","","","","hjkm",1));
        final BookmarkAdapter adapter = new BookmarkAdapter(list);
        Bookmarkrv.setAdapter(adapter);


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}