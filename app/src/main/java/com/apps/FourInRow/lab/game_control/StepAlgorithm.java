package com.apps.FourInRow.lab.game_control;

import com.apps.FourInRow.lab.figure.Figure;
import com.apps.FourInRow.lab.figure.FigureController;
import com.apps.FourInRow.lab.figure.FigureType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * Алгоритм шага. Генерирует шаги, для заднного игрока
 */
class StepAlgorithm
{
    private Set<Figure> mStepCandidates;  // Список кандидатов на ход, для блокирования оппонента
    private FigureController mController; // Контроллер фигур
    private Difficulty mDifficulty;       //Сложность


    /**
     * Конструктор
     *
     * @param manager - менеджер ходов
     */
    public StepAlgorithm(StepManager manager)
    {
        mController = manager.getFigureController();
        mDifficulty = manager.getDifficulty();
        mStepCandidates = new HashSet<>();
    }

    /**
     * Найти выигрышный ход
     *
     * @param targetStepsHistory - история ходов, по которой будет осуществляться поиск хода
     * @param targetType         - нужный тип фигуры, которому будет находится выигрышный ход
     * @return - возвращает выигрышный ход или null, если не нашел
     */
    private Figure findWinStep(Stack<Figure> targetStepsHistory, FigureType targetType)
    {
        for (Figure step : targetStepsHistory)
        {
            Figure winStep = findWinStepAtDirection(step, targetType, Direction.LEFT);
            if (isFigureNotNull(winStep))
            {
                return winStep;
            }

            winStep = findWinStepAtDirection(step, targetType, Direction.RIGHT);
            if (isFigureNotNull(winStep))
            {
                return winStep;
            }

            winStep = findWinStepAtDirection(step, targetType, Direction.TOP);
            if (isFigureNotNull(winStep))
            {
                return winStep;
            }

            winStep = findWinStepAtDirection(step, targetType, Direction.BOTTOM);
            if (isFigureNotNull(winStep))
            {
                return winStep;
            }

            winStep = findWinStepAtDirection(step, targetType, Direction.LT);
            if (isFigureNotNull(winStep))
            {
                return winStep;
            }

            winStep = findWinStepAtDirection(step, targetType, Direction.RT);
            if (isFigureNotNull(winStep))
            {
                return winStep;
            }

            winStep = findWinStepAtDirection(step, targetType, Direction.LB);
            if (isFigureNotNull(winStep))
            {
                return winStep;
            }

            winStep = findWinStepAtDirection(step, targetType, Direction.RB);
            if (isFigureNotNull(winStep))
            {
                return winStep;
            }
        }
        return null;
    }

    /**
     * Поиск выигрышного хода по нужному направлению
     *
     * @param startStep - начальный ход, с которого начинается поиск
     * @param type      - нужный тип фигуры
     * @param direction - нужное направление
     * @return - возвращает найденый выигрышный ход по направлению или null в противном случае
     */
    private Figure findWinStepAtDirection(Figure startStep, FigureType type, Direction direction)
    {
        Figure lastBuildFigure = findLastElemOfWinBuild(startStep, type, direction, 0);
        if (isFigureNotNull(lastBuildFigure))
        {
            Figure winStep = lastBuildFigure.getNeighborFromDirection(direction);
            if (isFigureNotNull(winStep) && winStep.getType().equals(FigureType.UNSELECTED))
            {
                //Если след. элемент по направлению свободен - возвращаем его
                return winStep;
            } else
            {
                //Иначе проверяем противоположного соседа начального хода
                winStep = startStep.getNeighborInOppositeDirection(direction);
                if (isFigureNotNull(winStep) && winStep.getType().equals(FigureType.UNSELECTED))
                {
                    return winStep;
                }
            }
        }
        return null;
    }

    /**
     * Поиск последней фигуры выигрышного здания
     *
     * @param startFigure - начальная фигура
     * @param type        - нужный тип фигуры
     * @param direction   - нужное направление
     * @param buildHeight - текущяя высота подходящего здания
     * @return - возвращает последнюю фигуру подходящего (выигрышного) здания
     */
    private Figure findLastElemOfWinBuild(Figure startFigure, FigureType type,
                                          Direction direction, int buildHeight)
    {
        Figure lastFigure = null;
        if (buildHeight < 3)    //Если здание пока не выигрышное
        {
            if (isFigureNotNull(startFigure))
            {
                if (startFigure.getType().equals(type))
                {
                    buildHeight++;
                    if (buildHeight == 3) //Если здание стало выигрышным
                    {
                        return startFigure;
                    }
                    //Если еще нет, проверяем дальше
                    Figure neighbor = startFigure.getNeighborFromDirection(direction);
                    Figure next = findLastElemOfWinBuild(neighbor, type, direction, buildHeight);
                    if (isFigureNotNull(next))
                    {
                        lastFigure = next;
                    }
                }
            }
        }
        return lastFigure;
    }

