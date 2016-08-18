package ru.ncedu.frolov.ArchiveComparator;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.zip.*;

/**
 * The <code>ArchiveComparator</code> class contains basic functionality for comparing two archives and
 * creating report file. Provides checking archives correctness.
 * Provides comparing files inside archives by name, size, hash code and generates readable report.
 * @author Frolov Maksim
 * @version 24/05/2016
 */

public class ArchiveComparator {
    /**
     * Fields that contains archives path.
     */
    private String firstArchive;
    private String secondArchive;

/**
 * Constructor is used to check the correctness of types transferred files and
 * if they are correct sets them to the <code>firstArchive</code> and <code>secondArchive</code> values.
 * @throws IllegalArgumentException If one or both files are incorrect.
 * @param firstArch Path to first archive.
 * @param secondArch Path to second archive.
 */
    public ArchiveComparator(String firstArch, String secondArch) {
        try {
            if ((firstArch.toLowerCase().endsWith("zip") || firstArch.toLowerCase().endsWith("jar"))
                    && (secondArch.toLowerCase().endsWith("zip") || secondArch.toLowerCase().endsWith("jar"))) {
                firstArchive = firstArch;
                secondArchive = secondArch;
            } else {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException ex) {
            System.err.println("Incorrect file!");
        }
    }

    /**
     * Extract the files from the archive, and return them in usable form for further comparing.
     * @param archive Archive that used for extracting files.
     * @return <code>Map</code> which contains archive files name in keys and archive files as <code>ZipEntry</code> in values.
     * @throws IOException If can't read the file.
     */
    public Map<String, ZipEntry> setArchFiles(String archive) throws IOException {
        Map<String, ZipEntry> entries = new Hashtable<>();

        try (ZipInputStream zipStr = new ZipInputStream(new FileInputStream(archive))) {
            ZipEntry entry;
            String name;
            while ((entry = zipStr.getNextEntry()) != null) {
                name = entry.getName();
                entries.put(name.toLowerCase(), entry);
            }
            zipStr.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(0);
        }
        return entries;
    }

    /**
     * Compares archives files and creates List of checked pairs of files in formatted form,
     * which can be added in report.
     * @param firstArch files of first archive in Map representation.
     * @param secondArch files of second archive in Map representation.
     * @return <code>List</code> of checked pairs of files in <code>String</code> format which can be added in report.
     */
    public List<String> compareFiles(Map<String, ZipEntry> firstArch, Map<String, ZipEntry> secondArch) throws NullPointerException, IOException {
        Set<String> firstArchFilesNames = firstArch.keySet();
        Set<String> secondArchFilesNames = secondArch.keySet();
        List<String> checkedFiles = new LinkedList<>();
        Set<String> oldFiles = new LinkedHashSet<>();

        for (String nextFileFirst : firstArchFilesNames) {
            boolean deleted = true;

            for (String nextFileSecond : secondArchFilesNames) {
                if (nextFileFirst.equalsIgnoreCase(nextFileSecond)) {
                    oldFiles.add(nextFileSecond);
                    if (firstArch.get(nextFileFirst).getSize() == secondArch.get(nextFileFirst).getSize()
                            && firstArch.get(nextFileFirst).hashCode() == secondArch.get(nextFileFirst).hashCode()) {
                        deleted = false;
                        continue;
                    }
                    if (firstArch.get(nextFileFirst).getSize() != secondArch.get(nextFileFirst).getSize()
                            || firstArch.get(nextFileFirst).hashCode() != secondArch.get(nextFileFirst).hashCode()) {
                        checkedFiles.add("* " + nextFileFirst + createDelimiter(" ", nextFileFirst.length() + 2) + "|  * " + nextFileFirst);
                        deleted = false;
                    }
                } else if (firstArch.get(nextFileFirst).getSize() == secondArch.get(nextFileSecond).getSize()) {
                    oldFiles.add(nextFileSecond);
                    checkedFiles.add("? " + nextFileFirst + createDelimiter(" ", nextFileFirst.length() + 2) + "|  ? " + nextFileSecond);
                    deleted = false;
                }
            }
            if (deleted)
                checkedFiles.add("- " + nextFileFirst + createDelimiter(" ", nextFileFirst.length() + 2) + "|");
        }

        secondArchFilesNames.removeAll(oldFiles);
        for (String file : secondArchFilesNames) {
            checkedFiles.add(createDelimiter(" ", 0) + "|  + " + file);
        }

        return checkedFiles;
    }

    /**
     * Creates readable report in *.txt form. Based on the comparison of archives files by
     * using a <code>compareFiles</code> method.
     * Open report.txt after created.
     * @throws NullPointerException If archive no file contains.
     * @throws IOException If can't read the file.
     */
    public void getReport() throws IOException {

        try {
            if (firstArchive == null || secondArchive == null) throw new NullPointerException();
            Map<String, ZipEntry> firstArchFiles = setArchFiles(firstArchive);
            Map<String, ZipEntry> secondArchFiles = setArchFiles(secondArchive);

            File report = new File("report.txt");

            BufferedWriter writer = new BufferedWriter(new FileWriter(report));
            writer.write("\"-\" - deleted files");
            writer.newLine();
            writer.append("\"+\" - added files");
            writer.newLine();
            writer.append("\"*\" - updated files");
            writer.newLine();
            writer.append("\"?\" - renamed files");
            writer.newLine();
            writer.newLine();
            writer.append(firstArchive).append(createDelimiter(" ", firstArchive.length())).append("|  ").append(secondArchive);
            writer.newLine();
            writer.append(createDelimiter("-", 0)).append("+").append(createDelimiter("-", 0));

            for (String file : compareFiles(firstArchFiles, secondArchFiles)) {
                writer.newLine();
                writer.append(file);
            }
            writer.close();
            Desktop.getDesktop().open(report);
            System.exit(0);
        } catch (NullPointerException ex) {
            System.err.println("Select correct files");
            System.exit(0);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.exit(0);
        }
    }

    /**
     * Utility method. Creates delimiters which improve readability of report. It's necessary to align columns
     * of files in report.
     * @param symbol Type of delimiter.
     * @param delimiterLength Count of delimiters necessary to align columns.
     * @return Delimiter that can be used in report.
     */
    private String createDelimiter(String symbol, int delimiterLength) throws IOException {
        String delimiters = "";

        int length = firstArchive.length() > secondArchive.length() ? firstArchive.length() : secondArchive.length();
        for (String file : setArchFiles(firstArchive).keySet()) {
            if (file.length() > length) length = file.length();
        }

        for (int i = 0; i < (length + 5); i++) {
            delimiters += symbol;
        }

        return delimiters.substring(delimiterLength);
    }
}
