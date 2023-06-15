import de.csp.demo.rest.logic.UserValidate;
import de.csp.demo.rest.model.User;
import de.csp.fsm.FSMContext;
import org.junit.Test;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

class FSMContextImpl implements FSMContext<User> {

    private Map<String, Object> valueMap = new HashMap<>();
    private User user = new User();

    @Override
    public FSMContext put(String s, Object o) {
        valueMap.put(s,o);
        return this;
    }

    @Override
    public Object get(String s) {
        return valueMap.get(s);
    }

    @Override
    public User getRootObject() {
        return user;
    }
}
public class SpringStateMachine {

    UserValidate userValidate2 = new UserValidate();
    FSMContext<User> userFSMContext = new FSMContextImpl();

    private Mono<Void> init(StateContext<String, String> context) {
        userFSMContext.put("STATE", "NULL");
        return Mono.empty();
    }

    private Mono<Void> doThis(StateContext<String, String> context) {
        System.out.println("doThis()");
        Object result = userValidate2.doThis(userFSMContext);
        context.getStateMachine().sendEvent(result.toString());
        return Mono.empty();
    }

    private Mono<Void> reCalculate(StateContext<String, String> context) {
        System.out.println("start reCalculate()");
        try {
            Thread.sleep(3000);
        } catch (Exception e){}
        userValidate2.reCalculate(userFSMContext);
        System.out.println("finish reCalculate()");
        return Mono.empty();
    }

    private Mono<Void> doThat(StateContext<String, String> context) {
        System.out.println("doThat()");
        Object result = userValidate2.doThat(userFSMContext);
        context.getStateMachine().sendEvent(result.toString());
        return Mono.empty();
    }

    @Test
    public void testSprintStateMachineWithBuilder() throws Exception {
        StateMachineBuilder.Builder<String, String> builder
                = StateMachineBuilder.builder();
        builder.configureStates().withStates()
                .initial("INIT", this::init)
                .state("doThis", this::doThis)
                .state("reCalculate", this::reCalculate)
                .state("doThat", this::doThat)
                .end("Boolean.TRUE")
                .end("Boolean.FALSE")
                ;

        builder.configureTransitions()
                .withExternal()
                .source("INIT").target("doThis")
                .and().withExternal().source("doThis").target("reCalculate").event("NULL")
                .and().withExternal().source("doThis").target("doThat").event("TRUE")
                .and().withExternal().source("doThis").target("Boolean.FALSE").event("FALSE")

                .and().withExternal().source("reCalculate").target("doThis")

                .and().withExternal().source("doThat").target("Boolean.TRUE")
        ;

        StateMachineFactory<String, String> factory = builder.createFactory();
        final StateMachine<String,String>  machine = factory.getStateMachine();
        machine.addStateListener(new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<String, String> from, State<String, String> to) {
                System.out.printf("Machine trasitions from state '%s' to state '%s'\n", from!=null ? from.getId() : "null", to.getId());
            }
        });
        machine.startReactively().subscribe(
                data -> {},
                err -> {},
                () -> {
                    State endState = machine.getState();
                    System.out.printf("EndState is %s\n", endState.getId());
                }
        );
        while( !machine.isComplete() ) {
            System.out.println("Waiting for Termination...");
            try { Thread.sleep(500); }catch( Exception e ) {}
        }
    }
}