    /**
     * Генерация хода
     *
     * @param myStepsHistory       - ваша история ходов
     * @param opponentStepsHistory - история ходов оппонента
     * @param myType               - ваш тип фигуры
     * @param opponentType         - тип фигуры оппонента
     * @param player               - функцию вызывает игрок?
     * @return - возвращает сгенерированый ход
     */
    public Figure generateStep(Stack<Figure> myStepsHistory, Stack<Figure> opponentStepsHistory,
                               FigureType myType, FigureType opponentType, boolean player)
    {
        Figure selectedFigure;

        //Если сложность тяжелая, или вызывл игрок - ищем победный ход,
        // игнорируя сооружения противника
        if (mDifficulty.equals(Difficulty.HARD) || player)
        {
            selectedFigure = findWinStep(myStepsHistory, myType);
            if (isFigureNotNull(selectedFigure))
            {
                return selectedFigure;
            }
        }
        //Собираем опасные ходы противника, для последующей блокировки
        collectStepsForBlockOpponent(opponentStepsHistory);
        if (mStepCandidates.isEmpty())
        {
            //Если не нашли таковых - ищем для себя лучший ход
            selectedFigure = findBetterStep(myType);
        } else
        {
            //Если уровень тяжелый, или вызывл игрок - проверяем наличие хитрого хода
            if (mDifficulty.equals(Difficulty.HARD) || player)
            {
                selectedFigure = findCheatingStep(myStepsHistory,
                        myType, opponentType);
                if (selectedFigure != null)
                {
                    return selectedFigure;
                }
            }

            //В ином случае - ищем лучший ход для блокирования
            selectedFigure = findBetterStepForBlockOpponent(opponentType);
            mStepCandidates.clear();
        }
        return selectedFigure;
    }

    /**
     * Существует ли фигура
     *
     * @param figure - проверяемая фигура
     * @return - возвращает истину, если фигура не равна null и ложь в противном случае
     */
    private boolean isFigureNotNull(Figure figure)
    {
        return figure != null;
    }

    /**
     * Является ли текущая фигура фигурой оппонента
     *
     * @param targetFigure - проверяемая фигура
     * @param opponentType - тип фигуры у оппонента
     * @return - возвращает истину если является соседом и ложь - если нет
     */
    private boolean figureAreOpponentNeighbor(Figure targetFigure, FigureType opponentType)
    {
        return targetFigure != null && targetFigure.getType().equals(opponentType);
    }

    /**
     * Является ли размер здания критичным (для какого-либо оппонента)
     *
     * @param buildHeight - размер здания
     * @return - возвращает истину, если размер здания превышает размер,
     * задаваемым уровнем сложности
     */
    private boolean isCritical(int buildHeight)
    {
        return buildHeight > mDifficulty.getValue();
    }

    /**
     * Есть ли у данной фигуры в соседях критично-опасные здания противника
     *
     * @param figure       - фигура для проверки
     * @param opponentType - тип фигуры противника
     * @return - возвращает истину, если есть критично-опасные здания противника
     * хотя бы в одной стороне и ложь в противном случае
     */
    private boolean isDangerBuildAround(Figure figure, FigureType opponentType)
    {
        return isCritical(getFiguresCountAtDirection(figure, opponentType, Direction.LEFT, 0))
                || isCritical(getFiguresCountAtDirection(figure, opponentType, Direction.RIGHT, 0))
                || isCritical(getFiguresCountAtDirection(figure, opponentType, Direction.TOP, 0))
                || isCritical(getFiguresCountAtDirection(figure, opponentType, Direction.BOTTOM, 0))
                || isCritical(getFiguresCountAtDirection(figure, opponentType, Direction.LT, 0))
                || isCritical(getFiguresCountAtDirection(figure, opponentType, Direction.RT, 0))
                || isCritical(getFiguresCountAtDirection(figure, opponentType, Direction.LB, 0))
                || isCritical(getFiguresCountAtDirection(figure, opponentType, Direction.RB, 0));
    }

