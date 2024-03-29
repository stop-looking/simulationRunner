package edu.ringtests.view;

import edu.ringtests.simulation.Simulation;
import edu.ringtests.simulation.workers.CalibrationCurvesWorker;
import edu.ringtests.simulation.workers.OptimizationWorker;
import edu.ringtests.simulation.workers.SimulationWorker;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author Kamil Sikora
 *         Data: 27.09.13
 */
public class MainWindow {
    private JPanel mainPanel;
    private JComboBox simulationBox;
    private JComboBox frictionBox;
    private JTextField projectField;
    private JButton chooseProjectButton;
    private JRadioButton calibrationCurvesButtion;
    private JTextField calibrationCoeffsField;
    private JRadioButton optimalizationButton;
    private JTextField experimentPathField;
    private JButton chooseExperimentButton;
    private JButton startButton;
    private JTextField calibrationPathField;
    private JButton chooseCalibrationDataButton;
    private JFrame frame;

    private final String PROPERTIES_FILE = "properties.xml";
    private final String FORGE_PATH = "forgePath";
    private static final String LAST_PROJECT = "last_project_path";

    private Simulation selectedSimulation;
    private String forgePath;
    private String projectName;

    private Configuration config;
    private static Logger logger = Logger.getLogger(MainWindow.class);

    private ActionListener createFileChooseDialog(final String extension, final String description, final JTextField textField) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser(new File(".").getAbsolutePath());
                FileFilter filter = new FileNameExtensionFilter(description, extension);
                fc.setFileFilter(filter);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.showOpenDialog(null);
                if (fc.getSelectedFile() != null) {
                    saveLastProjectPath(fc.getSelectedFile().getPath());
                    textField.setText(fc.getSelectedFile().getPath());
                    populateSimulationBox(fc.getSelectedFile());
                }

            }
        };
    }

    private void saveLastProjectPath(String path) {
        config.addProperty(Configuration.LAST_PROJECT_KEY, path);
    }

    private void populateSimulationBox(File projectFile) {
        File parent = new File(projectFile.getParent());
        File[] dirlist = parent.listFiles(new java.io.FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        ArrayList<Simulation> simulations = new ArrayList<Simulation>();
        for (File file : dirlist) {
            File[] files = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.contains("tsf");
                }
            });

            for (File f : files)
                simulations.add(new Simulation(f, projectFile.getName(), false));
        }

        if (simulations.size() > 0) {
            ComboBoxModel model = new DefaultComboBoxModel(simulations.toArray());
            simulationBox.setModel(model);
            simulationBox.validate();
            simulationBox.setEnabled(true);
            selectedSimulation = (Simulation) simulationBox.getSelectedItem();
            populateFrictionBox(selectedSimulation);
        }
    }

    private void populateSimulationBox(String path) {
        populateSimulationBox(new File(path));
    }


    private void populateFrictionBox(final Simulation selectedSimulation) {
        File[] files = selectedSimulation.getSimulationDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains("tff");
            }
        });

        ComboBoxModel model = new DefaultComboBoxModel(files);
        frictionBox.setModel(model);
        selectedSimulation.setFrictionFile((File) frictionBox.getSelectedItem());
        frictionBox.setEnabled(files.length > 0 ? true : false);
        frictionBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedSimulation.setFrictionFile((File) frictionBox.getSelectedItem());
            }
        });
    }

    public MainWindow() {
        config = new Configuration();

        chooseCalibrationDataButton.addActionListener(createFileChooseDialog("csv", "Plik CSV", calibrationPathField));
        chooseExperimentButton.addActionListener(createFileChooseDialog("csv", "Plik CSV", experimentPathField));
        chooseProjectButton.addActionListener(createFileChooseDialog("tpf", "Plik projektu Forge3", projectField));

        simulationBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                selectedSimulation = (Simulation) e.getItem();
                populateFrictionBox(selectedSimulation);
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimulationWorker worker = null;
                if (calibrationCurvesButtion.isSelected()) {
                    String[] factorsString = calibrationCoeffsField.getText().split(" ");
                    double[] factors = new double[factorsString.length];

                    for (int i = 0; i < factors.length; i++)
                        factors[i] = Double.parseDouble(factorsString[i]);

                    worker = new CalibrationCurvesWorker(selectedSimulation, factors, forgePath);

                }
                if (optimalizationButton.isSelected()) {
                    if (experimentPathField.getText().isEmpty() || calibrationPathField.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Nie wybrano pliku.", "Błąd", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    File calibrationFile = new File(calibrationPathField.getText());
                    File experimentFile = new File(experimentPathField.getText());
                    worker = new OptimizationWorker(selectedSimulation, forgePath, calibrationFile, experimentFile);
                }

                if (worker != null)
                    worker.run();
            }
        });

        frame = new JFrame("Simulation Runner");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                config.save();
                super.windowClosing(e);
            }
        });
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        loadForgePath();

        if (config.getProperty(LAST_PROJECT) != null) {
            projectField.setText(config.getProperty(LAST_PROJECT));
            populateSimulationBox(config.getProperty(LAST_PROJECT));
        }

        frame.setVisible(true);
    }

    private void loadForgePath() {
        if (config.getProperty(FORGE_PATH) == null) {
            ForgePathDialog dialog = new ForgePathDialog();
            config.addProperty(FORGE_PATH, dialog.getPath());
            forgePath = dialog.getPath();
        } else
            forgePath = config.getProperty(FORGE_PATH);
    }

    public static void main(String[] args) {
        URL resource = Loader.getResource("configs/log4j-conf.xml");
        DOMConfigurator.configure(resource);
        logger.info("simulationRunner initialized");
        new MainWindow();
    }

}
