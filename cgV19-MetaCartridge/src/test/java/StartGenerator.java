import de.spraener.nxtgen.NextGen;
import org.junit.Test;

public class StartGenerator {

    @Test
    public void testRestModel() {
        NextGen.main(new String[]{"./DSL.oom"});
    }
}
