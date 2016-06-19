package com.apps.FourInRow.lab.game_control;

import com.apps.FourInRow.lab.figure.Figure;
import com.apps.FourInRow.lab.figure.FigureType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Класс "Победитель". Определяет победителя игры, если таковой имеется.
 */
class Winner
{
    private StepManager mStepManager;              //Менеджер ходов
    private ArrayList<Integer> mWinnerComboCellId; // Выигрышная комбинация ходов(id ячеек)

    /**
     * Конструктор
     *
     * @param manager - менеджер ходов
     */
    public Winner(StepManager manager)
    {
        mStepManager = manager;
        mWinnerComboCellId = new ArrayList<>(4);
    }

    /**
     * Содержит ли данный список ходов выигрышную комбинацию (победитель ли)
     *
     * @param targetFigureList - нужный список ходов определенной фигуры
     * @return - возвращает истину, если есть победитель и ложь в противном случае
     */
    public boolean isWin(Stack<Figure> targetFigureList)
    {
        for (Figure figure : targetFigureList)
        {
            if (checkSideOnWin(figure, Direction.LEFT)
                    || checkSideOnWin(figure, Direction.RIGHT)
                    || checkSideOnWin(figure, Direction.TOP)
                    || checkSideOnWin(figure, Direction.BOTTOM)
                    || checkSideOnWin(figure, Direction.LT)
                    || checkSideOnWin(figure, Direction.RT)
                    || checkSideOnWin(figure, Direction.LB)
                    || checkSideOnWin(figure, Direction.RB))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Является ли текущее положение ничейным
     *
     * @return - возвращает истину, если игра звершилась ничьей и ложь,
     * если еще есть свободные фигуры
     */
    public boolean isDraw()
    {
        List<Figure> figureList = mStepManager.getFigureController().getFigureList();
        for (Figure figure : figureList)
        {
            if (figure.getType().equals(FigureType.UNSELECTED))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Получить id ячеек с выигрышной комбинацией
     *
     * @return возвращает id ячеек выигрышной комбинации, или пустой массив в противном случае
     */
    public ArrayList<Integer> getWinComboCellId()
    {
        return mWinnerComboCellId;
    }

    /**
     * Проверить сторону на присутствие выигрышных комбинаций
     *
     * @param startFigure - начальная фигура
     * @param direction   - направление, в котором будет осуществляться проверка
     * @return - возвращает истину, если сторона содержит выигрышную комбинацию
     * и ложь в противном случае
     */
    private boolean checkSideOnWin(Figure startFigure, Direction direction)
    {
        return getFiguresCount(startFigure, startFigure.getType(), direction, 0) == 4;
    }

    /**
     * Получить кол-во одинаковых фигур в ряд (не более 4х).
     *
     * @param figure     - начальная фигура
     * @param targetType - нужный тип фигуры (крестик, нолик)
     * @param direction  - нужное направление
     * @param count      - кол-во одинаковых фигур
     * @return - возвращает кол-во одинаковых фигур, идущих в ряд
     */
    private int getFiguresCount(Figure figure, FigureType targetType, Direction direction,
                                int count)
    {
        if (count < 4)
        {
            if (figure != null)
            {
                if (figure.getType().equals(targetType))
                {
                    count++;
                    //Заносим id ячейки в лист выигрышной комбинации
                    mWinnerComboCellId.add(figure.getCellId());
                    Figure neighbor = figure.getNeighborFromDirection(direction);
                    count = getFiguresCount(neighbor, targetType, direction, count);
                }
            }
        }

        //Если комба не выигрышная - очищаем список.
        if (mWinnerComboCellId.size() < 4)
        {
            mWinnerComboCellId.clear();
        }
        return count;
    }
}