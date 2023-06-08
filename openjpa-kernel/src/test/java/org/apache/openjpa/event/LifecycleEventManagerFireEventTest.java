package org.apache.openjpa.event;

import org.apache.openjpa.kernel.BrokerImpl;
import org.apache.openjpa.meta.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class LifecycleEventManagerFireEventTest {
    private static LifecycleEventManager lifecycleEventManager;
    private Object source;
    private Object related;
    private ClassMetaData meta;
    private final int type;
    private final Class<? extends Exception> expectedException;
    private enum ObjType {
        NULL, VALID, INVALID
    }

    public LifecycleEventManagerFireEventTest(ObjType sourceType, ObjType relatedType, ObjType metaType, int type, Class<? extends Exception> expectedException){
        this.type = type;
        this.expectedException = expectedException;
        switch(sourceType) {
            case NULL:
                this.source = null;
                break;
            case VALID:
                this.source = null;
                break;
            case INVALID:
                this.source = null;
                break;
        }
        switch(relatedType) {
            case NULL:
                this.related = null;
                break;
            case VALID:
                this.related = null;
                break;
            case INVALID:
                this.related = null;
                break;
        }
        switch(metaType) {
            case NULL:
                this.meta = null;
                break;
            case VALID:
                this.meta = null;
                break;
            case INVALID:
                this.meta = null;
                break;
        }
    }

    @Before
    public void configure() {
        lifecycleEventManager = new LifecycleEventManager();
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
//                { ObjType.NULL,      ObjType.NULL,      ObjType.NULL,      -1,   null },
//                { ObjType.INVALID,   ObjType.INVALID,   ObjType.INVALID,    0,   null },
                { ObjType.VALID,     ObjType.VALID,     ObjType.VALID,      1,   null }
        });
    }

    @Test
    public void fireEventTest() {
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
//        LifecycleCallbacks[] LifecycleCallbacksListMock = new LifecycleCallbacks[]{mock(LifecycleCallbacks.class), mock(LifecycleCallbacks.class)};

        when(classMetaDataMock.getRepository()).thenReturn(metaDataRepositoryMock);
        when(metaDataRepositoryMock.getMetaDataFactory()).thenReturn(metaDataFactoryMock);
        when(metaDataFactoryMock.getDefaults()).thenReturn(metaDataDefaultsMock);

        when(classMetaDataMock.getLifecycleMetaData()).thenReturn(lifecycleMetaDataMock);
        when(lifecycleMetaDataMock.getIgnoreSystemListeners()).thenReturn(false);

        when(lifecycleMetaDataMock.getCallbacks(this.type)).thenReturn(LifecycleCallbacksListMock);


        try {
            // Utilizzo della riflessione per accedere alla variabile _flags
            Field exceps = LifecycleEventManager.class.getDeclaredField("_exceps");
            exceps.setAccessible(true);
            List<Exception> e = new LinkedList<>();
            e.add(new Exception("First dummy exception!"));
            e.add(new Exception("Second dummy exception!"));
            e.add(new Exception("Third dummy exception!"));
            exceps.set(lifecycleEventManager, e);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("The reflection for a mock raised an exception!");
        }


        if (expectedException == null) {
            Assertions.assertDoesNotThrow(() -> {
                // Codice di test che non dovrebbe sollevare un'eccezione
//                lifecycleEventManager.fireEvent(this.source, this.related, this.meta, this.type);
                Exception[] returnList = lifecycleEventManager.fireEvent(this.source, this.related, classMetaDataMock, this.type);
                System.out.println(returnList.length);
            });
        } else {
            Assertions.assertThrows(expectedException, () -> {
                // Codice di test che dovrebbe sollevare un'eccezione

                Assert.fail();
            });
        }
    }
}
