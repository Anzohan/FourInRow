package com.apps.FourInRow.lab.figure;

import com.apps.FourInRow.lab.game_control.Direction;

/**
 * Фигура. Определяет фигуру и ее различные характеристики
 */
public class Figure
{
    private int mCellId;            //id ячейки, которая принадлежит данной фигуре
    private FigureType mType;       //Тип фигуры (крестик, нолик, пустая)
    private Figure mLeftNeighbor;   //Левый сосед
    private Figure mRightNeighbor;  //Правый сосед
    private Figure mTopNeighbor;    //Верхний сосед
    private Figure mBottomNeighbor; //Нижний сосед
    private Figure mLtNeighbor;     //Левый верхний сосед
    private Figure mRtNeighbor;     //Правый верхний сосед
    private Figure mLbNeighbor;     //Левый нижний сосед
    private Figure mRbNeighbor;     //Правый нижний сосед

    /**
     * Конструктор
     *
     * @param cellId - id ячейки, которая будет принадлежать данной фигуре
     */
    public Figure(int cellId)
    {
        mCellId = cellId;
    }

    /**
     * @return возвращает id ячейки, которая принадлежит данной фигуре
     */
    public int getCellId()
    {
        return mCellId;
    }

    /**
     * Задает тип данной фигуры
     *
     * @param type - задаваемый тип фигуры
     */
    public void setFigureType(FigureType type)
    {
        mType = type;
    }

    /**
     * @return возвращает тип фигуры
     */
    public FigureType getType()
    {
        return mType;
    }

    /**
     * @param direction - нужное направление
     * @return - возвращает соседа, по нужному направлению
     */
    public Figure getNeighborFromDirection(Direction direction)
    {
        switch (direction)
        {
            case LEFT:
                return mLeftNeighbor;
            case RIGHT:
                return mRightNeighbor;
            case TOP:
                return mTopNeighbor;
            case BOTTOM:
                return mBottomNeighbor;
            case LT:
                return mLtNeighbor;
            case RT:
                return mRtNeighbor;
            case LB:
                return mLbNeighbor;
            case RB:
                return mRbNeighbor;
            default:
                return null;
        }
    }

    /**
     * @param currentDirection -текущее направление
     * @return - возвращает соседа в противоположном от текущего направления
     */
    public Figure getNeighborInOppositeDirection(Direction currentDirection)
    {
        return getNeighborFromDirection(Direction.getOppositeDirection(currentDirection));
    }

    /**
     * Задает соседей для фигуры
     *
     * @param neighbors - соседи.Всего 8 штук. Задаются в след порядке:
     *                  левый, правый, верхний, нижний, левый верхний, правый верхний,
     *                  левый нижний, правый нижний.
     */
    public void setNeighbors(Figure... neighbors)
    {
        mLeftNeighbor = neighbors[0];
        mRightNeighbor = neighbors[1];
        mTopNeighbor = neighbors[2];
        mBottomNeighbor = neighbors[3];
        mLtNeighbor = neighbors[4];
        mRtNeighbor = neighbors[5];
        mLbNeighbor = neighbors[6];
        mRbNeighbor = neighbors[7];
    }
}