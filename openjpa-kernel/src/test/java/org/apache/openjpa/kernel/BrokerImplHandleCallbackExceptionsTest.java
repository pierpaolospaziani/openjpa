package org.apache.openjpa.kernel;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class BrokerImplHandleCallbackExceptionsTest {
    private static BrokerImpl broker;
    private Exception[] exceps = null;
    private final int mode;
    private final Class<? extends Exception> expectedException;
    private enum ObjType {
        NULL, EMPTY, VALID
    }

    public BrokerImplHandleCallbackExceptionsTest(ObjType excepsType, int mode, Class<? extends Exception> expectedException){
        this.mode = mode;
        this.expectedException = expectedException;
        switch(excepsType) {
            case NULL:
                this.exceps = null;
                break;
            case EMPTY:
                this.exceps = new Exception[0];
                break;
            case VALID:
                Exception[] exceptions = new Exception[1];
                exceptions[0] = new Exception("Dummy exception!");
                this.exceps = exceptions;
                break;
        }
    }

    @Before
    public  void configure() {
        broker = new BrokerImpl();
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { ObjType.NULL,    -1,   InvocationTargetException.class },  // 0
                { ObjType.NULL,     0,   InvocationTargetException.class },  // 1
                { ObjType.NULL,     1,   InvocationTargetException.class },  // 2
                { ObjType.EMPTY,   -1,                 null              },  // 3
                { ObjType.EMPTY,    0,                 null              },  // 4
                { ObjType.EMPTY,    1,                 null              },  // 5
                { ObjType.VALID,   -1,                 null              },  // 6
                { ObjType.VALID,    0,                 null              },  // 7
                { ObjType.VALID,    1,                 null              }   // 8
        });
    }

    @Test
    public void handleCallbackExceptionsTest() {
        try {
            // Utilizzo della reflection per ottenere il metodo privato
            Method handleCallbackExceptions = BrokerImpl.class.getDeclaredMethod("handleCallbackExceptions", Exception[].class, int.class);
            handleCallbackExceptions.setAccessible(true);
            if (expectedException == null) {
                Assertions.assertDoesNotThrow(() -> {
                    // Codice di test che non dovrebbe sollevare un'eccezione

//                    int mode = BrokerImpl.CALLBACK_ROLLBACK | BrokerImpl.CALLBACK_LOG;

                    // Chiamata al metodo privato utilizzando la reflection
                    handleCallbackExceptions.invoke(broker, this.exceps, this.mode);
                });
            } else {
                Assertions.assertThrows(expectedException, () -> {
                    // Codice di test che dovrebbe sollevare un'eccezione
                    handleCallbackExceptions.invoke(broker, this.exceps, this.mode);
                    Assert.fail();
                });
            }
        } catch (NoSuchMethodException e) {
            Assert.fail("The reflection raised an exception!");
        }
    }

    @After
    public void cleanUp() {

    }
}
