package com.apps.FourInRow.lab.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.apps.FourInRow.lab.R;
import com.apps.FourInRow.lab.figure.FigureType;
import com.apps.FourInRow.lab.game_control.Difficulty;

/**
 * Окно выбора уровня сложности и фигуры, за которую будет играть пользователь
 */
public class SelectModeActivity extends Activity implements RadioGroup.OnCheckedChangeListener,
        View.OnClickListener
{
    //Ключи для передачи выбраных:
    public static final String DIFFICULTY_KEY = "difficulty";         // Уровня сложности
    public static final String FIGURE_TYPE_KEY = "player_figure_type";// Типа фигуры

    private Difficulty mDifficulty;           //Выбраная сложность
    private FigureType mPlayerFigureType;     //Выбраный тип фигуры

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_difficulty_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        RadioGroup difficultGroup = (RadioGroup) findViewById(R.id.difficulty_group);
        RadioGroup figureGroup = (RadioGroup) findViewById(R.id.select_figure_group);
        difficultGroup.setOnCheckedChangeListener(this);
        figureGroup.setOnCheckedChangeListener(this);
        Button playBtn = (Button) findViewById(R.id.play_btn);
        playBtn.setOnClickListener(this);
        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(this);
    }

    /**
     * @see android.widget.RadioGroup.OnCheckedChangeListener
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        //Проверка группы, в которой что то выбрали
        if (group.getId() == R.id.difficulty_group)
        {
            switch (checkedId)
            {
                case R.id.hard:
                    mDifficulty = Difficulty.HARD;
                    break;
                case R.id.normal:
                    mDifficulty = Difficulty.NORMAL;
                    break;
                case R.id.easy:
                    mDifficulty = Difficulty.EASY;
                    break;
                default:
                    mDifficulty = null;
                    break;
            }
        } else if (group.getId() == R.id.select_figure_group)
        {
            switch (checkedId)
            {
                case R.id.cross:
                    mPlayerFigureType = FigureType.CROSS;
                    break;
                case R.id.zero:
                    mPlayerFigureType = FigureType.ZERO;
                    break;
                default:
                    mPlayerFigureType = null;
                    break;
            }
        }
    }

    /**
     * Попробовать начать игру и перейти на активность игрового поля
     */
    private void tryToBeginGame()
    {
        //Если пользователь выбрал нужные параметры - переходим
        if (mDifficulty != null && mPlayerFigureType != null)
        {
            Intent gameFieldIntent = new Intent(SelectModeActivity.this, GameFieldActivity.class);
            gameFieldIntent.putExtra(DIFFICULTY_KEY, mDifficulty);
            gameFieldIntent.putExtra(FIGURE_TYPE_KEY, mPlayerFigureType);
            startActivity(gameFieldIntent);
        } else
        {
            //Иначе отображаем короткий тост о том, что нужно выбрать параметры
            Toast.makeText(SelectModeActivity.this, R.string.error_begin_play_msg,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.play_btn:
                tryToBeginGame();
                break;
            case R.id.back_button:
                finish();
                break;
        }
    }
}