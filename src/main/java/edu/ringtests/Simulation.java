package edu.ringtests;

import java.io.File;

/**
 * @author Kamil Sikora
 *         Data: 21.09.13
 */
public class Simulation {
    private File simulationDir;
    private File frictionFile;

    public Simulation(File simulationDir) {
        this.simulationDir = simulationDir.getParentFile();

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
}
