package edu.ringtests.view;

import edu.ringtests.VtfExplorer;
import edu.ringtests.simulation.CalibrationCurvesWorker;
import edu.ringtests.simulation.Simulation;
import edu.ringtests.simulation.SimulationWorker;
import org.apache.log4j.BasicConfigurator;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

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
    private JTextField dataFileField;
    private JButton chooseDataButton;
    private JButton startButton;
    private JFrame frame;

    private final String PROPERTIES_FILE = "properties.xml";
    private final String FORGE_PATH_KEY = "forgePath";

    private Simulation selectedSimulation;
    private String forgePath;
    private String projectName;

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
                    textField.setText(fc.getSelectedFile().getPath());
                    populateSimulationBox(fc.getSelectedFile());
                }

            }
        };
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
        chooseDataButton.addActionListener(createFileChooseDialog("csv", "Plik CSV", dataFileField));
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
                    worker.run();
                }
            }
        });

        frame = new JFrame("Simulation Runner");
        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        loadProperties();

        frame.setVisible(true);
    }

    private void loadProperties() {
        Properties properties = new Properties();
        File propFile = new File(PROPERTIES_FILE);
        if (!propFile.exists()) {
            ForgePathDialog dialog = new ForgePathDialog();
            properties.put(FORGE_PATH_KEY, dialog.getPath());
            try {
                properties.storeToXML(new FileOutputStream(PROPERTIES_FILE), "simulationRunner properties file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                properties.loadFromXML(new FileInputStream(PROPERTIES_FILE));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        forgePath = properties.getProperty(FORGE_PATH_KEY);
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        new MainWindow();
    }

}
