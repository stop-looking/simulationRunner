package edu.ringtests.simulation.workers;

import edu.ringtests.file.CsvExplorer;
import edu.ringtests.simulation.Experiment;
import edu.ringtests.simulation.Simulation;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

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

    private CalibrationCurves calibrationData;
    private LinkedList<Experiment> experiments;

    public OptimizationWorker(Simulation simulation, String forgePath, File calibrationFile, File experimentResultsFile) {
        super(simulation, forgePath);
        this.calibrationFile = calibrationFile;
        this.calibrationData = new CalibrationCurves(calibrationFile);

        this.experimentResultsFile = experimentResultsFile;
        experiments = fetchExperiments(experimentResultsFile);
    }

    private LinkedList<Experiment> fetchExperiments(File csvFile) {
        CsvExplorer explorer = new CsvExplorer(csvFile, true);
        String line = null;
        LinkedList<Experiment> result = new LinkedList<>();
        while ((line = explorer.fetchRawLine()) != null) {
            String[] tokens = line.split(CsvExplorer.SEPARATOR);
            String name = tokens[0];
            double height = Double.parseDouble(tokens[1]);
            double width = Double.parseDouble(tokens[2]);
            result.add(new Experiment(name, height, width));
        }

        return result;
    }

    @Override
    protected void saveResult(double currentFactor, String description) {


    }

    @Override
    public void run() {
        double eps = 0.007;
        double maxIter = 15;
        for (Experiment experiment : experiments) {
            double currentFactor = calibrationData.determineStartPoint(experiment.getHeight(), experiment.getInnerDiameter());
            Double[] calibration = calibrationData.findClosestDiameter(currentFactor, experiment);
            logger.info(String.format("Found closest height: %f and width: %f for m=%f for experiment: %s", calibration[0], calibration[1], currentFactor, experiment));

            int iter = 0;
            while (iter < maxIter) {
                long start = System.currentTimeMillis() / 1000;
                setFrictionParameter(currentFactor);
                prepareEnviroment(currentFactor);
                startSimulation(currentFactor);
                long end = System.currentTimeMillis() / 1000;
                logger.info(String.format("Simulation time for %s sample and m=%f: %d seconds", experiment.getName(), currentFactor, start - end));

                CalibrationCurves result = getResult(currentFactor);
                if (eps >= absDifference(experiment.getInnerDiameter(), calibration[1])) {
                    /* cieszymy się*/
                    break;
                }

            }
        }
    }

    private CalibrationCurves getResult(double currentFactor) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    private static double absDifference(double a, Double b) {
        return Math.abs(Math.abs(a) - Math.abs(b));
    }

    public double findNewFactor(double current, double next) {
        return 0.0;
    }

    public LinkedList<Experiment> getExperiments() {
        return experiments;
    }

    /**
     * Contains information about calibration curves related to specific material.
     */
    public static class CalibrationCurves {
        /**
         * HashMap caointaing calibration data. Key is friction factor, value is two dimensional array where <i>array[][0]</i>
         * is per cent of height redution and <i>array[][1]</i> is per cent of inner diameter reduction.
         */
        HashMap<Double, Double[][]> calibrationData;

        public CalibrationCurves(File calibrationFile) {
            calibrationData = fetchCalibrationData(calibrationFile);
        }

        /**
         * Fetches whole content of calibration file.
         */
        private HashMap<Double, Double[][]> fetchCalibrationData(File calibrationFile) {
            CsvExplorer explorer = new CsvExplorer(calibrationFile, true);
            HashMap<Double, ValuePairs> map = new HashMap<>();
            String line = null;
            while ((line = explorer.fetchRawLine()) != null) {
                double[] tokens = convertToNumeric(line);
                Double coeff = tokens[0];
                if (map.containsKey(coeff)) {
                    map.get(coeff).add(tokens[1], tokens[2]);
                } else {
                    ValuePairs vp = new ValuePairs(tokens[1], tokens[2]);
                    map.put(coeff, vp);
                }
            }

            return toFinal(map);
        }

        /**
         * Converts temporary HashMap to final form.
         */
        private HashMap<Double, Double[][]> toFinal(HashMap<Double, ValuePairs> map) {
            HashMap<Double, Double[][]> result = new HashMap<>();
            for (Double coeff : map.keySet())
                result.put(coeff, map.get(coeff).toArray());

            return result;
        }

        /**
         * Converts raw line to tokens and further to numeric tokens.
         *
         * @return tokens converted to numerical form.
         */
        private double[] convertToNumeric(String line) {
            double[] tokens = new double[3];

            int i = 0;
            for (String s : line.split(CsvExplorer.SEPARATOR)) {
                tokens[i] = Double.parseDouble(s);
                ++i;
            }

            return tokens;
        }

        /**
         * @return friction factor that is closest to experimental result.
         */
        public double determineStartPoint(final double height, final double width) {
            HashMap<Double, Integer> closestArgs = new HashMap<>();
            for (Double frictionFact : calibrationData.keySet()) {
                closestArgs.put(frictionFact, findClosestArg(height, calibrationData.get(frictionFact)));
            }

            double closestFactor = 0.0;
            double absWidth = Math.abs(width);
            double minDist = absDifference(calibrationData.get(closestFactor)[closestArgs.get(0.0)][1], absWidth);
            for (Double frictionFact : closestArgs.keySet()) {
                double dist = absDifference(calibrationData.get(frictionFact)[closestArgs.get(frictionFact)][1], absWidth);
                if (dist < minDist) {
                    minDist = dist;
                    closestFactor = frictionFact;
                }
            }

            return closestFactor;
        }

        /**
         * @param value compared value (height).
         * @param array array with calibration data.
         * @return index of closest argment from given array.
         */
        private int findClosestArg(double value, Double[][] array) {
            int index = 0;
            double min = absDifference(array[0][0], value);
            for (int i = 1; i < array.length; ++i) {
                double dist = absDifference(array[i][0], value);
                if (dist < min) {
                    min = dist;
                    index = i;
                }
            }

            return index;
        }

        /**
         * @return array of calibration data of specific coefficient.
         */
        public Double[][] getCalibrationData(double coeff) {
            return calibrationData.get(coeff);
        }

        /**
         * Finds closest diameter reduction to experimental results, for specified friction factor from calibration data.
         *
         * @return arrray of two elements with height and diameter reduction closest to experiment. First element is height reduction, second diameter reduction.
         */
        public Double[] findClosestDiameter(double frictionFactor, Experiment experiment) {
            Double[][] calibration = calibrationData.get(frictionFactor);
            int index = findClosestArg(experiment.getHeight(), calibration);

            return calibration[index];
        }

        /**
         * Class used as temporary container for function arguments and coresponding values.
         */
        private class ValuePairs {
            private LinkedList<Double> arguments;
            private LinkedList<Double> values;

            public ValuePairs(double arg, double val) {
                arguments = new LinkedList<>();
                values = new LinkedList<>();
                arguments.add(arg);
                values.add(val);
            }

            public void add(double arg, double val) {
                arguments.add(arg);
                values.add(val);
            }

            public Double[][] toArray() {
                Double[][] result = new Double[values.size()][2];

                for (int i = 0; i < values.size(); ++i) {
                    result[i][0] = arguments.get(i);
                    result[i][1] = values.get(i);
                }

                return result;
            }
        }
    }


}
