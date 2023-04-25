package de.csp.fsm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class FSMRunner<T> implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(FSMRunner.class.getName());

    private Class<?> fsmClass = null;
    private String result;
    private FSMContext<T> context;

    public FSMRunner( Class fsmClass, T rootObject ) {
        this.fsmClass = fsmClass;
        this.context = new FSMContextImpl(rootObject);
    }

    public String[] getFinalStates() {
        FSMRunnable fsmRunnable = fsmClass.getAnnotation(FSMRunnable.class);
        return fsmRunnable.finalStates();
    }

    public void run() {
        try {
            Object instance = fsmClass.getDeclaredConstructor().newInstance();
            FSMRunnable fsmRunnable = fsmClass.getAnnotation(FSMRunnable.class);
            Method initMethod = fsmClass.getMethod(fsmRunnable.initialMethod(), FSMContext.class);
            if( initMethod == null ) {
                return;
            }
            LOGGER.info(String.format("Invoking @FSMBefore-methods on %s.", fsmClass.getSimpleName()));
            invokeBefores(instance, context);
            Method nextMethod = initMethod;
            while( nextMethod != null ) {
                nextMethod = findNextMethod(instance, nextMethod, context);
            }
            LOGGER.info(String.format("FSM terminating with state '%s'", result));
        } catch( Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invokeBefores(Object instance, FSMContext context) throws InvocationTargetException, IllegalAccessException {
        for( Method m : fsmClass.getMethods() ) {
            FSMBefore before = m.getAnnotation(FSMBefore.class);
            if( before != null ) {
                m.invoke(instance, context);
            }
        }
    }

    private Method findNextMethod(Object instance, Method nextMethod, FSMContext context) {
        try {
            String result = (String)nextMethod.invoke(instance, context);
            LOGGER.info(String.format("Invocation of '%s' on instance of %s := %s", nextMethod.getName(), fsmClass.getSimpleName(), result));
            if( isFinalState(result)) {
                setResult(result);
                return null;
            }
            FSMTransitions transitions = nextMethod.getAnnotation(FSMTransitions.class);
            if( isEmpty(result) && transitions.transistions().length==1 ) {
                String target = transitions.transistions()[0].target();
                if( isFinalState(target) ) {
                    setResult(target);
                    return null;
                }
                return instance.getClass().getMethod(transitions.transistions()[0].target(), FSMContext.class);
            }
            for (FSMTransition transition : transitions.transistions()) {
                if (result != null && result.equals(transition.guard())) {
                    if( isFinalState(transition.target()) ) {
                        setResult(transition.target());
                        return null;
                    }
                    return instance.getClass().getMethod(transition.target(), FSMContext.class);
                }
            }
            throw new FSMRunnerException("Illegal result '%s' from state '%s'. No transition specified.", result, nextMethod.getName());
        } catch( InvocationTargetException itxc ) {
            throw new RuntimeException(itxc.getCause());
        } catch( ReflectiveOperationException roXC ) {
            throw new RuntimeException(roXC);
        }
    }

    private boolean isEmpty(Object result) {
        return result==null || "".equals(result.toString());
    }

    private boolean isFinalState(String nextState) {
        for( String finalState : getFinalStates() ) {
            if( finalState.equals(nextState) ) {
                return true;
            }
        }
        return false;
    }

    public FSMContext getContext() {
        return context;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
