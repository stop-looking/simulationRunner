package edu.ringtests.test;

import edu.ringtests.simulation.Experiment;
import edu.ringtests.simulation.Simulation;
import edu.ringtests.simulation.workers.OptimizationWorker;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.LinkedList;

/**
 * @author Kamil Sikora
 *         Data: 17.12.13
 */
public class OptimizationWorkerTest {
    @Test
    public void calibrationCurvesFetch() {
        OptimizationWorker.CalibrationCurves cc = new OptimizationWorker.CalibrationCurves(new File("calibration-test.csv"));

        Double[][] calibrationData = cc.getCalibrationData(0.0);
        Double[] expected = new Double[]{0.488593115, -0.41478481};

        Assert.assertArrayEquals(String.valueOf(calibrationData[0]), calibrationData[0], expected);
    }

    @Test
    public void experimentalDataFetch() {
        OptimizationWorker worker = new OptimizationWorker(new Simulation(new File(".//RingTest.tsv//RingTest3d.tpf"), "Speczanie", true), "./forge", new File("calibration-test.csv"), new File("experiment-test.csv"));
        LinkedList<Experiment> experiments = worker.getExperiments();

        Experiment first = experiments.getFirst();
        Experiment last = experiments.getLast();
        Assert.assertTrue(first.toString(), first.getHeight() == 0.493011435832274 && first.getInnerDiameter() == 0.0685131195335275);
        Assert.assertTrue(last.toString(), last.getHeight() == 0.510184595798854 && last.getInnerDiameter() == -0.0361344537815127);
    }

    @Test
    public void startPointTest() {
        OptimizationWorker.CalibrationCurves cc = new OptimizationWorker.CalibrationCurves(new File("calibration-test.csv"));
        double startPoint = cc.determineStartPoint(0.49, -0.15);

        Assert.assertTrue(String.valueOf(startPoint), startPoint == 0.1);

    }
}