    /**
     * Нахождение "хитрого" хода. Под ним подразумевается игнорирование блокирования не критичного
     * но опасного хода противника, если имеется подходящее здание, в которое при совершении хода
     * найденого здесь, даст выигрышный вариант (противник не сможет закрыть все пути).
     *
     * @param targetStepsHistory - выбраная история ходов
     * @param myType             - ваш тип фигуры
     * @param opponentType       - тип фигуры оппонента
     * @return - возвращает "хитрый" ход, если такой имеется и null в противном случае
     */
    private Figure findCheatingStep(Stack<Figure> targetStepsHistory, FigureType myType,
                                    FigureType opponentType)
    {
        for (Figure figure : mStepCandidates)
        {   //Проверяем, нет ли критичных зданий противника
            if (isDangerBuildAround(figure, opponentType))
            {
                return null;
            }
        }

        Figure cheatingStep;
        for (Figure figure : targetStepsHistory)
        {

            if (getFiguresCountAtDirection(figure, myType, Direction.LEFT, 1) == 2)      //
            {                   //Если высота вашего здания подходит - то получаем фигуру,
                // если соседи на всех концах свободны
                cheatingStep = getFigureIfBothSideAreFreedom(figure, myType, Direction.LEFT, 1);
                if (cheatingStep != null)
                {
                    return cheatingStep;
                }
            }

            if (getFiguresCountAtDirection(figure, myType, Direction.RIGHT, 1) == 2)
            {
                cheatingStep = getFigureIfBothSideAreFreedom(figure, myType, Direction.RIGHT, 1);
                if (cheatingStep != null)
                {
                    return cheatingStep;
                }
            }

            if (getFiguresCountAtDirection(figure, myType, Direction.TOP, 1) == 2)
            {
                cheatingStep = getFigureIfBothSideAreFreedom(figure, myType, Direction.TOP, 1);
                if (cheatingStep != null)
                {
                    return cheatingStep;
                }
            }

            if (getFiguresCountAtDirection(figure, myType, Direction.BOTTOM, 1) == 2)
            {
                cheatingStep = getFigureIfBothSideAreFreedom(figure, myType, Direction.BOTTOM, 1);
                if (cheatingStep != null)
                {
                    return cheatingStep;
                }
            }

            if (getFiguresCountAtDirection(figure, myType, Direction.LT, 1) == 2)
            {
                cheatingStep = getFigureIfBothSideAreFreedom(figure, myType, Direction.LT, 1);
                if (cheatingStep != null)
                {
                    return cheatingStep;
                }
            }

            if (getFiguresCountAtDirection(figure, myType, Direction.RT, 1) == 2)
            {
                cheatingStep = getFigureIfBothSideAreFreedom(figure, myType, Direction.RT, 1);
                if (cheatingStep != null)
                {
                    return cheatingStep;
                }
            }

            if (getFiguresCountAtDirection(figure, myType, Direction.LB, 1) == 2)
            {
                cheatingStep = getFigureIfBothSideAreFreedom(figure, myType, Direction.LB, 1);
                if (cheatingStep != null)
                {
                    return cheatingStep;
                }
            }

            if (getFiguresCountAtDirection(figure, myType, Direction.RB, 1) == 2)
            {
                cheatingStep = getFigureIfBothSideAreFreedom(figure, myType, Direction.RB, 1);
                if (cheatingStep != null)
                {
                    return cheatingStep;
                }
            }
        }
        return null;
    }

    /**
     * Получить фигуру, если соседи на обоих концах издания свободны
     *
     * @param startFigure - начальная фигура
     * @param targetType  - выбраный тип фигуры
     * @param direction   - нужное направление
     * @param buildHeight - высота здания
     * @return - возвращает свободную фигуру, по направлению, переданому функцию, если
     * соседи свободны с обоих сторон и null в противном случае
     */
    private Figure getFigureIfBothSideAreFreedom(Figure startFigure, FigureType targetType,
                                                 Direction direction, int buildHeight)
    {
        Direction oppositeDirection = Direction.getOppositeDirection(direction);

        Figure freedomFigure = findFreedomFigureAroundDangerBuild(startFigure, targetType,
                direction, buildHeight);
        Figure opFreedomFigure = findFreedomFigureAroundDangerBuild(startFigure, targetType,
                oppositeDirection, buildHeight);

        if (freedomFigure != null && opFreedomFigure != null)
        {
            Figure neighbor = freedomFigure.getNeighborFromDirection(direction);
            Figure oppositeNeighbor = opFreedomFigure.getNeighborFromDirection(oppositeDirection);
            if (bothSideAreFreedom(neighbor, oppositeNeighbor))
            {
                return freedomFigure;
            }
        }
        return null;
    }

