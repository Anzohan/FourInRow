package com.apps.FourInRow.lab.game_control;

import com.apps.FourInRow.lab.figure.FigureController;
import com.apps.FourInRow.lab.figure.FigureType;

/**
 * Класс Ход, описывающий и реализующий некоторые базовые методы.
 */
public abstract class Step
{
    protected StepManager mManager;              // Менеджер ходов
    protected StepAlgorithm mStepAlgorithm;      // Алгоритм ходов
    protected FigureController mFigureController; //Контроллер фигур
    protected FigureType mComputerType;          // Тип фигуры у компьютера
    protected FigureType mPlayerType;            // Тип фигуры у игрока
    private boolean mCurrentStep;                // Очередь хода текущей фигурой
    private OnStepDoneListener mListener;        // Слушатель окончания хода

    /**
     * Конструктор
     *
     * @param manager - менеджер ходов, включающий в себя слушателя завершения хода
     */
    Step(StepManager manager)
    {
        mManager = manager;
        mListener = manager;
        mStepAlgorithm = mManager.getStepAlgorithm();
        mFigureController = mManager.getFigureController();
        mComputerType = mFigureController.getComputerFigureType();
        mPlayerType = mFigureController.getPlayerFigureType();
    }

    /**
     * Обновление хода фигуры (текущий ход - становится ходом данной фигуры)
     */
    public void updateStep()
    {
        mCurrentStep = true;
    }

    /**
     * @return - возвращает состояние хода (текущий ход - мой, или чужой)
     */
    public boolean isCurrentStep()
    {
        return mCurrentStep;
    }

    /**
     * Звершение хода текущей фигуры
     *
     * @param who - какая фигура завершила ход (тэг)
     */
    protected void stepDone(String who)
    {
        mCurrentStep = false;
        mListener.onStepDone(who);
    }

    /**
     * Очистить слушатель
     */
    public void removeListener()
    {
        mListener = null;
    }
}