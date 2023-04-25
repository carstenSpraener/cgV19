import de.csp.demo.rest.logic.UserValidate;
import de.csp.demo.rest.model.User;
import de.csp.fsm.FSMRunner;
import org.junit.Test;

public class TestFSMRunner {

    @Test
    public void testUserValidateRunning() throws Exception {
        FSMRunner<User> runner = new FSMRunner(UserValidate.class, new User());

        runner.run();
        System.out.println("FinalState: "+runner.getResult());
    }
}