    /**
     * Проверка, являются ли обе фигуры (фигуры с разных сторон здания) пустыми
     *
     * @param neighborAtDirect - сосед по направлению
     * @param oppositeNeighbor - сосед противоположного направления
     * @return - возвращает истину, если оба свободны и ложь в противном случае
     */
    private boolean bothSideAreFreedom(Figure neighborAtDirect, Figure oppositeNeighbor)
    {
        boolean suitableAtDirection = neighborAtDirect != null &&
                neighborAtDirect.getType().equals(FigureType.UNSELECTED);


        boolean suitableAtOppositeDirection = oppositeNeighbor != null &&
                oppositeNeighbor.getType().equals(FigureType.UNSELECTED);

        return suitableAtDirection && suitableAtOppositeDirection;
    }

    /**
     * Нахождения лучшего хода, для блокирования ходов игрока
     *
     * @param opponentType - тип фигуры противника
     * @return - возвращает лучший ход, для блокирования
     */
    private Figure findBetterStepForBlockOpponent(FigureType opponentType)
    {
        int maxFiguresCount = 0;
        Figure betterStep = null;
        int figuresCount;

        for (Figure figure : mStepCandidates)
        {
            //Проверяем, есть ли критичные здания у противника,
            // которые являются соседями с данной фигурой
            if (!isDangerBuildAround(figure, opponentType))
            {
                //Если нет - проверяем соседей на принадлежность к фигурам оппонента
                figuresCount = 0;
                figuresCount += figureAreOpponentNeighbor(figure.
                        getNeighborFromDirection(Direction.LEFT), opponentType) ? 1 : 0;
                figuresCount += figureAreOpponentNeighbor(figure.
                        getNeighborFromDirection(Direction.RIGHT), opponentType) ? 1 : 0;
                figuresCount += figureAreOpponentNeighbor(figure.
                        getNeighborFromDirection(Direction.TOP), opponentType) ? 1 : 0;
                figuresCount += figureAreOpponentNeighbor(figure.
                        getNeighborFromDirection(Direction.BOTTOM), opponentType) ? 1 : 0;

                figuresCount += figureAreOpponentNeighbor(figure.
                        getNeighborFromDirection(Direction.LT), opponentType) ? 1 : 0;
                figuresCount += figureAreOpponentNeighbor(figure.
                        getNeighborFromDirection(Direction.RT), opponentType) ? 1 : 0;
                figuresCount += figureAreOpponentNeighbor(figure.
                        getNeighborFromDirection(Direction.LB), opponentType) ? 1 : 0;
                figuresCount += figureAreOpponentNeighbor(figure.
                        getNeighborFromDirection(Direction.RB), opponentType) ? 1 : 0;
                //У кого больше соседей, тот и будет лучшим
                if (figuresCount > maxFiguresCount)
                {
                    maxFiguresCount = figuresCount;
                    betterStep = figure;
                }
            } else
            {
                //А если есть критичные - блокируем сразу.
                return figure;
            }
        }
        return betterStep;
    }

    /**
     * Нахождение лучшего хода
     *
     * @param betterStepType - тип фигуры, для которой будет искаться лучший ход
     * @return - лучший ход
     */
    private Figure findBetterStep(FigureType betterStepType)
    {
        Figure selectedFigure = findBetterPlace(betterStepType);
        if (selectedFigure == null)
        {
            selectedFigure = findBetterPlace(FigureType.UNSELECTED);
        }
        return selectedFigure;
    }

