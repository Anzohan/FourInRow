package com.apps.FourInRow.lab.game_control;

import android.content.Intent;

import com.apps.FourInRow.lab.Application;
import com.apps.FourInRow.lab.R;
import com.apps.FourInRow.lab.figure.Figure;
import com.apps.FourInRow.lab.figure.FigureController;
import com.apps.FourInRow.lab.figure.FigureType;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Менеджер ходов. Отвечает за операции, связанные с любыми ходами
 */
public class StepManager implements OnStepDoneListener
{
    public static final String COMPUTER_STEP_TAG = "computer_step";   //Тэг "Ход Компьютера"
    public static final String PLAYER_STEP_TAG = "player_step";       //Тэг "Ход Игрока"
    public final static String RATE = "rate";                         //Ключ для "Оценка"
    public final static String RATE_MSG_KEY = "rate_msg";             //Ключ для "Текстовая оценка"
    public final static String RATE_WHICH_KEY = "which_rate";         //Ключ для "Кому была оценка"
    public final static String WINNER = "winner";                     //Ключ для "Победитель"
    public final static String WINNER_NAME_KEY = "winner_name";       //Ключ для "Имя победителя"
    public final static String WINNER_COMBO_LIST_KEY = "combo_list";  //Ключ для "Комбо-лист"
    public final static String FIGURE = "figure";                     //Ключ для "Фигура"
    public final static String FIGURE_CELL_ID_KEY = "figure_cell_id"; //Ключ для "Id Ячейки Фигуры"
    public final static String FIGURE_TYPE_KEY = "figure_type";       //Ключ для "Тип Фигуры"

    private PlayerStep mPlayer;                  //Ходок игрока
    private ComputerStep mComputer;              //Ходок компьютера
    private StepAlgorithm mStepAlgorithm;        //Алгоритм хода
    private Winner mWinner;                      //Определитель победителя
    private FigureController mFigureController;  //Контроллер фигур
    private StepRater mStepRater;                //Оценщик ходов
    private Difficulty mDifficulty;              //Текущая сложность
    private Stack<Figure> mComputerStepsHistory; //История ходов компьютера
    private Stack<Figure> mPlayerStepsHistory;   //История ходов игрока

    /**
     * Конструктор
     *
     * @param difficulty - выбраный уровень сложности
     * @param controller - контроллер фигур
     */
    public StepManager(Difficulty difficulty, FigureController controller)
    {
        mFigureController = controller;
        mWinner = new Winner(this);
        mDifficulty = difficulty;

        mComputerStepsHistory = new Stack<>();
        mPlayerStepsHistory = new Stack<>();

        mStepAlgorithm = new StepAlgorithm(this);
        mPlayer = new PlayerStep(this);
        mComputer = new ComputerStep(this);

        mStepRater = new StepRater();
        firstStep();
    }

    /**
     * Получить текущий уровень сложности
     *
     * @return - возвращает текущий уровень сложности
     */
    public Difficulty getDifficulty()
    {
        return mDifficulty;
    }

    /**
     * Получить историю ходов игрока
     *
     * @return - возвращает список(историю) ходов игрока
     */
    public Stack<Figure> getPlayerStepsHistory()
    {
        return mPlayerStepsHistory;
    }

    /**
     * Получить историю ходов компьютера
     *
     * @return - возвращает список(историю) ходов компьютера
     */
    public Stack<Figure> getComputerStepsHistory()
    {
        return mComputerStepsHistory;
    }

    /**
     * Получить ходока игрока
     *
     * @return - возвращает ходока игрока
     */
    public PlayerStep getPlayerStep()
    {
        return mPlayer;
    }

    /**
     * Получить ходока компьютера
     *
     * @return - возвращает ходока компьютера
     */
    public ComputerStep getComputerStep()
    {
        return mComputer;
    }

    /**
     * Получить алгоритм ходов
     *
     * @return - возвращает алгоритм ходов
     */
    public StepAlgorithm getStepAlgorithm()
    {
        return mStepAlgorithm;
    }

    /**
     * Добавить совершенный ход в историю ходов
     *
     * @param tag  - тэг, чей ход добавлять (компьютера, игрока)
     * @param step - последний ход
     */
    public void addStepInHistory(String tag, Figure step)
    {
        if (tag.equals(COMPUTER_STEP_TAG))
        {
            mComputerStepsHistory.push(step);
        } else
        {
            mPlayerStepsHistory.push(step);
        }
    }

    /**
     * Совершить первый ход (или, если первый ход игрока - разрешить ему ход)
     */
    private void firstStep()
    {
        if (mFigureController.getPlayerFigureType().equals(FigureType.ZERO))
        {
            mComputer.updateStep();
            mComputer.step();
        } else
        {
            mPlayer.updateStep();
        }
    }

    /**
     * Удалить последний шаг из историй игрока и компьютера
     */
    public void removeLastStepFromHistory()
    {
        if (!mComputerStepsHistory.isEmpty() && !mPlayerStepsHistory.isEmpty())
        {
            Figure computerFigure = mComputerStepsHistory.pop();
            Figure playerFigure = mPlayerStepsHistory.pop();
            computerFigure.setFigureType(FigureType.UNSELECTED);
            playerFigure.setFigureType(FigureType.UNSELECTED);
            sendFigureBroadcast(computerFigure.getCellId(), computerFigure.getType());
            sendFigureBroadcast(playerFigure.getCellId(), playerFigure.getType());
            mComputer.updateStep();
            mPlayer.updateStep();
        }
    }

