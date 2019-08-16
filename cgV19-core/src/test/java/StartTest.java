import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.ModelLoader;
import org.junit.Test;

import java.io.InputStream;

public class StartTest {

    @Test
    public void testCodeGen() throws Exception {
        InputStream is = StartTest.class.getResourceAsStream("/META-INF/services/"+ ModelLoader.class.getName());
        assert is!=null;
        NextGen.main(new String[]{
                "/model.groovy"
        });
    }
}
