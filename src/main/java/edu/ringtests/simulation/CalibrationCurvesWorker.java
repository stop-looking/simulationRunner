package edu.ringtests.simulation;

import java.io.*;

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

    /* TODO zapis parametrów symulacji musi odbywać się w głównym projekcie */
    @Override
    protected void setFrictionParameter(double factor) {
        File frictionFile = simulation.getFrictionFile();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(frictionFile));
            StringBuilder builder = new StringBuilder(400);
            String line = reader.readLine();
            do {
                if (line.contains("mbarre")) {
                    line = "mbarre = " + factor;
                }
                builder.append(line).append("\n");
            }
            while ((line = reader.readLine()) != null);
            reader.close();

            FileWriter writer = new FileWriter(frictionFile, false);
            writer.write(builder.toString());
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        forceProjectSave();
    }

    @Override
    public void run() {
        for (int i = 0; i < factors.length; ++i) {
            double currentFactor = factors[i];

            /* TODO
            * 1 - utworzenie nowej symulacji z nowym wspolczynnikiem
            * 2 - przygotowanie srodowiska - SimulationWorker
            * 3 - wykonanie
            * 4 - zapis wynikow i sprzatanie
            * */

            setFrictionParameter(currentFactor);
            prepareEnviroment(currentFactor);
            startSimulation();
            saveResult(currentFactor);
        }
    }

    @Override
    protected void startSimulation() {

    }
}