    /**
     * Нахождение лучшего места, для хода
     *
     * @param type - тип фигуры, для которой будет находится лучшее место
     * @return - возвращает лучшее место для заданого типа фигуры, или null если не нашел
     */
    private Figure findBetterPlace(FigureType type)
    {
        Figure betterFigure = null;
        int maxCommitFiguresInBuildsCount = 0;
        int totalFiguresInBuildsCount;
        int curCount;
        List<Figure> figureList = mController.getFigureList();

        for (Figure figure : figureList)
        {
            if (figure.getType().equals(FigureType.UNSELECTED))
            {
                totalFiguresInBuildsCount = 0;

                curCount = getFiguresCountAtDirection(figure, type, Direction.LEFT, 1);
                if (curCount == 1) // Если фигура не имеет хотя бы одного такого
                {               // же соседа(она одна) - зануляем
                    curCount = 0;
                } else if (isStepGood(curCount, type))
                {
                    return figure;
                }
                totalFiguresInBuildsCount += curCount;

                curCount = getFiguresCountAtDirection(figure, type, Direction.RIGHT, 1);
                if (curCount == 1)
                {
                    curCount = 0;
                } else if (isStepGood(curCount, type))
                {
                    return figure;
                }
                totalFiguresInBuildsCount += curCount;


                curCount = getFiguresCountAtDirection(figure, type, Direction.TOP, 1);
                if (curCount == 1)
                {
                    curCount = 0;
                } else if (isStepGood(curCount, type))
                {
                    return figure;
                }
                totalFiguresInBuildsCount += curCount;


                curCount = getFiguresCountAtDirection(figure, type, Direction.BOTTOM, 1);
                if (curCount == 1)
                {
                    curCount = 0;
                } else if (isStepGood(curCount, type))
                {
                    return figure;
                }
                totalFiguresInBuildsCount += curCount;


                curCount = getFiguresCountAtDirection(figure, type, Direction.LT, 1);
                if (curCount == 1)
                {
                    curCount = 0;
                } else if (isStepGood(curCount, type))
                {
                    return figure;
                }
                totalFiguresInBuildsCount += curCount;


                curCount = getFiguresCountAtDirection(figure, type, Direction.RT, 1);
                if (curCount == 1)
                {
                    curCount = 0;
                } else if (isStepGood(curCount, type))
                {
                    return figure;
                }
                totalFiguresInBuildsCount += curCount;


                curCount = getFiguresCountAtDirection(figure, type, Direction.LB, 1);
                if (curCount == 1)
                {
                    curCount = 0;
                } else if (isStepGood(curCount, type))
                {
                    return figure;
                }
                totalFiguresInBuildsCount += curCount;


                curCount = getFiguresCountAtDirection(figure, type, Direction.RB, 1);
                if (curCount == 1)
                {
                    curCount = 0;
                } else if (isStepGood(curCount, type))
                {
                    return figure;
                }
                totalFiguresInBuildsCount += curCount;


                if (totalFiguresInBuildsCount > maxCommitFiguresInBuildsCount)
                {
                    maxCommitFiguresInBuildsCount = totalFiguresInBuildsCount;
                    betterFigure = figure;
                }
            }
        }
        return betterFigure;
    }

    /**
     * Проверка, хороший ли ход, если ищется не "пустые" здания
     *
     * @param buildCountAtDirection - кол-во зданий по нужному направлению
     * @param targetType            - искомый тип фигур у зданий
     * @return - возвращает истину, если здание хорошее и ложь в противном случае.
     */
    private boolean isStepGood(int buildCountAtDirection, FigureType targetType)
    {
        return buildCountAtDirection > 2 && !targetType.equals(FigureType.UNSELECTED);
    }


    /**
     * Возвращает кол-во фигур одинакого типа при заданом направлении
     *
     * @param startFigure - начальная фигура
     * @param type        - задаваемый тип фигуры
     * @param direction   - нужное направление
     * @param count       - кол-во уже имеющихся фигур
     *                    (если стартовая фигура включается, нужно ставить 1)
     * @return - возвращает кол-во фигур заданого типа по заданому направлению
     */
    private int getFiguresCountAtDirection(Figure startFigure, FigureType type,
                                           Direction direction, int count)
    {
        if (count < 4)
        {
            Figure figureDirection = startFigure.getNeighborFromDirection(direction);
            if (figureDirection != null)
            {
                if (figureDirection.getType().equals(type))
                {
                    count++;
                    count = getFiguresCountAtDirection(figureDirection, type, direction, count);
                }
            }
        }
        return count;
    }

