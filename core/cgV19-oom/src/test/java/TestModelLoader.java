import de.spraener.nxtgen.NextGen;
import org.junit.Test;

public class TestModelLoader {

    @Test
    public void testLoader() throws Exception {
        NextGen.main(
                new String[]{"http://localhost:7000/WebFlow"}
        );
    }

    @Test
    public void testRestDemoLoader() throws Exception {
        NextGen.main(
                new String[]{"http://localhost:7000/de.csp.demo.rest"}
        );
    }

    @Test
    public void testAcitivtyGenerator() throws Exception {
        NextGen.main(
                new String[]{"/activity.oom"}
        );
    }

    @Test
    public void testAcitivtyGeneratorVP() throws Exception {
        NextGen.main(
                new String[]{"http://localhost:7001/de.csp.demo.rest"}
        );
    }

}
