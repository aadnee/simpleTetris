package com.example.tetrisproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private String finish;
    private String pause;
    private String resume;
    private Board tetrisBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        getActionBar();
        tetrisBoard =(Board)findViewById(R.id.tetrisBoard);
        Resources res = getResources();
        finish = res.getString(R.string.finish);
        pause = res.getString(R.string.pause);
        resume = res.getString(R.string.resume);

    }

    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        menu.add(finish);
        menu.add(pause);
        menu.add(resume);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getTitle().equals(finish)){
            tetrisBoard.getThread().setRunning(false);
            finish();
        }
        else if (item.getTitle().equals(pause)) {
            tetrisBoard.getThread().setPaused(true);
        }
        else if (item.getTitle().equals(resume)) {
            tetrisBoard.getThread().setPaused(false);
            tetrisBoard.setFocusable(true);
        }
        return true;
    }
}
