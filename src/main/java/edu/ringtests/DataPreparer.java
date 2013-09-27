package edu.ringtests;

import java.io.*;

/**
 * @author Kamil Sikora
 */
public class DataPreparer {
    public static void main(String[] args) {
        int[] columnsIndex = {0, 1, 2, 3, 4};

        double[][] z1Data = new double[0][], z2Data = new double[0][],
                x1Data = new double[0][], x2Data = new double[0][];

        try {
            VtfExplorer z1 = new VtfExplorer(new File("z-11.vtf"), VtfExplorer.HEIGHT_COLUMNS);
            VtfExplorer z2 = new VtfExplorer(new File("z-12.vtf"), VtfExplorer.HEIGHT_COLUMNS);
            VtfExplorer x1 = new VtfExplorer(new File("x-inner1.vtf"), VtfExplorer.WIDTH_COLUMNS);
            VtfExplorer x2 = new VtfExplorer(new File("x-inner2.vtf"), VtfExplorer.WIDTH_COLUMNS);

            z1Data = z1.fetchAll();
            z2Data = z2.fetchAll();

            x1Data = x1.fetchAll();
            x2Data = x2.fetchAll();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuilder outBuilder = new StringBuilder(2000);
        outBuilder.append("Width;Height;\n");
        double[] width = new double[x1Data[2].length];
        double[] height = new double[x1Data[2].length];
        for (int i = 0; i < x1Data[2].length; ++i) {
            width[i] = x1Data[2][i] + x2Data[2][i];
            height[i] = z1Data[2][i] + z2Data[2][i];
            outBuilder.append(width[i]).append(";");
            outBuilder.append(height[i]).append(";\n");
        }


        try {
            String outName = "out.csv";
            BufferedWriter writer = new BufferedWriter(new FileWriter(outName));
            writer.write(outBuilder.toString());
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


