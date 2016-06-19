package com.apps.FourInRow.lab.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.apps.FourInRow.lab.R;

/**
 *  Окно главного меню
 */
public class MainMenuActivity extends Activity implements View.OnClickListener
{

    /**
     * Вызывается при создании активности
     *
     * @see Activity#onCreate(Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        //Здесь и в дальнейшем будем скрывать верхнюю панель Android.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Button playBtn = (Button) findViewById(R.id.begin_play_btn);
        Button rulesBtn = (Button) findViewById(R.id.rules_btn);
        Button exitBtn = (Button) findViewById(R.id.exit_btn);

        playBtn.setOnClickListener(this);
        rulesBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);

    }

    /**
     * Начать игру. Осуществляет переход на активность выбора сложности и фигуры.
     */
    private void startPlay()
    {
        Intent goToSelectDifIntent = new Intent(MainMenuActivity.this, SelectModeActivity.class);
        startActivity(goToSelectDifIntent);
    }

    /**
     * Перейти к правилам игры.
     */
    private void goToRules()
    {
        Intent goToRulesIntent = new Intent(MainMenuActivity.this, RulesActivity.class);
        startActivity(goToRulesIntent);
    }

    /**
     * @see android.view.View.OnClickListener
     */
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.begin_play_btn:
                startPlay();
                break;
            case R.id.rules_btn:
                goToRules();
                break;
            case R.id.exit_btn:
                android.os.Process.killProcess(Process.myPid());
                break;
        }
    }
}