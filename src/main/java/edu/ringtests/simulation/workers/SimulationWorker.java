package edu.ringtests.simulation.workers;

import edu.ringtests.simulation.Simulation;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Arrays;

/**
 * Interface designed for running different kind of jobs, like generating calibration curves or determining real friction factor.
 *
 * @author Kamil Sikora
 *         Data: 28.09.13
 */
public abstract class SimulationWorker {
    protected static final String RESULTS_PATH = ".\\simulationRunnerResults";

    protected final String SAVE_PROJECT_CMD = "project open %s\n" +
            "simulation activate by name %s\n" +
            "simulation save active\n\n";
    protected final String RUN_SIMULATION_CMD = "setup.bat non forge3 & PreparCalculFg3.exe %s.ref & forge3.exe";

    protected Simulation simulation;
    protected File analysisDir;
    protected String forgePath;
    protected String projectPath;
    protected Logger logger = Logger.getLogger(SimulationWorker.class);

    public SimulationWorker(Simulation simulation, String forgePath) {
        this.simulation = simulation;
        this.analysisDir = new File(simulation.getSimulationDir().getParent() + "\\Analysis\\ResultDataBase\\" + simulation.getName());
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

        /* Create workdir for new simulation */
        recursiveCopy(simulation.getSimulationDir(), dest);
        logger.info(String.format("Copying from %s to %s", simulation.getSimulationDir(), dest.toString()));

        try {

//            File f = new File(getClass().getClassLoader().getResource("setup.bat").toString());
            File f = new File("setup.bat");
            BufferedReader is = new BufferedReader(new FileReader(f));
            BufferedWriter os = new BufferedWriter(new FileWriter(new File(dest, "setup.bat")));
            String line = is.readLine();
            while (line != null) {
                if (line.contains("SET SIM_NAME=\"Speczanie\\\"")) {
                    line = line.replace("Speczanie", dest.getName());
                }
                os.write(line + System.getProperty("line.separator"));

                line = is.readLine();
            }
            os.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

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


    protected abstract void saveResult(double currentFactor, String description);

    protected void setFrictionParameter(double factor) {
        File frictionFile = simulation.getFrictionFile();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(frictionFile));
            StringBuilder builder = new StringBuilder(400);
            String line = reader.readLine();
            do {
                if (line.contains("mbarre")) {
                    line = "mbarre = " + factor;
                }
                builder.append(line).append("\n");
            }
            while ((line = reader.readLine()) != null);
            reader.close();

            FileWriter writer = new FileWriter(frictionFile, false);
            writer.write(builder.toString());
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        forceProjectSave();
    }

    public abstract void run();

    protected void startSimulation(double factor) {
        String cmd = String.format(RUN_SIMULATION_CMD, simulation.getName());
        File dir = new File(analysisDir.getPath() + "-" + factor);
        logger.info("Workdir: " + dir);
//        runCmd(dir, cmd);
//        "setup.bat non forge3 & PreparCalculFg3.exe %s.ref & forge3.exe"
        runCmd(dir, dir + "\\setup.bat", "non", "forge3");
        runCmd(dir, dir + "\\PreparCalculFg3.exe", simulation.getName() + ".ref");
        runCmd(dir, dir + "\\forge3.exe");
    }

    /*TODO przekazywać do simualtionWorker ścieżkę do projektu!*/
    protected void forceProjectSave() {
        String cmd = String.format(SAVE_PROJECT_CMD, simulation.getSimulationDir().getParent() + "\\" + simulation.getProjectName(), simulation.getName());
        try {
            FileWriter cmdFile = new FileWriter(new File(forgePath, "newSim.txt"));
            cmdFile.write(cmd);
            cmdFile.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }

        String s = forgePath + "\\GLPreEngine.exe" + "-command" + "\"cmd newSim.txt\"";
        runCmd(new File(forgePath), forgePath + "\\GLPreEngine.exe", "-command", "\"cmd newSim.txt\"");
    }

    protected void runCmd(File workdir, String... args) {
        ProcessBuilder builder = new ProcessBuilder(args);
        builder.directory(workdir);
        logger.info("RunCmd: " + builder.directory() + "\\ " + Arrays.toString(args));
        Process p = null;
        try {
            p = builder.start();
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            System.out.println("\n\n");
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        System.out.println("\n\n");
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