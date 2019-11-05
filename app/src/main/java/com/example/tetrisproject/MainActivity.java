package com.example.tetrisproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String finish;
    private String pause;
    private String resume;
    private String language;
    private String help;
    private boolean paused = false;
    private Board tetrisBoard;
    private Locale locale;
    Configuration config = new Configuration();
    Resources res;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        getActionBar();
        tetrisBoard =(Board)findViewById(R.id.tetrisBoard);
        res = getResources();
        locale  = new Locale("no", "NO");
    }

    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        addMenuOptions(menu, paused);
        return true;
    }

    void addMenuOptions(Menu menu, boolean paused){
        finish = res.getString(R.string.finish);
        pause = res.getString(R.string.pause);
        resume = res.getString(R.string.resume);
        language = res.getString(R.string.language);
        help = res.getString(R.string.help);
        menu.add(finish);
        if(!paused) menu.add(pause);
        else menu.add(resume);
        menu.add(help);
        SubMenu sub = menu.addSubMenu(menu.NONE, 1, 3, language);
        sub.add(2, 5, 1, "Norsk");
        sub.add(2, 5, 2, "English");
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getTitle().equals(finish)){
            tetrisBoard.getThread().setRunning(false);
            finish();
        }
        else if (item.getTitle().equals(pause)) {
            tetrisBoard.getThread().setPaused(true);
            paused = true;
            menu.clear();
            addMenuOptions(menu, paused);
        }
        else if (item.getTitle().equals(resume)) {
            tetrisBoard.getThread().setPaused(false);
            tetrisBoard.setFocusable(true);
            paused = false;
            menu.clear();
            addMenuOptions(menu, paused);
        }
        else if(item.getTitle().equals(help)){
            tetrisBoard.getThread().setPaused(true);
            paused = true;
            new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme))
                    .setTitle(getString(R.string.help))
                    .setMessage(getString(R.string.helpDescription).replace("-/", "\n"))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            tetrisBoard.getThread().setPaused(false);
                            paused = false;
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public void onDismiss(DialogInterface dialog) {
                            tetrisBoard.getThread().setPaused(false);
                            paused = false;
                        }
                    })
                    .show();
        }
        else if(item.getTitle().equals(("Norsk"))){
            Log.i("Språk: ", "Norsk");
            locale = new Locale("no", "NO");
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
            menu.clear();
            addMenuOptions(menu, paused);
        }
        else if(item.getTitle().equals(("English"))){
            Log.i("Language: ", "English");
            locale = new Locale("en", "US");
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
            menu.clear();
            addMenuOptions(menu, paused);
        }
        return true;
    }
}
