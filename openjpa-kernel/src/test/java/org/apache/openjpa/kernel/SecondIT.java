package org.apache.openjpa.kernel;

import org.apache.openjpa.event.LifecycleCallbacks;
import org.apache.openjpa.event.LifecycleEventManager;
import org.apache.openjpa.meta.*;
import org.apache.openjpa.util.CallbackException;
import org.apache.openjpa.util.OpenJPAException;
import org.apache.openjpa.util.WrappedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static org.apache.openjpa.event.CallbackModes.CALLBACK_RETHROW;
import static org.apache.openjpa.event.CallbackModes.CALLBACK_ROLLBACK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class SecondIT {
    private static BrokerImpl broker;
    private static LifecycleEventManager lifecycleEventManager;
    private static ReentrantLock lock = new ReentrantLock();
    private final Class<? extends Exception> expectedException;

    public SecondIT(Class<? extends Exception> expectedException){
        this.expectedException = expectedException;
    }

    @Before
    public void configure() {
        broker = new BrokerImpl();
        lifecycleEventManager = new LifecycleEventManager();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { null }
        });
    }

    @Test
    public void integrationTest() {
        try {
            // Utilizzo della reflection per ottenere il metodo privato
            Method handleCallbackExceptions = BrokerImpl.class.getDeclaredMethod("handleCallbackExceptions", Exception[].class, int.class);
            handleCallbackExceptions.setAccessible(true);

            ClassMetaData classMetaDataMock = mock(ClassMetaData.class);
            MetaDataRepository metaDataRepositoryMock = mock(MetaDataRepository.class);
            MetaDataFactory metaDataFactoryMock = mock(MetaDataFactory.class);
            MetaDataDefaults metaDataDefaultsMock = mock(MetaDataDefaults.class);
            LifecycleMetaData lifecycleMetaDataMock = mock(LifecycleMetaData.class);
            LifecycleCallbacks lifecycleCallbacksMock = new LifecycleCallbacks() {
                @Override
                public boolean hasCallback(Object obj, int eventType) {
                    return false;
                }

                @Override
                public void makeCallback(Object obj, Object related, int eventType) throws Exception {
                    throw new Exception("That's an additional Exception!");
                }
            };
            LifecycleCallbacks[] LifecycleCallbacksListMock = new LifecycleCallbacks[]{lifecycleCallbacksMock};
//            LifecycleCallbacks[] LifecycleCallbacksListMock = new LifecycleCallbacks[]{mock(LifecycleCallbacks.class), mock(LifecycleCallbacks.class)};
            when(classMetaDataMock.getRepository()).thenReturn(metaDataRepositoryMock);
            when(metaDataRepositoryMock.getMetaDataFactory()).thenReturn(metaDataFactoryMock);
            when(metaDataFactoryMock.getDefaults()).thenReturn(metaDataDefaultsMock);
            when(classMetaDataMock.getLifecycleMetaData()).thenReturn(lifecycleMetaDataMock);
            when(lifecycleMetaDataMock.getIgnoreSystemListeners()).thenReturn(false);
            when(lifecycleMetaDataMock.getCallbacks(0)).thenReturn(LifecycleCallbacksListMock);
            // Utilizzo della riflessione per accedere alla variabile _exceps
            Field exceps = LifecycleEventManager.class.getDeclaredField("_exceps");
            exceps.setAccessible(true);
            List<Exception> e = new LinkedList<>();
            e.add(new WrappedException("Dummy wrapped exception!"));
            e.add(new Exception("First dummy exception!"));
            e.add(new Exception("Second dummy exception!"));
            exceps.set(lifecycleEventManager, e);


            // Codice di test che non dovrebbe sollevare un'eccezione
            lock.lock();
            Exception[] exs;
            try {
                exs = lifecycleEventManager.fireEvent(null, null, classMetaDataMock, 0);
            } finally {
                lock.unlock();
            }
            try {
                handleCallbackExceptions.invoke(broker, exs, CALLBACK_RETHROW);
            } catch (InvocationTargetException ex){
                Throwable[] trw = ((CallbackException) ex.getCause()).getNestedThrowables();
                    for (Throwable tr : trw){
//                        tr.printStackTrace();
                        System.out.println(tr.getMessage());
                    }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("The reflection raised an exception!");
        }
    }
}