    /**
     * Получить контролер фигур
     *
     * @return - возвращает контроллер фигур
     */
    public FigureController getFigureController()
    {
        return mFigureController;
    }

    /**
     * Проверка на то, что игра завершилась (победа, ничья, поражение)
     *
     * @param whoEndStepTag - тэг закончившего ход (игрок, компьютер)
     * @return - возвращает истину, если игра закончена и ложь в противном случае.
     */
    private boolean checkOnEndOfGame(String whoEndStepTag)
    {
        Stack<Figure> targetStepsHistory = whoEndStepTag.equals(COMPUTER_STEP_TAG) ?
                getComputerStepsHistory() : getPlayerStepsHistory();
        if (mWinner.isWin(targetStepsHistory))  //Если победили
        {
            int winnerNameId = whoEndStepTag.equals(PLAYER_STEP_TAG) ?
                    R.string.player_win_text : R.string.computer_win_text;

            //То посылаем бродкаст о том, что игра звершилась
            sendWinnerBroadcast(winnerNameId, mWinner.getWinComboCellId());
            return true;
        } else if (mWinner.isDraw())            //А если ничья
        {                                       //То посылаем бродкаст, что ничья случилась.
            sendWinnerBroadcast(R.string.draw_text, null);
            return true;
        }
        return false;
    }

    /**
     * При завершении хода
     *
     * @param which - кто закончил ход (компьютер, игрок)
     */
    @Override
    public void onStepDone(String which)
    {
        if (!checkOnEndOfGame(which))
        {
            if (which.equals(COMPUTER_STEP_TAG))
            {
                rateStep(COMPUTER_STEP_TAG);
                mPlayer.updateStep();
            } else
            {
                rateStep(PLAYER_STEP_TAG);
                mComputer.updateStep();
                mComputer.step();
            }
        }
    }

    /**
     * Оценить ход
     *
     * @param whoseTag - тэг, который принадлежит участнику, чей ход нужно оценить
     */
    private void rateStep(String whoseTag)
    {
        int rateId = 0;
        FigureType playerType = mFigureController.getPlayerFigureType();
        FigureType computerType = mFigureController.getComputerFigureType();
        switch (whoseTag)
        {
            case COMPUTER_STEP_TAG:
                rateId = mStepRater.rate(mComputerStepsHistory.peek(), computerType, playerType);
                break;
            case PLAYER_STEP_TAG:
                rateId = mStepRater.rate(mPlayerStepsHistory.peek(), playerType, computerType);
                break;
        }
        if (rateId != 0)
        {
            sendRateBroadcast(whoseTag, rateId);
        }
    }

    /**
     * Найти лучший ход, для показа его игроку
     *
     * @return - возвращает id ячейки, которая принадлежит фигуре лучшего хода
     */
    public int findStepForShowUser()
    {
        Figure betterStep = mPlayer.generateBetterStepForShow();
        return betterStep.getCellId();
    }

    /**
     * Послать сообщение об оценке
     *
     * @param whoseRateTag - тэг участника, которому постаивли оценку
     * @param rateId       - строковое id оценки
     */
    private void sendRateBroadcast(String whoseRateTag, int rateId)
    {
        Intent rate = new Intent(RATE);
        rate.putExtra(RATE_MSG_KEY, rateId);
        rate.putExtra(RATE_WHICH_KEY, whoseRateTag);
        rate.setPackage(Application.INSTANCE.getPackageName());
        Application.INSTANCE.sendBroadcast(rate);
    }

    /**
     * Послать сообщение о победителе
     *
     * @param winnerNameId - строковый id имени победителя (текст победителя)
     * @param winnerCombo  - id ячеек с выигрышной комбинацией
     */
    private void sendWinnerBroadcast(int winnerNameId, ArrayList<Integer> winnerCombo)
    {
        Intent winner = new Intent(WINNER);
        winner.putExtra(WINNER_NAME_KEY, winnerNameId);
        if (winnerCombo != null)
        {
            winner.putIntegerArrayListExtra(WINNER_COMBO_LIST_KEY, winnerCombo);
        }
        winner.setPackage(Application.INSTANCE.getPackageName());
        Application.INSTANCE.sendBroadcast(winner);
    }

    /**
     * послать сообщение про заданую фигуру (ячейку)
     *
     * @param cellId - id ячейки, фигуре которой она принадлежит
     * @param type   - тип этой фигуры
     */
    public void sendFigureBroadcast(int cellId, FigureType type)
    {
        Intent figure = new Intent(FIGURE);
        figure.putExtra(FIGURE_CELL_ID_KEY, cellId);
        figure.putExtra(FIGURE_TYPE_KEY, type);
        figure.setPackage(Application.INSTANCE.getPackageName());
        Application.INSTANCE.sendBroadcast(figure);
    }
}