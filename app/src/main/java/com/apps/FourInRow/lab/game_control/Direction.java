package com.apps.FourInRow.lab.game_control;

/**
 * Направления. Хранит в себе всевозможные направления,
 * в которые можно осуществлять поиск, для фигур.
 */
public enum Direction
{
    LEFT,       //Левое
    RIGHT,      //Правое
    TOP,        //Верхнее
    BOTTOM,      //Нижнее
    LT,         //Лево-вверх
    RT,         //Право-вверх
    LB,         //Лево-вниз
    RB;         //Право-вниз

    /**
     * Получить противоположное направление
     *
     * @param currentDirection - текущее направление
     * @return - возвращает противоположное направление
     */
    public static Direction getOppositeDirection(Direction currentDirection)
    {
        switch (currentDirection)
        {
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            case TOP:
                return BOTTOM;
            case BOTTOM:
                return TOP;
            case LT:
                return RB;
            case RT:
                return LB;
            case LB:
                return RT;
            case RB:
                return LT;
            default:
                return null;
        }
    }
}