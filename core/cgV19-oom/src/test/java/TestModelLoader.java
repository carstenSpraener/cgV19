import de.spraener.nxtgen.NextGen;
import org.junit.Test;

public class TestModelLoader {

    @Test
    public void testLoader() throws Exception {
        NextGen.main(
                new String[]{"http://localhost:7000/WebFlow"}
        );
    }


}
