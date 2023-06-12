package org.apache.openjpa.kernel;

import org.apache.openjpa.lib.log.Log;
import org.apache.openjpa.util.CallbackException;
import org.apache.openjpa.util.WrappedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import static org.apache.openjpa.event.CallbackModes.*;

@RunWith(Parameterized.class)
public class BrokerImplHandleCallbackExceptionsTest {
    private static BrokerImpl broker;
    private Exception[] exceps = null;
    private final int mode;
    private final Class<? extends Exception> expectedException;
    private enum ObjType {
        NULL, EMPTY, INVALID, VALID_ONE, VALID, WRAPPED
    }

    public BrokerImplHandleCallbackExceptionsTest(ObjType excepsType, int mode, Class<? extends Exception> expectedException){
        this.mode = mode;
        this.expectedException = expectedException;
        Exception[] exceptions;
        switch(excepsType) {
            case NULL:
                this.exceps = null;
                break;
            case EMPTY:
                this.exceps = new Exception[0];
                break;
            case INVALID:
                exceptions = new Exception[1];
                exceptions[0] = null;
                this.exceps = exceptions;
                break;
            case VALID_ONE:
                exceptions = new Exception[1];
                exceptions[0] = null;
                exceptions[0] = new Exception("Dummy exception!");
                this.exceps = exceptions;
                break;
            case VALID:
                exceptions = new Exception[2];
                exceptions[0] = new Exception("First dummy exception!");
                exceptions[1] = new Exception("Second dummy exception!");
                this.exceps = exceptions;
                break;
            case WRAPPED:
                exceptions = new Exception[1];
                exceptions[0] = new WrappedException("Dummy wrapped exception!");
                this.exceps = exceptions;
                break;
        }
    }

    @Before
    public void configure() {
        broker = new BrokerImpl();
        try {
            if (this.mode == CALLBACK_ROLLBACK && expectedException != null){
                // Utilizzo della riflessione per accedere alla variabile _flags
                Field flagsField = BrokerImpl.class.getDeclaredField("_flags");
                flagsField.setAccessible(true);
                flagsField.setInt(broker, 2);
            } else if (this.mode == CALLBACK_LOG && expectedException == null){
                Log logMock = Mockito.mock(Log.class);
                Mockito.when(logMock.isWarnEnabled()).thenReturn(true);
                // Utilizzo della riflessione per accedere alla variabile _log
                Field logField = BrokerImpl.class.getDeclaredField("_log");
                logField.setAccessible(true);
                logField.set(broker, logMock);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("The reflection for a mock raised an exception!");
        }
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { ObjType.NULL,               -1,           NullPointerException.class },  // 0
                { ObjType.NULL,                0,           NullPointerException.class },  // 1
                { ObjType.NULL,                1,           NullPointerException.class },  // 2
                { ObjType.INVALID,            -1,                      null            },  // 3
                { ObjType.INVALID,             0,           NullPointerException.class },  // 4
                { ObjType.INVALID,             1,           NullPointerException.class },  // 5
                { ObjType.EMPTY,              -1,                      null            },  // 3
                { ObjType.EMPTY,               0,                      null            },  // 4
                { ObjType.EMPTY,               1,                      null            },  // 5
                { ObjType.VALID_ONE,          -1,                      null            },  // 6
                { ObjType.VALID_ONE,           0,                      null            },  // 7
                { ObjType.VALID_ONE,           1,                      null            },  // 8
                { ObjType.VALID,              -1,                      null            },  // 9
                { ObjType.VALID,               0,                      null            },  // 10
                { ObjType.VALID,               1,                      null            },  // 11
                { ObjType.WRAPPED,            -1,                      null            },  // 12
                { ObjType.WRAPPED,             0,             WrappedException.class   },  // 13
                { ObjType.WRAPPED,             1,             WrappedException.class   },  // 14
                { ObjType.VALID,       CALLBACK_IGNORE,                null            },  // 15
                { ObjType.VALID,       CALLBACK_ROLLBACK,              null            },  // 16
                { ObjType.VALID,       CALLBACK_ROLLBACK,         Exception.class      },  // 17
                { ObjType.VALID,       CALLBACK_LOG,        NullPointerException.class },  // 18
                { ObjType.VALID,       CALLBACK_LOG,                   null            },  // 19
                { ObjType.VALID,       CALLBACK_RETHROW,      CallbackException.class  }   // 20
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
                    handleCallbackExceptions.invoke(broker, this.exceps, this.mode);
                });
            } else {
                Assertions.assertThrows(expectedException, () -> {
                    // Codice di test che dovrebbe sollevare un'eccezione
                    try {
                        handleCallbackExceptions.invoke(broker, this.exceps, this.mode);
                    } catch (Exception e){
                        throw e.getCause();
                    }
                    Assert.fail();
                });
            }
        } catch (NoSuchMethodException e) {
            Assert.fail("The reflection raised an exception!");
        }
    }
}