    /**
     * Собрать подходящие ходы, для блокирования ходов оппонента
     *
     * @param opponentStepsHistory - история ходов оппонента
     */
    private void collectStepsForBlockOpponent(Stack<Figure> opponentStepsHistory)
    {
        for (Figure step : opponentStepsHistory)
        {
            findStepAroundBuilding(step, Direction.LEFT);
            findStepAroundBuilding(step, Direction.RIGHT);
            findStepAroundBuilding(step, Direction.TOP);
            findStepAroundBuilding(step, Direction.BOTTOM);

            findStepAroundBuilding(step, Direction.LT);
            findStepAroundBuilding(step, Direction.RT);
            findStepAroundBuilding(step, Direction.LB);
            findStepAroundBuilding(step, Direction.RB);
        }
    }

    /**
     * Поиск свободного шага, около вражеского здания
     *
     * @param opponentFigure - фигура оппонента
     * @param direction      - направление, в котором будет осуществляться поиск
     */
    private void findStepAroundBuilding(Figure opponentFigure, Direction direction)
    {
        Figure foundStep;
        foundStep = findFreedomFigureBetweenOpponentSteps(opponentFigure, direction);
        if (foundStep != null)
        {
            mStepCandidates.add(foundStep);
        }

        FigureType opponentType = opponentFigure.getType();
        Figure neighbor = opponentFigure.getNeighborFromDirection(direction);
        foundStep = findFreedomFigureAroundDangerBuild(neighbor, opponentType, direction, 1);

        if (isFigureNotNull(foundStep) && !mStepCandidates.contains(foundStep))
        {
            mStepCandidates.add(foundStep);
        }
    }

    /**
     * Найти свободную фигуру, между двумя фигурами оппонента.
     * Работает только при уровне сложности выше легкого
     *
     * @param opponentFigure - вражеская фигура
     * @param direction      - направление в котором будут идти поиски
     * @return - возвращает найденую фигуру, или null в противном случае
     */
    private Figure findFreedomFigureBetweenOpponentSteps(Figure opponentFigure,
                                                         Direction direction)
    {
        FigureType opponentType = opponentFigure.getType();
        if (!mDifficulty.equals(Difficulty.EASY))
        {
            Figure neighbor = opponentFigure.getNeighborFromDirection(direction);
            if (neighbor != null && neighbor.getType().equals(FigureType.UNSELECTED))
            {
                Figure nextAfterNeighbor = neighbor.getNeighborFromDirection(direction);
                if (nextAfterNeighbor != null && nextAfterNeighbor.getType().equals(opponentType))
                {
                    return neighbor;
                }
            }
        }
        return null;
    }

    /**
     * Проверка, является ли здание опасным (критично-опасным)
     *
     * @param lastBuildFigure - последняя найденая фигура здания
     * @param direction       - направление в котором будет осуществляться проверка
     * @param buildHeight     - высота здания
     * @return - возвращает истину если здание опасно(или критично-опасно)
     * или ложь, если не опасно.
     */
    private boolean isDangerous(Figure lastBuildFigure, Direction direction, int buildHeight)
    {
        Figure neighbor = lastBuildFigure.getNeighborFromDirection(direction);
        boolean danger = (buildHeight == mDifficulty.getValue()) && isFigureNotNull(neighbor);
        return isCritical(buildHeight) || danger;
    }

    /**
     * Нахождение свободной фигуры, рядом с опасным зданием
     *
     * @param startFigure  - начальная фигура оппонента
     * @param opponentType - тип фигуры оппонента
     * @param direction    - направление в котором будут поиски
     * @param buildHeight  - высота здания
     * @return - возвращает свободную фигуру, около опасного здания,
     * или null, если свободнйо фигуры не найдено
     */
    private Figure findFreedomFigureAroundDangerBuild(Figure startFigure, FigureType opponentType,
                                                      Direction direction, int buildHeight)
    {
        Figure figureForStep = null;
        if (isFigureNotNull(startFigure))
        {
            FigureType curFigureType = startFigure.getType();

            if (curFigureType.equals(opponentType))
            {
                buildHeight++;
                Figure neighbor = startFigure.getNeighborFromDirection(direction);
                figureForStep = findFreedomFigureAroundDangerBuild(neighbor, opponentType,
                        direction, buildHeight);

            } else if (isDangerous(startFigure, direction, buildHeight) &&
                    curFigureType.equals(FigureType.UNSELECTED))
            {
                figureForStep = startFigure;
            }
        }
        return figureForStep;
    }
}