import edu.ringtests.file.CsvExplorer;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kamil Sikora
 *         Data: 16.12.13
 */
public class CsvExplorerTest {
    @Test
    public void readTest() {
        CsvExplorer explorer = new CsvExplorer("test.csv", true);
        double[][] data = explorer.fetchAll();
        double[] expected = {1, 2, 3};
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 3; ++j)
                Assert.assertTrue(data[i][j] == expected[i]);
    }
}
