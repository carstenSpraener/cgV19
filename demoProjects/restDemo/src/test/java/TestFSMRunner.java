import de.csp.demo.rest.logic.UserValidate;
import de.csp.demo.rest.model.User;
import de.csp.fsm.FSMRunner;
import de.csp.fsm.FSMRunnerException;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestFSMRunner {

    @Test
    public void testUserValidateRunning() throws Exception {
        FSMRunner<User> runner = new FSMRunner(UserValidate.class, new User());

        RuntimeException xc = assertThrows(RuntimeException.class, ()->runner.run());
        assertNotNull(xc);
        assertEquals(FSMRunnerException.class, xc.getCause().getClass());
    }
}
