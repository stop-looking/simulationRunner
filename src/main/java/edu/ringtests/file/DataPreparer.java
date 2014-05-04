package edu.ringtests.file;

import java.io.*;

/**
 * @author Kamil Sikora
 */
public class DataPreparer {
    /**
     * Method save results of simulation in destined csv file.
     *
     * @param source path of simulation analysis directory.
     * @param dest   destination csv file
     * @param factor friction factor
     */
    public static void extractData(File source, File dest, double factor) {

        double[][] z1Data = new double[0][], z2Data = new double[0][],
                x1Data = new double[0][], x2Data = new double[0][];

        try {
            VtfExplorer z1 = new VtfExplorer(new File(source, "results\\z-11.vtf"), VtfExplorer.HEIGHT_COLUMNS);
            VtfExplorer z2 = new VtfExplorer(new File(source, "results\\z-12.vtf"), VtfExplorer.HEIGHT_COLUMNS);
            VtfExplorer x1 = new VtfExplorer(new File(source, "results\\x-inner1.vtf"), VtfExplorer.WIDTH_COLUMNS);
            VtfExplorer x2 = new VtfExplorer(new File(source, "results\\x-inner2.vtf"), VtfExplorer.WIDTH_COLUMNS);

            z1Data = z1.fetchAll();
            z2Data = z2.fetchAll();

            x1Data = x1.fetchAll();
            x2Data = x2.fetchAll();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        StringBuilder outBuilder = new StringBuilder(2000);
        outBuilder.append("Width;Height;\n");
        double[] width = new double[x1Data.length];
        double[] height = new double[x1Data.length];
        for (int i = 0; i < x1Data.length; ++i) {
            width[i] = x1Data[i][2] + x2Data[i][2];
            height[i] = z1Data[i][2] + z2Data[i][2];
            outBuilder.append(width[i]).append(";");
            outBuilder.append(height[i]).append(";\n");
        }


        try {
            File outName = new File("out-" + factor + ".csv");
            BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
            writer.write(outBuilder.toString());
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static double[][] getDiameterReductionPecentage(File source) {
        double[][] z1Data = new double[0][], z2Data = new double[0][],
                x1Data = new double[0][], x2Data = new double[0][];

        try {
            VtfExplorer z1 = new VtfExplorer(new File(source, "results\\z-11.vtf"), VtfExplorer.HEIGHT_COLUMNS);
            VtfExplorer z2 = new VtfExplorer(new File(source, "results\\z-12.vtf"), VtfExplorer.HEIGHT_COLUMNS);
            VtfExplorer x1 = new VtfExplorer(new File(source, "results\\x-inner1.vtf"), VtfExplorer.WIDTH_COLUMNS);
            VtfExplorer x2 = new VtfExplorer(new File(source, "results\\x-inner2.vtf"), VtfExplorer.WIDTH_COLUMNS);

            z1Data = z1.fetchAll();
            z2Data = z2.fetchAll();

            x1Data = x1.fetchAll();
            x2Data = x2.fetchAll();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        double[][] result = new double[x1Data.length][2];
        double initialHeight = z1Data[0][2] + z2Data[0][2];
        double initialWidth = x1Data[0][2] + x2Data[0][2];

        for (int i = 1; i < x1Data.length; ++i) {
            result[i][0] = (initialHeight - z1Data[i][2] + z2Data[i][2]) / initialHeight;
            result[i][1] = (initialWidth - x1Data[i][2] + x2Data[i][2]) / initialWidth;
        }

        return result;
    }

    public static void main(String[] args) {
        extractData(new File("./"), new File("./out.csv"), 0.0);
    }
}


