package com.apps.FourInRow.lab.game_control;

import com.apps.FourInRow.lab.figure.Figure;

/**
 * Ход игрока. Отвечает за ходы, совершаемые игроком.
 */
public class PlayerStep extends Step
{
    /**
     * Конструктор
     *
     * @param manager - передается сам менеджер ходов, который также включает в себя слушателя
     */
    PlayerStep(StepManager manager)
    {
        super(manager);
    }

    /**
     * Процедура выполнения хода
     *
     * @param figureCell - фигура, выбраная для совершения хода
     */
    public void step(Figure figureCell)
    {
        if (isCurrentStep())    //Если сейчас ход игрока - делаем ход.
        {
            figureCell.setFigureType(mFigureController.getPlayerFigureType());
            mManager.sendFigureBroadcast(figureCell.getCellId(), figureCell.getType());
            mManager.addStepInHistory(StepManager.PLAYER_STEP_TAG, figureCell);
            stepDone(StepManager.PLAYER_STEP_TAG);
        }
    }

    /**
     * Сгенерировать ход для отображения игроку, если тот нажмет "показать ход"
     * @return - сгенерированый ход
     */
    public Figure generateBetterStepForShow()
    {
        return mStepAlgorithm.generateStep(mManager.getPlayerStepsHistory(),
                mManager.getComputerStepsHistory(), mPlayerType, mComputerType, true);
    }
}