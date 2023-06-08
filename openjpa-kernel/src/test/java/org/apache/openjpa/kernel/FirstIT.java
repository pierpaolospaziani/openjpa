package org.apache.openjpa.kernel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class FirstIT {
    private static BrokerImpl broker;
    private static BrokerImpl brokerMock;
    private static DelegatingBroker delegatingBroker;
    private static StateManagerImpl sm;
    private static FindCallbacks findCallbacks = new DummyFindCallbacks();
    private final Class<? extends Exception> expectedException;

    public FirstIT(Class<? extends Exception> expectedException){
        this.expectedException = expectedException;
    }

    @Before
    public void configure() {
        broker = new BrokerImpl();
        brokerMock = mock(BrokerImpl.class);
        when(brokerMock.findCached(any(), any())).thenReturn("Hello World!");
        delegatingBroker = new DelegatingBroker(broker, null);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { null }
        });
    }

    @Test
    public void integrationTest() {
        if (expectedException == null) {
            Assertions.assertDoesNotThrow(() -> {
                // Codice di test che non dovrebbe sollevare un'eccezione
                Object returnObj = delegatingBroker.findCached(null, findCallbacks);
                Assert.assertEquals("Hello World!", returnObj.toString());
            });
        } else {
            Assertions.assertThrows(expectedException, () -> {
                // Codice di test che dovrebbe sollevare un'eccezione

                Assert.fail();
            });
        }
    }


    public static class DummyFindCallbacks implements FindCallbacks {
        @Override
        public Object processArgument(Object oid) {
//            return null;
//            return oid;
            return "Hello World!";
        }

        @Override
        public Object processReturn(Object oid, OpenJPAStateManager sm) {
//            return null;
            return (sm == null) ? null : sm.getManagedInstance();
        }
    }
}
