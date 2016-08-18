package ru.ncedu.frolov.Snake;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;

/**
 * Class for main game's calculations. Calculate position of games objects, score and conditions of the end of the game.
 * @author Frolov Maksim
 * @version 28/05/2016
 */
class Game {
    Game() {
        for (int i = 0; i < 30; i++) {
            gridArray[0][i] = 20 * i + 1;
            gridArray[1][i] = 20 * i + 26;
        }
        hX = gridArray[0][15];
        hY = gridArray[1][15];
        makeFood();
        tailArray.add(new ArrayList<Integer>());
        tailArray.add(new ArrayList<Integer>());
    }

    private int[][] gridArray = new int[2][30];
    static final int LEFT_D = 0;
    static final int UP_D = 1;
    static final int RIGHT_D = 2;
    static final int DOWN_D = 3;
    int course;
    int hX;
    int hY;
    private int hXOld;
    private int hYOld;
    int fY;
    int fX;
    ArrayList<ArrayList<Integer>> tailArray = new ArrayList<>();
    int amount;
    int length = 0;
     boolean endGame;

    /**
     * Utility method serves to check that food icon appears into a snake.
     * @param x X food's coordinate.
     * @param y Y food's coordinate.
     * @return Whether food icon appears with a snake
     */
    private boolean isSnake(int x, int y) {
        if (x == hX && y == hY) {
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (x == tailArray.get(0).get(i) && y == tailArray.get(1).get(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a food for the snake in random place but not into the snake.
     */
    private void makeFood(){
        fX = gridArray[0][new Random().nextInt(30)];
        fY = gridArray[1][new Random().nextInt(30)];
        if (isSnake(fX,fY)){
            makeFood();
        }
    }

    /**
     * Start a new game. With update game's objects positions.
     */
    void makeNewGame() {
        hX = gridArray[0][15];
        hY = gridArray[1][15];
        makeFood();
        endGame = false;
        course = LEFT_D;
        amount = 0;
        length = 0;
    }

    /**
     * Moves snake's head on the game's field.
     */
    void moveHead() {
        for (int i = 2; i < length; i++) {
            if (tailArray.get(0).get(i) == hX && tailArray.get(1).get(i) == hY){
                endGame = true;
                return;
            }
        }

        hXOld = hX;
        hYOld = hY;
        switch (course) {
            case LEFT_D:
                if (hX == gridArray[0][0]) {
                    hX = gridArray[0][29];
                    break;
                }
                hX -= 20;
                break;
            case UP_D:
                if (hY == gridArray[1][0]) {
                    hY = gridArray[1][29];
                    break;
                }
                hY -= 20;
                break;
            case RIGHT_D:
                if (hX == gridArray[0][29]) {
                    hX = gridArray[0][0];
                    break;
                }
                hX += 20;
                break;
            case DOWN_D:
                if (hY == gridArray[1][29]) {
                    hY = gridArray[1][0];
                    break;
                }
                hY += 20;
                break;
        }
        if (hX == fX && hY == fY) {
            makeFood();
            amount += 10;
            createTail();
        }
    }

    /**
     * Adds new tails segment if the snake ate food.
     */
    private void createTail() {
        length += 1;
        switch (course) {
            case LEFT_D: {
                tailArray.get(0).add(hXOld);
                tailArray.get(1).add(hYOld);
                break;
            }
            case UP_D: {
                tailArray.get(1).add(hYOld);
                tailArray.get(0).add(hXOld);
                break;
            }
            case RIGHT_D: {
                tailArray.get(0).add(hXOld);
                tailArray.get(1).add(hYOld);
                break;
            }
            case DOWN_D: {
                tailArray.get(1).add(hYOld);
                tailArray.get(0).add(hXOld);
                break;
            }
        }
    }

    /**
     * Provides snake's tail movement for head;
     */
    void moveTail() {
        Queue<Integer> oldX = new PriorityQueue<>();
        Queue<Integer> oldY = new PriorityQueue<>();
        oldX.add(tailArray.get(0).get(0));
        oldY.add(tailArray.get(1).get(0));

        tailArray.get(0).add(0, hXOld);
        tailArray.get(1).add(0, hYOld);
        for (int i = 1; i < length; i++) {
            oldX.add(tailArray.get(0).get(i));
            oldY.add(tailArray.get(1).get(i));
            tailArray.get(0).add(oldX.remove());
            tailArray.get(1).add(oldY.remove());
        }

    }
}
