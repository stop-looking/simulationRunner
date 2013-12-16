package edu.ringtests.simulation;

import java.io.File;

/**
 * @author Kamil Sikora
 *         Data: 11.12.13
 */
public class OptimizationWorker extends SimulationWorker {
    /**
     * Link to calibration file of specific alloy.
     */
    private final File calibrationFile;

    /**
     * CSV file with result experiment. Next columns should be:
     * Sample number, height reduction percentage, width reduction percentage
     */
    private final File experimentResultsFile;

    public OptimizationWorker(Simulation simulation, String forgePath, File calibrationFile, File experimentResultsFile) {
        super(simulation, forgePath);
        this.calibrationFile = calibrationFile;
        this.experimentResultsFile = experimentResultsFile;
    }

    @Override
    protected void saveResult(double currentFactor, String description) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void run() {

    }

    public double findNewFactor(double current, double next) {
        return 0.0;
    }

    private double determineStartPoint(double width, double height) {
        return 0.0;
    }

    private class CalibrationCurves {

    }


}
