import de.spraener.nxtgen.NextGen;
import org.junit.Test;

public class StartGenerator {

    @Test
    public void testRestModel() {
        NextGen.main(new String[]{"http://localhost:7000/de.csp.demo.rest"});
    }
}
