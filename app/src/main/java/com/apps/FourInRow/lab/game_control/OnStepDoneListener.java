package com.apps.FourInRow.lab.game_control;

/**
 * Слушатель заканчивания какого-либо хода.
 */
interface OnStepDoneListener
{
    /**
     * Срабатывает при заканчивании какого-либо хода
     *
     * @param which - кто закончил ход (компьютер, игрок)
     */
    void onStepDone(String which);
}