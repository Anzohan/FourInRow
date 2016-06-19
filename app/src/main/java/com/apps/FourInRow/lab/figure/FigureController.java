package com.apps.FourInRow.lab.figure;

import android.app.Activity;

import com.apps.FourInRow.lab.game_control.Direction;
import com.apps.FourInRow.lab.ui.NeighborImageButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер фигур. Отвечает за операции, связанные с фигурами
 */
public class FigureController
{
    private FigureType mPlayerFigureType;       //Тип фигуры у игрока
    private FigureType mComputerFigureType;     //Тип фигуры у компьютера
    private List<Figure> mFigureList;           //Список всех фигур


    public FigureController()
    {
        mFigureList = new ArrayList<>();
    }

    /**
     * Найти фигуру по id ячейки
     *
     * @param cellId - id ячейки, которая принадлежит искомой фигуре
     * @return - возвращает найденую фигуру (или null в противном случае)
     */
    public Figure findFigureByCellId(int cellId)
    {
        for (Figure figure : mFigureList)
        {
            if (figure.getCellId() == cellId)
            {
                return figure;
            }
        }
        return null;
    }

    /**
     * @return - возвращает тип фигуры у игрока
     */
    public FigureType getPlayerFigureType()
    {
        return mPlayerFigureType;
    }

    /**
     * Задает тип фигуры игроку
     *
     * @param type - тип фигуры, для задания
     */
    public void setPlayerFigureType(FigureType type)
    {
        mPlayerFigureType = type;
    }

    /**
     * @return - возвращает тип фигуры у компьютера
     */
    public FigureType getComputerFigureType()
    {
        return mComputerFigureType;
    }

    /**
     * Задает тип фигуры компьютеру
     *
     * @param type - тип фигуры, для задания
     */
    public void setComputerFigureType(FigureType type)
    {
        mComputerFigureType = type;
    }

    /**
     * Добавляет фигуру в список известных фигур
     *
     * @param figure - фигура, которую надо добавить
     */
    public void addFigure(Figure figure)
    {
        mFigureList.add(figure);
    }

    /**
     * @return - возвращает список всех известных фигур
     */
    public List<Figure> getFigureList()
    {
        return mFigureList;
    }

    /**
     * Поиск соседей для всех фигур
     *
     * @param activity - активность, на которой лежат ячейки для фигур
     */
    public void findNeighbors(Activity activity)
    {
        for (Figure figure : mFigureList)
        {
            NeighborImageButton curFigureView = (NeighborImageButton)
                    activity.findViewById(figure.getCellId());
            if (curFigureView != null)
            {
                Figure left = findFigureByCellId(curFigureView.
                        getNeighborViewIdByDirection(Direction.LEFT));
                Figure right = findFigureByCellId(curFigureView.
                        getNeighborViewIdByDirection(Direction.RIGHT));
                Figure top = findFigureByCellId(curFigureView.
                        getNeighborViewIdByDirection(Direction.TOP));
                Figure bottom = findFigureByCellId(curFigureView.
                        getNeighborViewIdByDirection(Direction.BOTTOM));

                Figure lt = findFigureByCellId(curFigureView.
                        getNeighborViewIdByDirection(Direction.LT));
                Figure rt = findFigureByCellId(curFigureView.
                        getNeighborViewIdByDirection(Direction.RT));
                Figure lb = findFigureByCellId(curFigureView.
                        getNeighborViewIdByDirection(Direction.LB));
                Figure rb = findFigureByCellId(curFigureView.
                        getNeighborViewIdByDirection(Direction.RB));

                figure.setNeighbors(left, right, top, bottom, lt, rt, lb, rb);
            } else
            {
                break;
            }
        }
    }
}