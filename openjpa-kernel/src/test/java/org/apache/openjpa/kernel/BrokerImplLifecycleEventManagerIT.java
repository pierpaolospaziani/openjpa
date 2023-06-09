package org.apache.openjpa.kernel;

import org.apache.openjpa.event.LifecycleCallbacks;
import org.apache.openjpa.event.LifecycleEventManager;
import org.apache.openjpa.lib.log.Log;
import org.apache.openjpa.meta.*;
import org.apache.openjpa.util.CallbackException;
import org.apache.openjpa.util.WrappedException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static org.apache.openjpa.event.CallbackModes.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class BrokerImplLifecycleEventManagerIT {
    private static BrokerImpl broker;
    private static LifecycleEventManager lifecycleEventManager;
    private Method handleCallbackExceptions;
    private ClassMetaData classMetaDataMock;
    private final int mode;
    private Exception[] exceps = new Exception[0];
    private enum ListType {
        EMPTY, SINGLE, MULTIPLE, WRAPPED
    }

    public BrokerImplLifecycleEventManagerIT(ListType listType, int mode){
        this.mode = mode;
        List<Exception> e = new LinkedList<>();
        switch(listType) {
            case EMPTY:
                this.exceps = e.toArray(new Exception[0]);
                break;
            case SINGLE:
                if (mode == CALLBACK_RETHROW)
                    e.add(new CallbackException(new Exception("That's an additional exception!")));
                this.exceps = e.toArray(new Exception[0]);
                break;
            case MULTIPLE:
                if (mode == CALLBACK_RETHROW){
                    e.add(new WrappedException("Dummy wrapped exception!"));
                    e.add(new Exception("First dummy exception!"));
                    e.add(new Exception("Second dummy exception!"));
                    e.add(new Exception("That's an additional exception!"));
                }
                this.exceps = e.toArray(new Exception[0]);
                break;
            case WRAPPED:
                if (mode != CALLBACK_IGNORE)
                    e.add(new WrappedException("Dummy wrapped exception!"));
                this.exceps = e.toArray(new Exception[0]);
                break;
        }
        mockSetup(listType);
    }

    public void mockSetup(ListType listType) {
        try {
            // Utilizzo della reflection per ottenere il metodo privato
            handleCallbackExceptions = BrokerImpl.class.getDeclaredMethod("handleCallbackExceptions", Exception[].class, int.class);
            handleCallbackExceptions.setAccessible(true);
            Log logMock = mock(Log.class);
            when(logMock.isWarnEnabled()).thenReturn(true);
            // Utilizzo della riflessione per accedere alla variabile _log
            Field logField = BrokerImpl.class.getDeclaredField("_log");
            logField.setAccessible(true);
            logField.set(broker, logMock);
            // Utilizzo della riflessione per accedere alla variabile _flags
            Field flagsField = BrokerImpl.class.getDeclaredField("_flags");
            flagsField.setAccessible(true);
            flagsField.setInt(broker, 0);

            classMetaDataMock = mock(ClassMetaData.class);
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
                    throw new Exception("That's an additional exception!");
                }
            };
            LifecycleCallbacks[] LifecycleCallbacksListMock;
            if (listType == ListType.EMPTY || listType == ListType.WRAPPED)
                LifecycleCallbacksListMock = new LifecycleCallbacks[]{mock(LifecycleCallbacks.class)};
            else
                LifecycleCallbacksListMock = new LifecycleCallbacks[]{lifecycleCallbacksMock};
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
            if (listType == ListType.MULTIPLE){
                e.add(new WrappedException("Dummy wrapped exception!"));
                e.add(new Exception("First dummy exception!"));
                e.add(new Exception("Second dummy exception!"));
            } else if (listType == ListType.WRAPPED)
                e.add(new WrappedException("Dummy wrapped exception!"));
            exceps.set(lifecycleEventManager, e);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("The reflection raised an exception!");
        }
    }

    @BeforeClass
    public static void configure() {
        broker = new BrokerImpl();
        lifecycleEventManager = new LifecycleEventManager();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { ListType.EMPTY,      CALLBACK_IGNORE   },  // 0
                { ListType.EMPTY,      CALLBACK_ROLLBACK },  // 1
                { ListType.EMPTY,      CALLBACK_LOG      },  // 2
                { ListType.EMPTY,      CALLBACK_RETHROW  },  // 3
                { ListType.SINGLE,     CALLBACK_IGNORE   },  // 4
                { ListType.SINGLE,     CALLBACK_ROLLBACK },  // 5
                { ListType.SINGLE,     CALLBACK_LOG      },  // 6
                { ListType.SINGLE,     CALLBACK_RETHROW  },  // 7
                { ListType.MULTIPLE,   CALLBACK_IGNORE   },  // 8
                { ListType.MULTIPLE,   CALLBACK_ROLLBACK },  // 9
                { ListType.MULTIPLE,   CALLBACK_LOG      },  // 10
                { ListType.MULTIPLE,   CALLBACK_RETHROW  },  // 11
                { ListType.WRAPPED,    CALLBACK_IGNORE   },  // 12
                { ListType.WRAPPED,    CALLBACK_ROLLBACK },  // 13
                { ListType.WRAPPED,    CALLBACK_LOG      },  // 14
                { ListType.WRAPPED,    CALLBACK_RETHROW  }   // 15
        });
    }

    @Test
    public void integrationTest() {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        Exception[] exs = null;
        try {
            exs = lifecycleEventManager.fireEvent(null, null, classMetaDataMock, 0);
        } catch (Exception e){
            Assert.fail("Unexpected exception for lifecycleEventManager!");
        } finally {
            lock.unlock();
        }

        if (this.exceps.length == 0) {
            Exception[] finalExs = exs;
            Assertions.assertDoesNotThrow(() -> {
                // Codice di test che non dovrebbe sollevare un'eccezione
                handleCallbackExceptions.invoke(broker, finalExs, this.mode);
            });
        } else {
            // Codice di test che dovrebbe sollevare un'eccezione
            boolean hasExcepted = false;
            try {
                handleCallbackExceptions.invoke(broker, exs, this.mode);
            } catch (Exception e){
                hasExcepted = true;
                Throwable[] trw;
                if (e.getCause().getClass()!= WrappedException.class)
                    trw = ((CallbackException) e.getCause()).getNestedThrowables();
                else
                    trw = e.getCause().getSuppressed();
                for (int i=0; i<trw.length; i++){
                    if (!this.exceps[i].getMessage().equals(trw[i].getMessage())){
                        Assert.fail("The exception list is not as the expected one!");
                    }
                }
            }
            if (!hasExcepted) Assert.fail("Expected exception not thrown");
        }
    }
}
