import edu.ringtests.simulation.OptimizationWorker;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * @author Kamil Sikora
 *         Data: 17.12.13
 */
public class CalibrationCurvesTest {
    @Test
    public void fetchTest() {
        OptimizationWorker.CalibrationCurves cc = new OptimizationWorker.CalibrationCurves(new File("calibration-test.csv"));

        Double[][] calibrationData = cc.getCalibrationData(0.0);
        Double[] expected = new Double[]{-0.42256962, 0.493612288};

        Assert.assertArrayEquals(String.valueOf(calibrationData[0]), calibrationData[0], expected);
    }
}
