import de.csp.demo.rest.logic.UserValidate2;
import de.csp.demo.rest.model.User;
import de.csp.fsm.FSMContextImpl;
import de.csp.fsm.FSMRunner;
import de.csp.fsm.FSMStateStorage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestFSMRunner {
    private FSMContextImpl testContext;

    FSMStateStorage storage = new FSMStateStorage() {
        @Override
        public Object storeSate(FSMContextImpl context) {
            testContext = context;
            return "1";
        }

        @Override
        public FSMContextImpl loadState(Object key) {
            return testContext;
        }
    };

    @Test
    public void testUserValidateRunning() throws Exception {
        FSMRunner<User> runner = new FSMRunner(UserValidate2.class, new User())
                .withStateStorage(storage);
        runner.run();

        FSMRunner<User> runner2 = new FSMRunner<>(testContext, "FALSE").withStateStorage(storage);
        runner2.run();
        assertEquals("Boolean.FALSE", runner2.getResult());
    }
}
