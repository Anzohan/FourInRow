package com.apps.FourInRow.lab.game_control;

import com.apps.FourInRow.lab.R;
import com.apps.FourInRow.lab.figure.Figure;
import com.apps.FourInRow.lab.figure.FigureType;

/**
 * Оценщик ходов. Оценивает ходы заданного игрока.
 */
class StepRater
{
    private static final int GOOD_STEP = R.string.good_step;        //Хороший ход
    private static final int NORMAL_STEP = R.string.normal_step;    //Нормальный ход
    private static final int BAD_STEP = R.string.bad_step;          //Плохой ход

    /**
     * Оценивает чей-либо ход
     *
     * @param lastTargetStep - последний сделаный ход
     * @param myType         - ваш тип фигуры
     * @param opponentType   - тип фигуры оппонента
     * @return - возвращает оценку, для сделаного хода
     */
    public int rate(Figure lastTargetStep, FigureType myType, FigureType opponentType)
    {
        int totalRate = 0;

        totalRate = rateAtDirection(lastTargetStep, myType, opponentType, Direction.LEFT,
                totalRate);
        if (totalRate == 3)    //Если оценка хорошая, возвращаем ее сразу
        {
            return getRateFromNumber(totalRate);
        }


        totalRate = rateAtDirection(lastTargetStep, myType, opponentType, Direction.RIGHT,
                totalRate);
        if (totalRate == 3)
        {
            return getRateFromNumber(totalRate);
        }


        totalRate = rateAtDirection(lastTargetStep, myType, opponentType, Direction.TOP,
                totalRate);
        if (totalRate == 3)
        {
            return getRateFromNumber(totalRate);
        }


        totalRate = rateAtDirection(lastTargetStep, myType, opponentType, Direction.BOTTOM,
                totalRate);
        if (totalRate == 3)
        {
            return getRateFromNumber(totalRate);
        }


        totalRate = rateAtDirection(lastTargetStep, myType, opponentType, Direction.LT,
                totalRate);
        if (totalRate == 3)
        {
            return getRateFromNumber(totalRate);
        }


        totalRate = rateAtDirection(lastTargetStep, myType, opponentType, Direction.RT,
                totalRate);
        if (totalRate == 3)
        {
            return getRateFromNumber(totalRate);
        }


        totalRate = rateAtDirection(lastTargetStep, myType, opponentType, Direction.LB,
                totalRate);
        if (totalRate == 3)
        {
            return getRateFromNumber(totalRate);
        }


        totalRate = rateAtDirection(lastTargetStep, myType, opponentType, Direction.RB,
                totalRate);
        if (totalRate == 3)
        {
            return getRateFromNumber(totalRate);
        }

        return getRateFromNumber(totalRate);
    }

    /**
     * Попытаться дать хорошую оценку ходу
     *
     * @param lastStep     - последний ход
     * @param direction    - заданое направление, для оценки по нему
     * @param opponentType - тип фигуры оппонента
     * @param buildCount   - размер здания, которому принадлежит этот ход
     * @return - возвращает хорошую оценку если ход хороший и 0 в противном случае
     */
    private int tryToRateGood(Figure lastStep, Direction direction, FigureType opponentType,
                              int buildCount)
    {    //Хороший в плане блока фигур противника
        if (isSatisfyStep(lastStep, opponentType, 2) || isBuildGoodAtDirection(lastStep,
                //Хороший в плане свободных ячеек
                direction, buildCount) || isSatisfyStep(lastStep, FigureType.UNSELECTED, 3))
        {
            return GOOD_STEP;
        }
        return 0;
    }

    /**
     * Хорошее ли здание по заданому направлению
     *
     * @param lastStep   - последний ход
     * @param direction  - заданое направление
     * @param buildCount - размер здания
     * @return - возвращает истину, если здание можно оценить как хорошее, по данному направлению
     * и ложь в противном случае
     */
    private boolean isBuildGoodAtDirection(Figure lastStep, Direction direction, int buildCount)
    {
        return buildCount > 2 && satisfyForRate(lastStep, direction);
    }

