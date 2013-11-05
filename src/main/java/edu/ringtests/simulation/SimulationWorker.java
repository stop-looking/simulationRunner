package edu.ringtests.simulation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.log4j.Logger;

/**
 * Interface designed for running different kind of jobs, like generating calibration curves or determining real friction factor.
 *
 * @author Kamil Sikora
 *         Data: 28.09.13
 */
public abstract class SimulationWorker {
    private static final String RESULTS_PATH = ".\\simulationRunnerResults";

    protected final String SAVE_PROJECT_CMD = "project open %s\n" +
            "simulation activate by name %s\n" +
            "simulation save active";
    protected final String RUN_SIMULATION_CMD = "setup.bat non forge3 & PreparCalculFg3.exe %s.ref & forge3.exe";

    protected Simulation simulation;
    protected File analysisDir;
    protected String forgePath;
    protected String projectPath;
    protected Logger logger = Logger.getLogger(SimulationWorker.class);

    public SimulationWorker(Simulation simulation, String forgePath) {
        this.simulation = simulation;
        this.analysisDir = new File(simulation.getSimulationDir().getPath() + "\\..\\Analysis\\ResultDataBase\\" + simulation.getName());
        this.forgePath = forgePath;
    }

    protected void prepareEnviroment(double currentFactor) {
        File dest = new File(analysisDir.getPath() + "-" + currentFactor);
        if (dest.exists()) {
            logger.info(String.format("Folder %s istnieje.\n", dest.toString()));
            for (File file : dest.listFiles())
                deleteFile(file);
        }
        dest.mkdir();
        logger.info(String.format("Folder %s nie istnieje.\n", dest.toString()));

        recursiveCopy(simulation.getSimulationDir(), dest);
        logger.info(String.format("Kopiowanie z %s do %s", simulation.getSimulationDir(), dest.toString()));
    }

    protected void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
        }
        file.delete();
    }

    protected void saveResult(double currentFactor) {
        File resultDir = new File(RESULTS_PATH);
        if (!resultDir.exists())
            resultDir.mkdir();

        /* TODO podobnie jak wyżej kopiowanie może nie działać */
        File source = new File(analysisDir.getPath() + "-" + currentFactor);
        File destDir = new File(resultDir, simulation.getName() + "-" + currentFactor);
        if (!destDir.exists())
            destDir.mkdir();

        logger.info(String.format("Kopiowanie wynikow z %s do %s", source, destDir));
        recursiveCopy(source, destDir);
        deleteFile(source);
    }

    protected abstract void setFrictionParameter(double factor);

    public abstract void run();

    protected abstract void startSimulation();

    /*TODO przekazywać do simualtionWorker ścieżkę do projektu!*/
    protected void forceProjectSave() {
        String cmd = String.format(SAVE_PROJECT_CMD, simulation.getSimulationDir(), simulation.getName());
    }

    private void recursiveCopy(File fSource, File fDest) {
        try {
            if (fSource.isDirectory()) {
                // A simple validation, if the destination is not exist then create it
                if (!fDest.exists()) {
                    fDest.mkdirs();
                }

                // Create list of files and directories on the current source
                // Note: with the recursion 'fSource' changed accordingly
                String[] fList = fSource.list();

                for (int index = 0; index < fList.length; index++) {
                    File dest = new File(fDest, fList[index]);
                    File source = new File(fSource, fList[index]);

                    // Recursion call take place here
                    recursiveCopy(source, dest);
                }
            } else {
                // Found a file. Copy it into the destination, which is already created in 'if' condition above

                // Open a file for read and write (copy)
                FileInputStream fInStream = new FileInputStream(fSource);
                FileOutputStream fOutStream = new FileOutputStream(fDest);

                // Read 2K at a time from the file
                byte[] buffer = new byte[2048];
                int iBytesReads;

                // In each successful read, write back to the source
                while ((iBytesReads = fInStream.read(buffer)) >= 0) {
                    fOutStream.write(buffer, 0, iBytesReads);
                }

                // Safe exit
                fInStream.close();

                fOutStream.close();
            }
        } catch (Exception ex) {
            // Please handle all the relevant exceptions here
        }
    }
}
