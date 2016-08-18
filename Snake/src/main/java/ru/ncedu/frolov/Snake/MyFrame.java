package ru.ncedu.frolov.Snake;

import javax.swing.*;

/**
 *  The <code>MyFrame</code> class contains instructions for create game window
 *  through <code>JFrame</code> with <code>JPanel</code> classes.
 * @author Frolov Maksim
 * @version 28/05/2016
 */
public class MyFrame extends JFrame{

    /**
     * Create game window and set parameters for it.
     */
    public MyFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Game \"Snake\"");
        setContentPane(new MyPanel());
        setSize(800, 670);
        setResizable(false);
        setLocationRelativeTo(null);
    }
}
