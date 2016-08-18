package ru.ncedu.frolov.ArchiveComparator;

import javax.swing.*;
import java.io.IOException;

/**
 * The <code>Main</code> class using functionality of <code>ArchiveComparator</code> and <code>ArchCompUI</code>. It implements comparison of
 * two archives (* .zip or * .jar) and output information into report file (report.txt). It meets the requirements
 * to support two ways to enter the archives path.
 * @author Frolov Maksim
 * @version 24/05/2016
 */

public class Main {
    /**
     * Checks the number of arguments(archives path) and depending on the results:
     * <p>- 2 args - creates a report of comparing two files located in the paths;
     * <p>- 0 args - starts a graphical version of the app;
     * <p>- any other number of arguments throws <code>IllegalArgumentException</code>;
     * @throws IllegalArgumentException If wrong args number;
     * */
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException,
            InstantiationException, IllegalAccessException {

        try {
            if (args.length == 2) {
                ArchiveComparator archiveComparator = new ArchiveComparator(args[0], args[1]);
                archiveComparator.getReport();
            } else if (args.length == 0) {
                ArchCompUI archCompUI = new ArchCompUI();
                archCompUI.createChooser();
            } else {
                throw new IllegalArgumentException();
            }
        }catch (IllegalArgumentException ex){
            System.err.println("Wrong args number!");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
