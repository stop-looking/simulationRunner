package edu.ringtests;

import javax.swing.*;
import java.io.*;
import java.util.logging.Logger;

/**
 * @author Kamil Sikora
 *         Data: 19.09.13
 */
public class VtfExplorer {
    public static final int[] WIDTH_COLUMNS = {0, 1, 2};
    public static final int[] HEIGHT_COLUMNS = {0, 1, 4};

    private final File vtfFile;
    private int[] columnsToFetch;
    Logger logger;


    public VtfExplorer(File vtfFile, int[] columnsToFetch) throws FileNotFoundException {
        logger = Logger.getLogger(getClass().getName());
        if (!vtfFile.exists()) {
            logger.severe("File not found: " + vtfFile);
            throw new FileNotFoundException();
        }

        this.vtfFile = vtfFile;
        this.columnsToFetch = columnsToFetch;
    }

    public double[][] fetchAll() {
        int omited = 0;
        BufferedReader reader = null;

        int linesCount = getNumberOfLines() - 9;
        double[][] data = new double[columnsToFetch.length][linesCount];
        int j = 0;

        try {
            reader = new BufferedReader(new FileReader(vtfFile));

            String line = reader.readLine();
            while (line != null) {
                String[] tokens = line.split("\\t");
                if (tokens.length < 20 || !isNumeric(tokens[0])) {
                    line = reader.readLine();
                    ++omited;
                    continue;
                }

                for (int i = 0; i < columnsToFetch.length; i++) {
                    data[i][j] = Double.parseDouble(tokens[columnsToFetch[i]]);
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

        return toPositive(data);
    }

    private double[][] toPositive(double[][] data) {
        for (int i = 0; i < data.length; ++i)
            for (int j = 0; j < data[i].length; ++j)
                if (data[i][j] < 0)
                    data[i][j] *= -1;

        return data;
    }

    public boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private int getNumberOfLines() {
        int count = 0;

        try {
            InputStream is = new BufferedInputStream(new FileInputStream(vtfFile));
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
