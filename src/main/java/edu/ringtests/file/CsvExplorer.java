package edu.ringtests.file;

import javax.swing.*;
import java.io.*;

/**
 * @author Kamil Sikora
 *         Data: 15.12.13
 */
public class CsvExplorer {
    private final String SEPARATOR = ";";

    private final File csvFile;
    private boolean hasHeader;

    public CsvExplorer(String csvFile, boolean hasHeader) {
        this.csvFile = new File(csvFile);
        this.hasHeader = hasHeader;
    }

    public double[][] fetchAll() {
        BufferedReader reader = null;

        int linesCount = getNumberOfLines();
        int columnsCount = getNumberOfColumns();
        double[][] data = new double[linesCount][columnsCount];
        int j = 0;

        try {
            reader = new BufferedReader(new FileReader(csvFile));

            if (hasHeader)
                reader.readLine();
            String line = reader.readLine();
            while (line != null) {
                String[] tokens = line.split(SEPARATOR);

                for (int k = 0; k < columnsCount; k++) {
                    data[j][k] = Double.parseDouble(tokens[k]);
                }
                line = reader.readLine();
                ++j;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Nie znaleziono pliku wejściowego!", "Błąd", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } catch (IOException e) {
            try {
                reader.close();
                System.exit(-1);
            } catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.exit(-1);
            }
            e.printStackTrace();
            System.exit(-1);
        }

        return data;
    }

    private int getNumberOfColumns() {
        int columns = -1;
        try {
            RandomAccessFile f = new RandomAccessFile(csvFile, "r");
            columns = f.readLine().split(SEPARATOR).length;

            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return columns;
    }

    private int getNumberOfLines() {
        int count = 0;

        try {
            InputStream is = new BufferedInputStream(new FileInputStream(csvFile));
            byte[] c = new byte[1024];

            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }
}
