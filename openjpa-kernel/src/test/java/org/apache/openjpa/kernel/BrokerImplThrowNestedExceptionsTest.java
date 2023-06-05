package org.apache.openjpa.kernel;

import org.apache.openjpa.util.OpenJPAException;
import org.apache.openjpa.util.StoreException;
import org.apache.openjpa.util.UserException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class BrokerImplThrowNestedExceptionsTest {
    private static BrokerImpl broker;
    private List<Exception> exceps;
    private final boolean datastore;
    private final Class<? extends Exception> expectedException;
    private enum ObjType {
        NULL, EMPTY, INVALID, VALID_ONE, VALID
    }

    public BrokerImplThrowNestedExceptionsTest(ObjType excepsType, boolean datastore, Class<? extends Exception> expectedException){
        this.datastore = datastore;
        this.expectedException = expectedException;
        this.exceps = new ArrayList<>();
        switch(excepsType) {
            case NULL:
                this.exceps = null;
                break;
            case EMPTY:
                break;
            case INVALID:
                this.exceps.add(null);
                break;
            case VALID_ONE:
                this.exceps.add(new Exception("Dummy exception!"));
                break;
            case VALID:
                this.exceps.add(new Exception("First dummy exception!"));
                this.exceps.add(new Exception("Second dummy exception!"));
                OpenJPAException openJPAException = new OpenJPAException() {
                    @Override
                    public int getType() {
                        return 0;
                    }
                };
                this.exceps.add(openJPAException);
                OpenJPAException fatalOpenJPAException = new OpenJPAException() {
                    @Override
                    public int getType() {
                        return 0;
                    }
                };
                fatalOpenJPAException.setFatal(true);
                this.exceps.add(fatalOpenJPAException);
                break;
        }
    }

    @Before
    public void configure() {
        broker = new BrokerImpl();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { ObjType.NULL,        false,              null            },  // 0
                { ObjType.NULL,        true,               null            },  // 1
                { ObjType.EMPTY,       false,              null            },  // 2
                { ObjType.EMPTY,       true,               null            },  // 3
                { ObjType.INVALID,     false,       UserException.class    },  // 4
                { ObjType.INVALID,     true,    NullPointerException.class },  // 5
                { ObjType.VALID_ONE,   false,       UserException.class    },  // 6
                { ObjType.VALID_ONE,   true,      RuntimeException.class   },  // 7
                { ObjType.VALID,       false,       UserException.class    },  // 8
                { ObjType.VALID,       true,        StoreException.class   }   // 9
        });
    }

    @Test
    public void handleCallbackExceptionsTest() {
        try {
            // Utilizzo della reflection per ottenere il metodo privato
            Method throwNestedExceptions = BrokerImpl.class.getDeclaredMethod("throwNestedExceptions", List.class, boolean.class);
            throwNestedExceptions.setAccessible(true);
            if (expectedException == null) {
                Assertions.assertDoesNotThrow(() -> {
                    // Codice di test che non dovrebbe sollevare un'eccezione
                    throwNestedExceptions.invoke(broker, this.exceps, this.datastore);
                });
            } else {
                Assertions.assertThrows(expectedException, () -> {
                    // Codice di test che dovrebbe sollevare un'eccezione
                    try {
                        throwNestedExceptions.invoke(broker, this.exceps, this.datastore);
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
