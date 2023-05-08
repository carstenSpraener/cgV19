package de.csp.fsm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class FSMRunner<T> implements Runnable {
    private final static Logger LOGGER = Logger.getLogger(FSMRunner.class.getName());

    private Class<?> fsmClass = null;
    private String result;
    private FSMContextImpl<T> context;
    private FSMStateStorage stateStorage = null;
    private Object returnedValue = null;

    public FSMRunner( Class fsmClass, T rootObject ) {
        this.fsmClass = fsmClass;
        this.context = new FSMContextImpl(fsmClass, rootObject);
    }

    public FSMRunner( FSMContext<T> context, Object returnedValue ) {
        this.context = (FSMContextImpl<T>)context;
        this.fsmClass = this.context.getFsmClass();
        this.returnedValue = returnedValue;
    }

    public FSMRunner withStateStorage(FSMStateStorage stateStorage ) {
        this.stateStorage = stateStorage;
        return this;
    }

    public String[] getFinalStates() {
        FSMRunnable fsmRunnable = fsmClass.getAnnotation(FSMRunnable.class);
        return fsmRunnable.finalStates();
    }

    public void run() {
        try {
            Object instance = fsmClass.getDeclaredConstructor().newInstance();

            Method initMethod = null;
            if( this.context.getReturnFromMethod() == null ) {
                LOGGER.info(String.format("Invoking @FSMBefore-methods on %s.", fsmClass.getSimpleName()));
                invokeBefores(instance, context);

                FSMRunnable fsmRunnable = fsmClass.getAnnotation(FSMRunnable.class);
                initMethod = fsmClass.getMethod(fsmRunnable.initialMethod(), FSMContext.class);
            } else {
                initMethod = this.context.getReturnFromMethod();
            }
            if( initMethod == null ) {
                return;
            }

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

    private Method findNextMethod(Object instance, final Method actualMethod, FSMContext context) {
        try {
            if( actualMethod.isAnnotationPresent(FSMPrepareFor.class) ) {
                // This is an interruptable activity.
                actualMethod.invoke(instance, context);
                FSMContextImpl<?> contextImpl = (FSMContextImpl)context;
                contextImpl.setReturnFromMethod( findReturnMethod(actualMethod) );
                contextImpl.setInterrupted(true);
                storeState(contextImpl);
                return null;
            } else {
                String result = null;
                if( actualMethod.isAnnotationPresent(FSMReturnFrom.class) ) {
                    result = (String) actualMethod.invoke(instance, this.returnedValue, context);
                } else {
                    result = (String) actualMethod.invoke(instance, context);
                }
                LOGGER.info(String.format("Invocation of '%s' on instance of %s := %s", actualMethod.getName(), fsmClass.getSimpleName(), result));
                if (isFinalState(result)) {
                    setResult(result);
                    return null;
                }
                FSMTransitions transitions = actualMethod.getAnnotation(FSMTransitions.class);
                if (isEmpty(result) && transitions.transistions().length == 1) {
                    String target = transitions.transistions()[0].target();
                    if (isFinalState(target)) {
                        setResult(target);
                        return null;
                    }
                    return instance.getClass().getMethod(transitions.transistions()[0].target(), FSMContext.class);
                }
                for (FSMTransition transition : transitions.transistions()) {
                    if (result != null && result.equals(transition.guard())) {
                        if (isFinalState(transition.target())) {
                            setResult(transition.target());
                            return null;
                        }
                        return instance.getClass().getMethod(transition.target(), FSMContext.class);
                    }
                }
                throw new FSMRunnerException("Illegal result '%s' from state '%s'. No transition specified.", result, actualMethod.getName());
            }
        } catch( InvocationTargetException itxc ) {
            throw new RuntimeException(itxc.getCause());
        } catch( ReflectiveOperationException roXC ) {
            throw new RuntimeException(roXC);
        }
    }

    private void storeState(FSMContextImpl<?> contextImpl) {
        if( this.stateStorage == null ) {
            throw new IllegalStateException(String.format("A interruptable activity must have a state storage."));
        }
        this.stateStorage.storeSate(this.context);
    }

    private Method findReturnMethod(Method actualMethod) {
        FSMPrepareFor prepareFor = actualMethod.getAnnotation(FSMPrepareFor.class);
        String activityName = prepareFor.value();
        for( Method m : fsmClass.getMethods() ) {
            if( m.isAnnotationPresent(FSMReturnFrom.class) ) {
                FSMReturnFrom fsmReturnFrom = m.getAnnotation(FSMReturnFrom.class);
                if( fsmReturnFrom.value().equals(activityName) ) {
                    if( !(Object.class.equals(m.getReturnType())) ) {
                        throw new IllegalStateException(String.format("ReturnFrom Method '%s' must return Object type.", m.getName()));
                    }
                    if (m.getParameterTypes().length!=2 ||
                            !(Object.class.equals(m.getParameterTypes()[0]))||
                            !(FSMContext.class.equals(m.getParameterTypes()[1]))
                    ) {
                        throw new IllegalStateException(String.format("ReturnFrom Method '%s' has illegal parameter signature.", m.getName()));
                    }
                }
                return m;
            }
        }
        throw new IllegalStateException(String.format("No Method present with @FSMReturnFrom(\"%s\")", activityName));
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
