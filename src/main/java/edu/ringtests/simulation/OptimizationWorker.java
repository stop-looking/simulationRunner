package edu.ringtests.simulation;

import edu.ringtests.file.CsvExplorer;

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
        CsvExplorer explorer = new CsvExplorer(csvFile, false);
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void run() {

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

        private double determineStartPoint(double width, double height) {
            return 0.0;
        }

        /**
         * @return array of calibration data of specific coefficient.
         */
        public Double[][] getCalibrationData(double coeff) {
            return calibrationData.get(coeff);
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
