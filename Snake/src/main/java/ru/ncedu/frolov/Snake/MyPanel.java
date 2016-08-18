package ru.ncedu.frolov.Snake;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The <code>MyPanel</code> class contains methods for draws game window's elements and data exchange
 * with <code>Game</code> which doing game information calculation. Contains parameters for panel's components.
 * Has class MyKey which needed to control snake.
 * @author Frolov Maksim
 * @version 28/05/2016
 */
class MyPanel extends JPanel {
    MyPanel() {
        loadImgs();
        createComp();
        createListeners();
    }

    private Timer tmDraw;
    private Timer tmUpdate;
    private BufferedImage background;
    private BufferedImage body;
    private BufferedImage head;
    private BufferedImage food;
    private BufferedImage endgame;
    private JLabel score;
    private JButton newGame;
    private JButton exit;
    private Game myGame = new Game();

    /**
     * Creates listeners for buttons and timers. Timers needed for refresh game's data.
     */
    private void createListeners() {

        ActionListener newG = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                myGame.makeNewGame();
                repaint();
                addKeyListener(new MyKey());
                tmDraw.start();
            }
        };
        ActionListener strt = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                score.setText("Score: " + myGame.amount);
                myGame.moveHead();
                repaint();
            }
        };
        ActionListener tailM = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myGame.moveTail();
                repaint();
            }
        };

        tmUpdate = new Timer(50, tailM);
        tmDraw = new Timer(50, strt);
        newGame.setFocusable(false);
        exit.setFocusable(false);
        newGame.addActionListener(newG);
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    /**
     * Loads images (Snake's head, Snake's body, game's background, food for Snake and end game picture).
     * Images will be draw in game's window.
     */
    private void loadImgs() {
        try {
            background = ImageIO.read(new File("Background.bmp"));
            body = ImageIO.read(new File("Body.bmp"));
            head = ImageIO.read(new File("Head.png"));
            food = ImageIO.read(new File("Food.png"));
            endgame = ImageIO.read(new File("EndGame.bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates components for game panel ant attributes for them.
     */
    private void createComp() {
        setLayout(null);
        setFocusable(true);
        score = new JLabel("Score: 0");
        newGame = new JButton("New game");
        exit = new JButton("Exit");

        score.setFont(new Font("Verdana", Font.PLAIN, 22));
        newGame.setFont(new Font("Verdana", Font.PLAIN, 12));
        exit.setFont(new Font("Verdana", Font.PLAIN, 12));
        newGame.setBounds(650, 25, 110, 40);
        exit.setBounds(650, 75, 110, 40);
        score.setBounds(640, 140, 150, 40);
        add(newGame);
        add(exit);
        add(score);

    }

    /**
     * Draws games's images according to needs of the game. Draws snake's head and body moving.
      * @param graphics
     */
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.drawImage(background, 0, 0, null);
        g2d.drawImage(head, myGame.hX, myGame.hY, null);
        g2d.drawImage(food, myGame.fX, myGame.fY, null);

        if (myGame.length != 0) {
            tmUpdate.start();
            for (int i = 0; i < myGame.length; i++) {
                g2d.drawImage(body, myGame.tailArray.get(0).get(i), myGame.tailArray.get(1).get(i), null);
            }
        }
        if (myGame.endGame) {
            tmDraw.stop();
            tmUpdate.stop();
            g2d.drawImage(endgame, 0, 120, null);
        }
    }

    /**
     * Sets keys binding for control the snake. Snake change its course according to keys that were pressed.
     */
    private class MyKey extends KeyAdapter {

        public void keyPressed(KeyEvent event) {
            int key = event.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (myGame.course != Game.RIGHT_D)) {
                myGame.course = Game.LEFT_D;
            }

            if ((key == KeyEvent.VK_UP) && (myGame.course != Game.DOWN_D)) {
                myGame.course = Game.UP_D;
            }
            if ((key == KeyEvent.VK_RIGHT) && (myGame.course != Game.LEFT_D)) {
                myGame.course = Game.RIGHT_D;
            }

            if ((key == KeyEvent.VK_DOWN) && (myGame.course != Game.UP_D)) {
                myGame.course = Game.DOWN_D;
            }
        }
    }


}
