package com.apps.FourInRow.lab.game_control;

import com.apps.FourInRow.lab.figure.Figure;

/**
 * Ход компьютера. Отвечает за ходы, совершаемые компьютером
 */
public class ComputerStep extends Step
{

    /**
     * Конструктор
     *
     * @param manager - передается сам менеджер ходов, содержащий все необходимое
     */
    ComputerStep(StepManager manager)
    {
        super(manager);
    }

    /**
     * Совершить ход
     */
    public void step()
    {
        Figure selectedFigure = mStepAlgorithm.generateStep(mManager.getComputerStepsHistory(),
                mManager.getPlayerStepsHistory(), mComputerType, mPlayerType, false);
        selectedFigure.setFigureType(mManager.getFigureController().getComputerFigureType());
        mManager.sendFigureBroadcast(selectedFigure.getCellId(), selectedFigure.getType());
        mManager.addStepInHistory(StepManager.COMPUTER_STEP_TAG, selectedFigure);
        stepDone(StepManager.COMPUTER_STEP_TAG);
    }
}