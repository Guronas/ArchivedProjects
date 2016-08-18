package ru.ncedu.frolov.Snake;

import javax.swing.*;
import java.awt.*;

/**
 * The <code>Main</code> class which starts the game "Snake".
 * @author Frolov Maksim
 * @version 28/05/2016
 */
public class Snake {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MyFrame().setVisible(true);
            }
        });
    }
}
