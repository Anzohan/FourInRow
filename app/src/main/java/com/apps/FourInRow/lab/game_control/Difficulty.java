package com.apps.FourInRow.lab.game_control;

/**
 * Уровни сложности. Хранит в себе уровни сложности.
 */
public enum Difficulty
{
    HARD(2),        //Тяжелый (2 фигуры в ряд у игрока - повод для блокирования)
    NORMAL(2),      //Нормальный (2 фигуры в ряд у игрока - повод для блокирования)
    EASY(3);        //Легкий (3 фигуры в ряд у игрока - повод для блокирования)


    private int mValue; //Число фигур игрока, идущих в ряд, с которого будет блокирование

    Difficulty(int value)
    {
        mValue = value;
    }

    /**
     * @return - возвращает число фигур игрока, идущих в ряд, с которого будет блокирование
     */
    public int getValue()
    {
        return mValue;
    }
}