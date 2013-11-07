package edu.ringtests.simulation;

import java.io.File;

/**
 * @author Kamil Sikora
 *         Data: 21.09.13
 *         TODO: Refaktoryzacja klas Simulation i SimulationWorker. Przenieść do Simulation metody odpowiedzialne za zapis, modyfikacje oraz usuwanie symulacji.
 */
public class Simulation {


    private File simulationDir;
    private File frictionFile;
    private String projectName;
    /**
     * Sets simulation read only - cant modify its parameters.
     */
    private boolean readOnly;

    public Simulation(File simulationDir, String projectName, boolean readOnly) {
        this.simulationDir = simulationDir.getParentFile();
        this.projectName = projectName;
        this.readOnly = readOnly;
    }

    @Override
    public String toString() {
        return simulationDir.getName();
    }

    public File getSimulationDir() {
        return simulationDir;
    }

    public File getFrictionFile() {

        return frictionFile;
    }

    public void setFrictionFile(File frictionFile) {
        this.frictionFile = frictionFile;
    }


    public String getName() {
        return simulationDir.getName();
    }

    public void start() throws Exception {
        if (readOnly) {
            throw new Exception("Read only simulation");
        }

    }

    public void delete() throws Exception {
        if (readOnly) {
            throw new Exception("Read only simulation");
        }
    }

    public File saveResults(){
        return null;
    }

    public String getProjectName() {
        return projectName;
    }
}
