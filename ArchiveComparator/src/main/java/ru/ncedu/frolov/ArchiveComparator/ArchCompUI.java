package ru.ncedu.frolov.ArchiveComparator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;

/**
 * The <code>ArchCompUI</code> class contains methods for creating Archive Comparator GUI.
 * Uses functionality of <code>ArchiveComparator</code> for comparing archives files and
 * creating report.
 * @author Frolov Maksim
 * @version 24/05/2016
 */

public class ArchCompUI {

    /**
     * Create a form with <code>JFileChooser</code> for archives select which can be compared by <code>ArchiveComparator</code>.
     * Form contains the button which can create a report by using <code>getReport</code> method from <code>ArchiveComparator</code>.
     * @throws ClassNotFoundException
     * @throws UnsupportedLookAndFeelException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void createChooser() throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        JFrame window = new JFrame("Archive Comparator");
        JPanel topPanel = new JPanel();
        topPanel.setLayout(null);

        final JButton chooseFirstFile = new JButton("Select the first file");
        chooseFirstFile.setBounds(5, 15, 150, 30);
        final JButton chooseSecondFile = new JButton("Select the second file");
        chooseSecondFile.setBounds(5, 60, 150, 30);

        final JLabel firstLabel = new JLabel("No file selected...");
        firstLabel.setBounds(180, 15, 400, 30);
        JLabel secondLabel = new JLabel("No file selected...");
        secondLabel.setBounds(180, 60, 400, 30);

        final JButton getReport = new JButton("Get report!");
        getReport.setBounds(220, 160, 150, 30);

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        final JFileChooser fcFirst = new JFileChooser();
        final JFileChooser fcSecond = new JFileChooser();
        createFileChListener(fcFirst, chooseFirstFile, firstLabel);
        createFileChListener(fcSecond, chooseSecondFile, secondLabel);
        getRepListener(getReport, firstLabel, secondLabel);

        window.add(topPanel);
        topPanel.add(chooseFirstFile);
        topPanel.add(chooseSecondFile);
        topPanel.add(firstLabel);
        topPanel.add(secondLabel);
        topPanel.add(getReport);
        window.setSize(600, 280);
        window.setResizable(false);
        window.setVisible(true);
        window.setLocationRelativeTo(null);
    }

    /**
     * Create listener for buttons which activates <code>JFileChooser</code>. Checking selected files correctness
     * (*.zip or *.jar types).
     * @param fileChooser JFileChooser which will be activated by button.
     * @param button Button which uses this listner.
     * @param label Label which shows chosen file's path.
     * @throws IllegalArgumentException If chosen file is incorrect.
     */
    public void createFileChListener(final JFileChooser fileChooser, final JButton button, final JLabel label) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setCurrentDirectory(new java.io.File("."));
                fileChooser.setDialogTitle("Select a file");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

                try {
                    if (fileChooser.showOpenDialog(button) == JFileChooser.APPROVE_OPTION) {
                        String path = fileChooser.getSelectedFile().getAbsolutePath();
                        if (path.toLowerCase().endsWith("zip") || path.toLowerCase().endsWith("jar")) {
                            label.setText(fileChooser.getSelectedFile().getAbsolutePath());
                        } else {
                            throw new IllegalArgumentException();
                        }
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, "Incorrect file!");
                }
            }
        });
    }

    /**
     * Create listener for button which calls <code>getReport</code> from <code>ArchiveComparator</code>.
     * Checks selected files if no file chosen throws exception.
     * @param button Button which uses this listener.
     * @param firstLabel Label which contains first file's path.
     * @param secondLabel Label which contains second file's path.
     * @throws IllegalArgumentException If no file selected.
     */
    public void getRepListener(final JButton button, final JLabel firstLabel, final JLabel secondLabel) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if ((firstLabel.getText().toLowerCase().endsWith("zip") || firstLabel.getText().toLowerCase().endsWith("jar"))
                            && (secondLabel.getText().toLowerCase().endsWith("zip") || secondLabel.getText().toLowerCase().endsWith("jar"))) {
                        ArchiveComparator archiveComparator = new ArchiveComparator(firstLabel.getText(), secondLabel.getText());
                        archiveComparator.getReport();
                        System.exit(1);
                    } else {
                        throw new IllegalArgumentException();
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, "Please select a correct files!");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    System.exit(0);
                }
            }
        });
    }
}