    /**
     * Подходит ли фигура, принадлежащая зданию для определенной оценки
     *
     * @param lastStep  - последний ход
     * @param direction - направление в котором будет происходить проверка
     * @return - возвращает истину, если подходит и ложь в противном случае.
     */
    private boolean satisfyForRate(Figure lastStep, Direction direction)
    {
        Figure neighbor = lastStep.getNeighborInOppositeDirection(direction);
        return neighbor != null && neighbor.getType().equals(FigureType.UNSELECTED);
    }

    /**
     * Является ли этот ход хорошим/удовлетворяющим положению
     *
     * @param yourLastStep - ваш последний ход
     * @param targetType   - тип фигуры оппонента
     * @return - возвращает истину, если ход является хорошим, для блокирования оппонента
     * и ложь в противном случае
     */
    private boolean isSatisfyStep(Figure yourLastStep, FigureType targetType,
                                  int satisfyBuildCount)
    {
        int left = findFigureCountAtDirection(yourLastStep.
                getNeighborFromDirection(Direction.LEFT), targetType, Direction.LEFT);
        int right = findFigureCountAtDirection(yourLastStep.
                getNeighborFromDirection(Direction.RIGHT), targetType, Direction.RIGHT);
        int top = findFigureCountAtDirection(yourLastStep.
                getNeighborFromDirection(Direction.TOP), targetType, Direction.TOP);
        int bottom = findFigureCountAtDirection(yourLastStep.
                getNeighborFromDirection(Direction.BOTTOM), targetType, Direction.BOTTOM);

        int lt = findFigureCountAtDirection(yourLastStep.
                getNeighborFromDirection(Direction.LT), targetType, Direction.LT);
        int rt = findFigureCountAtDirection(yourLastStep.
                getNeighborFromDirection(Direction.LT), targetType, Direction.RT);
        int lb = findFigureCountAtDirection(yourLastStep.
                getNeighborFromDirection(Direction.LT), targetType, Direction.LB);
        int rb = findFigureCountAtDirection(yourLastStep.
                getNeighborFromDirection(Direction.LT), targetType, Direction.RB);

        return left >= satisfyBuildCount || top >= satisfyBuildCount ||
                right >= satisfyBuildCount || bottom >= satisfyBuildCount ||
                lt >= satisfyBuildCount || rt >= satisfyBuildCount ||
                lb >= satisfyBuildCount || rb >= satisfyBuildCount;
    }

    /**
     * Ход находится между фигурами оппонента
     *
     * @param lastStep       - последний ход
     * @param startDirection - начальное направление, в котором будет осществляться проверка
     * @param opponentType   - тип фигуры оппонента
     * @return - возвращает истину если последний ход находится между фигурами оппонента
     * и ложь в противном случае
     */
    private boolean isStepBetweenOpponentFigures(Figure lastStep, Direction startDirection,
                                                 FigureType opponentType)
    {
        if (lastStep != null)
        {
            Figure firstNeighbor = lastStep.getNeighborFromDirection(startDirection);
            if (firstNeighbor != null && firstNeighbor.getType().equals(opponentType))
            {
                Figure secondNeighbor = lastStep.getNeighborInOppositeDirection(startDirection);
                return secondNeighbor != null && secondNeighbor.getType().equals(opponentType);
            }
        }
        return false;
    }

    /**
     * Нормальное ли здание по заданому направлению
     *
     * @param lastTargetFigure - последний ход
     * @param direction        - заданое направление
     * @param buildCount       - высота здания
     * @return - возвращает истину если здание можно оценить как нормальное
     * и ложь в противном случае
     */
    private boolean isBuildNormalAtDirection(Figure lastTargetFigure, Direction direction,
                                             int buildCount)
    {
        return buildCount == 2 && satisfyForRate(lastTargetFigure, direction);
    }

    /**
     * Являются ли фигуры, между ходом - фгурами оппонента
     *
     * @param lastTargetStep - последний ход
     * @param opponentType   - тип фигуры оппонента
     * @return - возвращает истину, если между последним ходом стоят фигуры оппонента
     * и ложь в противном случае
     */
    private boolean betweenFiguresAreOpponents(Figure lastTargetStep, FigureType opponentType)
    {
        return isStepBetweenOpponentFigures(lastTargetStep, Direction.LEFT, opponentType)
                || isStepBetweenOpponentFigures(lastTargetStep, Direction.TOP, opponentType)
                || isStepBetweenOpponentFigures(lastTargetStep, Direction.LT, opponentType)
                || isStepBetweenOpponentFigures(lastTargetStep, Direction.RT, opponentType);
    }

