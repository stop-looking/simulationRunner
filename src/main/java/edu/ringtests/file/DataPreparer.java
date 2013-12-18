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
        double[] width = new double[x1Data[2].length];
        double[] height = new double[x1Data[2].length];
        for (int i = 0; i < x1Data[2].length; ++i) {
            width[i] = x1Data[2][i] + x2Data[2][i];
            height[i] = z1Data[2][i] + z2Data[2][i];
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

    public static double[][] getDiameterReductionPecentage() {
        return null;
    }
}


