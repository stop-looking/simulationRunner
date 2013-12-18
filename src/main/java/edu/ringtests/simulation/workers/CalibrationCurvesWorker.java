package edu.ringtests.simulation.workers;

import edu.ringtests.file.DataPreparer;
import edu.ringtests.simulation.Simulation;

import javax.swing.*;
import java.io.File;

/**
 * @author Kamil Sikora
 *         Data: 10.10.13
 */
public class CalibrationCurvesWorker extends SimulationWorker {
    private double[] factors;

    public CalibrationCurvesWorker(Simulation simulation, double[] factors, String forgePath) {
        super(simulation, forgePath);
        this.factors = factors;
    }

    protected void saveResult(double currentFactor) {
        saveResult(currentFactor, null);
    }

    @Override
    protected void saveResult(double currentFactor, String description) {
        File resultDir = new File(RESULTS_PATH);
        if (!resultDir.exists())
            resultDir.mkdir();

        if (description == null)
            description = "out";

        File source = new File(analysisDir.getPath() + "-" + currentFactor);
        File destDir = new File(resultDir, description + "-" + currentFactor + ".csv");
        /*if (!destDir.exists())
            destDir.mkdir();
        recursiveCopy(source, destDir);*/

        DataPreparer.extractData(source, destDir, currentFactor);

        logger.info(String.format("Kopiowanie wynikow z %s do %s", source, destDir));
        deleteFile(source);
    }

    @Override
    public void run() {
        for (int i = 0; i < factors.length; ++i) {
            double currentFactor = factors[i];
            String simName = simulation.getName() + "-" + currentFactor;

            /*
            * 1 - utworzenie nowej symulacji z nowym wspolczynnikiem
            * 2 - przygotowanie srodowiska - SimulationWorker
            * 3 - wykonanie
            * 4 - zapis wynikow i sprzatanie
            * */

            long start = System.currentTimeMillis() / 1000;
            setFrictionParameter(currentFactor);
            prepareEnviroment(currentFactor);
            startSimulation(currentFactor);
            saveResult(currentFactor);
            long end = System.currentTimeMillis() / 1000;
            logger.info(String.format("Czas symulacji dla wspolczynnika %f: %d sekund", currentFactor, start - end));
        }
        JOptionPane.showMessageDialog(null, "Obliczenia zakończone");
    }

}