    /**
     * Попытаться поставить оценку "нормально"
     *
     * @param lastTargetStep - последний ход
     * @param direction      - направление, по которому будет проводится оценка
     * @param opponentType   - Тип фигуры оппонента
     * @param buildCount     - высота здания
     * @return - возвращает оценку нормально, если ход оказался таким или 0 в противном случае
     */
    private int tryToRateNormal(Figure lastTargetStep, Direction direction,
                                FigureType opponentType, int buildCount)
    {
        if (isBuildNormalAtDirection(lastTargetStep, direction, buildCount) ||
                betweenFiguresAreOpponents(lastTargetStep, opponentType) ||
                isSatisfyStep(lastTargetStep, FigureType.UNSELECTED, 2))
        {
            return NORMAL_STEP;
        }
        return 0;
    }

    /**
     * Поставить оценку по заданому направлению от последнего хода
     *
     * @param lastTargetStep - последний ход
     * @param myType         - ваш тип фигуры
     * @param opponentType   - тип фигуры оппонента
     * @param direction      - заданое направление
     * @param totalRate      - итоговая оценка (на текущий момент)
     * @return - возвращает оценку по заданому направлению, если она выше итоговой оценки
     * и итоговую оценку в противном случае
     */
    private int rateAtDirection(Figure lastTargetStep, FigureType myType, FigureType opponentType,
                                Direction direction, int totalRate)
    {
        int countAtDirection = 1 + findFigureCountAtDirection(lastTargetStep.
                getNeighborFromDirection(direction), myType, direction);
        int curRate = calculateRateAtDirection(lastTargetStep, direction, opponentType,
                countAtDirection);
        if (curRate > totalRate)
        {
            totalRate = curRate;
        }
        return totalRate;
    }

    /**
     * Вычисление оценки
     *
     * @param lastTargetStep - последний ход
     * @param direction      - заданое направление по которому будет вычисляться оценка
     * @param opponentType   - тип фигуры оппонента
     * @param buildCount     - высота здания по заданому направлению
     * @return - возвращает оценку в числовом формате (чтобы получить нормальную,
     * необходимо конвертировать ее:
     * @see #getRateFromNumber(int)
     */
    private int calculateRateAtDirection(Figure lastTargetStep, Direction direction,
                                         FigureType opponentType, int buildCount)
    {
        int rate;

        rate = tryToRateGood(lastTargetStep, direction, opponentType, buildCount);
        if (rate != 0)
        {
            return 3;
        }

        rate = tryToRateNormal(lastTargetStep, direction, opponentType, buildCount);
        if (rate != 0)
        {
            return 2;
        }
        return 1;
    }

    /**
     * Получить оценку в виде ее строкового id
     *
     * @param number - номер, который соответсвует оценке (выдается функцией:
     * @return - возвращает оценку в виде строкового id (String Resource Id)
     * @see #calculateRateAtDirection(Figure, Direction, FigureType, int))
     */
    private int getRateFromNumber(int number)
    {
        switch (number)
        {
            case 1:
                return BAD_STEP;
            case 2:
                return NORMAL_STEP;
            case 3:
                return GOOD_STEP;
            default:
                return 0;
        }
    }

    /**
     * Получить кол-во фигур нужного типа по заданому направлению
     *
     * @param startFigure - начальная фигура
     * @param type        - нужный тип фигуры
     * @param direction   - заданое направление
     * @return - возвращает кол-во фигур нужного типа, идущих по заданому направлению
     */
    private int findFigureCountAtDirection(Figure startFigure, FigureType type, Direction direction)
    {
        int count = 0;
        if (startFigure != null)
        {
            if (startFigure.getType().equals(type))
            {
                count++;
                count += findFigureCountAtDirection(startFigure.getNeighborFromDirection(direction),
                        type, direction);
            }
        }
        return count;
    }
}