package com.apps.FourInRow.lab.ui;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.apps.FourInRow.lab.R;
import com.apps.FourInRow.lab.figure.Figure;
import com.apps.FourInRow.lab.figure.FigureController;
import com.apps.FourInRow.lab.figure.FigureType;
import com.apps.FourInRow.lab.game_control.Difficulty;
import com.apps.FourInRow.lab.game_control.StepManager;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Игровое поле, на котором происходит игра
 */
public class GameFieldActivity extends Activity implements View.OnClickListener
{
    private GridLayout mField;          //Слой поля, с ячейками
    private TextView mWinnerText;       //Текстовый виджет для отображения победителя
    private TextView mComputerRateText; //Виджет для отображения оценки хода компьютера
    /**
     * Очищаем оценку хода компьютера
     */
    private final Runnable mClearComputerRateRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            mComputerRateText.setText("");
        }
    };
    private TextView mPlayerRateText;   //Виджет для отображения оценки хода игрока
    /**
     * @see Runnable
     * Очищаем оценку хода игрока
     */
    private final Runnable mClearPlayerRateRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            mPlayerRateText.setText("");
        }
    };
    private Button mBackStep;           //Кнопка для отката хода.
    private Button mShowStep;           //Кнопка, для показа лучшего хода для игрока
    private Button mEndButton;          //Кнопка для выхода (возврат к выбору уровня)
    private StepManager mStepManager;           //Менеджер ходов
    private FigureController mFigureController; //Контроллер фигур
    private UiOperationReceiver mUiOperationReceiver;//Приемник сообщений для выполнения UI операций
    private Handler mComputerRateClearHandler;       //Обработчик для очистки оценки компьютера
    private Handler mPlayerRateClearHandler;         //Обработчик для очистки оценки игрока
    private HideStepHandler mHideStepHandler;        //Обработчик для очистки подстветки хода.
    /**
     * Вызываем обработчик, для очистки подсветки наилучшего хода игрока
     */
    private final Runnable mHideStepRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            mHideStepHandler.sendMessage(mHideStepHandler.obtainMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_field);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initViews();
        mFigureController = new FigureController();
        initFigureList();
        mComputerRateClearHandler = new Handler();
        mPlayerRateClearHandler = new Handler();
        mHideStepHandler = new HideStepHandler();

        registerReceiverAndFilters();
        addInformationFromIntent();
    }

    /**
     * Инициализация виджетов, кнопок и.т.д
     */
    private void initViews()
    {
        mWinnerText = (TextView) findViewById(R.id.winner_name_text);
        mComputerRateText = (TextView) findViewById(R.id.computer_step_rate);
        mPlayerRateText = (TextView) findViewById(R.id.player_step_rate);
        mField = (GridLayout) findViewById(R.id.field);
        mBackStep = (Button) findViewById(R.id.back_step_btn);
        mBackStep.setOnClickListener(this);
        mShowStep = (Button) findViewById(R.id.show_step);
        mShowStep.setOnClickListener(this);
        mEndButton = (Button) findViewById(R.id.end_btn);
        mEndButton.setOnClickListener(this);
    }

    /**
     * Инициализация и регистрация приемника сообщений
     */
    private void registerReceiverAndFilters()
    {
        IntentFilter broadcastFilter = new IntentFilter(StepManager.WINNER);
        broadcastFilter.addAction(StepManager.RATE);
        broadcastFilter.addAction(StepManager.FIGURE);

        mUiOperationReceiver = new UiOperationReceiver();
        registerReceiver(mUiOperationReceiver, broadcastFilter);
    }

    /**
     * Инициализация фигур поля и их соседей
     */
    private void initFigureList()
    {
        for (int i = 0; i < mField.getChildCount(); i++)
        {
            ImageButton cell = (ImageButton) mField.getChildAt(i);
            if (cell != null)
            {
                cell.setOnClickListener(this);
                Figure newFigure = new Figure(cell.getId());
                newFigure.setFigureType(FigureType.UNSELECTED);
                mFigureController.addFigure(newFigure);
            }
        }
        mFigureController.findNeighbors(this);
    }

    /**
     * Получить информацию из интента, который посылали в предыдущей активности
     */
    private void addInformationFromIntent()
    {
        Difficulty difficulty = (Difficulty) getIntent().
                getSerializableExtra(SelectModeActivity.DIFFICULTY_KEY);

        FigureType playerFigureType = (FigureType) getIntent().
                getSerializableExtra(SelectModeActivity.FIGURE_TYPE_KEY);

        FigureType computerFigureType = playerFigureType.equals(FigureType.CROSS) ?
                FigureType.ZERO : FigureType.CROSS;

        mFigureController.setPlayerFigureType(playerFigureType);
        mFigureController.setComputerFigureType(computerFigureType);
        mStepManager = new StepManager(difficulty, mFigureController);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_step_btn:
                mStepManager.removeLastStepFromHistory();
                break;
            case R.id.show_step:
                showStepForPlayer();
                break;
            case R.id.end_btn:
                finish();
                break;
            default:
                tryToStep(v);
                break;
        }
    }

    /**
     * Показать игроку лучший ход.
     */
    private void showStepForPlayer()
    {
        int betterCellId = mStepManager.findStepForShowUser();
        if (betterCellId != 0)
        {
            ImageButton betterStep = (ImageButton) findViewById(betterCellId);
            if (betterStep != null)
            {
                //Если найден, устанавливаем подсветку и отключаем кнопку, на 0.55 сек
                //пока подсветка не выключится
                betterStep.setImageDrawable(getResources().getDrawable(R.drawable.show_step_cell));
                betterStep.setEnabled(false);
                mHideStepHandler.removeCallbacks(mHideStepRunnable);
                mHideStepHandler.setCellForHideAfterTimeout(betterStep);
                mHideStepHandler.postDelayed(mHideStepRunnable, 550);
            }
        }
    }

    /**
     * Попробовать выполнить ход игроку
     *
     * @param cell - ячейка, выбраная для хода
     */
    private void tryToStep(View cell)
    {
        Figure figure = mFigureController.findFigureByCellId(cell.getId());
        if (figure != null && figure.getType().equals(FigureType.UNSELECTED))
        {
            //Если фигура есть и она пустая - делаем ход
            mStepManager.getPlayerStep().step(figure);
        }
    }

    /**
     * Задать изображение нужной фигуры, для ячейки
     *
     * @param cellId - id фигуры, которой принадлежит ячейка
     * @param type   - тип фигуры
     */
    private void setFigureDrawable(int cellId, FigureType type)
    {
        ImageButton cell = (ImageButton) findViewById(cellId);
        switch (type)
        {
            case CROSS:
                cell.setImageDrawable(getResources().getDrawable(R.drawable.cross));
                break;
            case ZERO:
                cell.setImageDrawable(getResources().getDrawable(R.drawable.zero));
                break;
            case UNSELECTED:
                cell.setImageDrawable(null);
                break;
        }
    }

    /**
     * Показать оценку хода
     *
     * @param which  - кто совершал ход
     * @param rateId - id оценки в ресурсах
     */
    private void setRateTargetStep(String which, int rateId)
    {
        if (rateId != 0 && which != null)
        {
            //Если оценка и создатель хода не пустые, то определяем чей ход
            // и показываем оценку на 0.9 сек для игрока и 1.2 сек. для компьютера
            String rate = getString(rateId);
            if (which.equals(StepManager.PLAYER_STEP_TAG))
            {
                mPlayerRateClearHandler.removeCallbacks(mClearPlayerRateRunnable);
                mPlayerRateText.setText(getString(R.string.player_rate_label, rate));
                mPlayerRateClearHandler.postDelayed(mClearPlayerRateRunnable, 1500);
            } else
            {
                mComputerRateClearHandler.removeCallbacks(mClearComputerRateRunnable);
                mComputerRateText.setText(getString(R.string.computer_step_label, rate));
                mComputerRateClearHandler.postDelayed(mClearComputerRateRunnable, 2000);
            }
        }
    }

    /**
     * Показать, победителя, или что игра завершилась ничьей
     *
     * @param winnerTextId     - id текста в ресурсах
     * @param winnerComboCells - массив id ячеек выигрышнйо комбинации
     */
    private void setWinnerLabelAndEndGame(int winnerTextId, List<Integer> winnerComboCells)
    {
        if (winnerTextId != 0)
        {
            //Если id не пустой, то показываем победителя(ничью)
            //И скрываем кнопки показа и отката хода.
            mWinnerText.setText(winnerTextId);
            if (winnerComboCells != null && !winnerComboCells.isEmpty())
            {
                highlightWinnerComboCells(winnerComboCells);
            }
            mBackStep.setVisibility(View.INVISIBLE);
            mShowStep.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Подсвечивает выигрышную комбинацию
     *
     * @param winnerComboCells - массив id ячеек выигрышной комбинации
     */
    private void highlightWinnerComboCells(List<Integer> winnerComboCells)
    {
        for (int cellId : winnerComboCells)
        {
            findViewById(cellId).setBackgroundColor(getResources().
                    getColor(R.color.win_combo_highlight));
        }
    }

    /**
     * Вызывается когда активность заканчивает свою работу
     *
     * @see Activity#onDestroy()
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mBackStep.setOnClickListener(null);
        mShowStep.setOnClickListener(null);
        mEndButton.setOnClickListener(null);
        mStepManager.getPlayerStep().removeListener();
        mStepManager.getComputerStep().removeListener();
        mStepManager = null;
        mFigureController = null;

        if (mUiOperationReceiver != null)
        {
            unregisterReceiver(mUiOperationReceiver);
        }

        mComputerRateClearHandler.removeCallbacksAndMessages(null);
        mPlayerRateClearHandler.removeCallbacksAndMessages(null);
        mHideStepHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Обработчик, для разблокировки и очистики подсветки у ячейки, которую показали игроку
     */
    private static class HideStepHandler extends Handler
    {
        private WeakReference<ImageButton> mNeighborBtn;

        public void setCellForHideAfterTimeout(ImageButton cell)
        {
            mNeighborBtn = new WeakReference<>(cell);   //Берем слабую ссылку на ячейку (виджет)
        }

        @Override
        public void handleMessage(Message msg)
        {
            ImageButton btn = mNeighborBtn.get();
            if (btn != null)
            {
                btn.setImageDrawable(null);
                btn.setEnabled(true);
                mNeighborBtn = null;                    //Очищаем ссылку
            }
        }
    }

    /**
     * Приемник сообщений, для получения сообщений от других классов,
     * для выполнения каких либо операций в UI
     *
     * @see BroadcastReceiver
     */
    private class UiOperationReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch (intent.getAction())     //Смотрим, какое действие надо вершить
            {
                case StepManager.RATE:
                    int msgId = intent.getIntExtra(StepManager.RATE_MSG_KEY, 0);
                    String which = intent.getStringExtra(StepManager.RATE_WHICH_KEY);
                    setRateTargetStep(which, msgId);
                    break;

                case StepManager.FIGURE:
                    int cellId = intent.getIntExtra(StepManager.FIGURE_CELL_ID_KEY, 0);
                    FigureType type = (FigureType) intent.
                            getSerializableExtra(StepManager.FIGURE_TYPE_KEY);
                    setFigureDrawable(cellId, type);
                    break;

                case StepManager.WINNER:
                    int winnerNameId = intent.getIntExtra(StepManager.WINNER_NAME_KEY, 0);
                    List<Integer> winnerComboArrayCellId = intent.
                            getIntegerArrayListExtra(StepManager.WINNER_COMBO_LIST_KEY);
                    setWinnerLabelAndEndGame(winnerNameId, winnerComboArrayCellId);
                    break;
            }
        }
    }
}