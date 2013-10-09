package edu.ringtests.View;

import edu.ringtests.Simulation;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.*;
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
    private JRadioButton generacjaKrzywychKalibracyjnychRadioButton;
    private JTextField calibrationCoeffsField;
    private JRadioButton wyznaczanieKrzywychEksperymentalnychRadioButton;
    private JTextField dataFileField;
    private JButton chooseDataButton;
    private JButton startButton;

    private final String PROPERTIES_FILE = "properties.xml";

    private Simulation selectedSimulation;

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
                    populateSimulationBox(fc.getSelectedFile().getParent());
                }

            }
        };
    }

    private void populateSimulationBox(String dir) {
        File parent = new File(dir);
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
                simulations.add(new Simulation(f));
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

    private void populateFrictionBox(Simulation selectedSimulation) {
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

        Properties properties = new Properties();
        File propertiesFile = new File(PROPERTIES_FILE);
        if(!propertiesFile.exists()){
            ForgePathDialog dialog = new ForgePathDialog();
            properties.put("forgePath", dialog.getPath());
            try {
                properties.storeToXML(new FileOutputStream(PROPERTIES_FILE), "Properites file");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            try {
                properties.loadFromXML(new FileInputStream(PROPERTIES_FILE));
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainWindow");
        frame.setContentPane(new MainWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
