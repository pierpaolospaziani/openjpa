package org.apache.openjpa.util;

import org.apache.openjpa.enhance.FieldConsumer;
import org.apache.openjpa.enhance.FieldSupplier;
import org.apache.openjpa.enhance.PersistenceCapable;
import org.apache.openjpa.enhance.StateManager;
import org.apache.openjpa.kernel.OpenJPAStateManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.internal.verification.Times;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(Parameterized.class)
public class ProxyManagerImplNewCustomProxyTest {
    private static ProxyManagerImpl proxyManagerImpl;
    private Object orig;
    private final boolean autoOff;
    private final boolean expected;
    private final Map<Integer, String> expectedMap = new HashMap<>();
    private Date expectedDate = null;
    private Calendar expectedCalendar = null;
    private Timestamp expectedTimestamp = null;
    private enum ObjType {
        NULL, PROXY, PERSISTENCECAPABLE, STRING, INT, BOOL, COLLECTION, MAP, DATE, CALENDAR, OTHER, SORTEDSET, SORTEDMAP, TIMESTAMP
    }
    public ProxyManagerImplNewCustomProxyTest(ObjType origType, boolean autoOff, boolean expected){
        this.autoOff = autoOff;
        this.expected = expected;
        switch(origType) {
            case NULL:
                this.orig = null;
                break;
            case PROXY:
                this.orig = new DummyProxy();
                break;
            case PERSISTENCECAPABLE:
                this.orig = new DummyPersistenceCapable();
                break;
            case STRING:
                this.orig = "That's a string!";
                break;
            case INT:
                this.orig = 0;
                break;
            case BOOL:
                this.orig = false;
                break;
            case COLLECTION:
                this.orig = new ArrayList<String>();
                break;
            case MAP:
                Map<Integer, String> origMap = new HashMap<>();
                origMap.put(0, "Value");
                expectedMap.put(0, "Value");
                this.orig = origMap;
                break;
            case DATE:
                Date origDate = new Date();
                this.orig = origDate;
                expectedDate = origDate;
                break;
            case CALENDAR:
                Calendar origCalendar = Calendar.getInstance();
                origCalendar.set(2023, Calendar.JUNE, 22);
                this.orig = origCalendar;
                expectedCalendar = origCalendar;
                break;
            case OTHER:
                this.orig = new DummyObject();
                break;
            case SORTEDSET:
                this.orig = new TreeSet<String>();
                break;
            case SORTEDMAP:
                TreeMap<Integer, String> origTreeMap = new TreeMap<>();
                origTreeMap.put(0, "Value");
                expectedMap.put(0, "Value");
                this.orig = origTreeMap;
                break;
            case TIMESTAMP:
                Timestamp origTimestamp = new Timestamp(System.currentTimeMillis());
                this.orig = origTimestamp;
                expectedTimestamp = origTimestamp;
                break;
        }
    }

    @Before
    public void configure() {
        proxyManagerImpl = new ProxyManagerImpl();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { ObjType.NULL,                 false,   false },  // 0
                { ObjType.NULL,                 true,    false },  // 1
                { ObjType.PROXY,                false,   true  },  // 2
                { ObjType.PROXY,                true,    true  },  // 3
                { ObjType.PERSISTENCECAPABLE,   false,   false },  // 4
                { ObjType.PERSISTENCECAPABLE,   true,    false },  // 5
                { ObjType.STRING,               false,   false },  // 6
                { ObjType.STRING,               true,    false },  // 7
                { ObjType.INT,                  false,   false },  // 8
                { ObjType.INT,                  true,    false },  // 9
                { ObjType.BOOL,                 false,   false },  // 10
                { ObjType.BOOL,                 true,    false },  // 11
                { ObjType.COLLECTION,           false,   true  },  // 12
                { ObjType.COLLECTION,           true,    true  },  // 13
                { ObjType.MAP,                  false,   true  },  // 14
                { ObjType.MAP,                  true,    true  },  // 15
                { ObjType.DATE,                 false,   true  },  // 16
                { ObjType.DATE,                 true,    true  },  // 17
                { ObjType.CALENDAR,             false,   true  },  // 18
                { ObjType.CALENDAR,             true,    true  },  // 19
                { ObjType.OTHER,                false,   false },  // 20
                { ObjType.OTHER,                true,    false },  // 21
                { ObjType.SORTEDSET,            false,   true  },  // 22
                { ObjType.SORTEDSET,            true,    true  },  // 23
                { ObjType.SORTEDMAP,            false,   true  },  // 24
                { ObjType.SORTEDMAP,            true,    true  },  // 25
                { ObjType.TIMESTAMP,            false,   true  },  // 26
                { ObjType.TIMESTAMP,            true,    true  }   // 27
        });
    }

    @Test
    public void newCustomProxyTest() {
        Assertions.assertDoesNotThrow(() -> {
            // Codice di test che non dovrebbe sollevare un'eccezione
            Proxy result = proxyManagerImpl.newCustomProxy(this.orig, this.autoOff);
            if (expected){
                Assertions.assertNotNull(result);
            }

            /** aggiunti per migliorare la mutation coverage */
            if (result instanceof Map) {
                assertEquals(expectedMap, result);
                return;
            }
            if (result instanceof Timestamp) {
                assertEquals(expectedTimestamp, result);
                return;
            }
            if (result instanceof Date) {
                assertEquals(expectedDate, result);
                return;
            }
            if (result instanceof Calendar) {
                assertEquals(expectedCalendar, result);
            }
        });
    }

    static class DummyObject {}

    static class DummyPersistenceCapable implements PersistenceCapable{
        @Override
        public int pcGetEnhancementContractVersion() {
            return 0;
        }

        @Override
        public Object pcGetGenericContext() {
            return null;
        }

        @Override
        public StateManager pcGetStateManager() {
            return null;
        }

        @Override
        public void pcReplaceStateManager(StateManager sm) {

        }

        @Override
        public void pcProvideField(int fieldIndex) {

        }

        @Override
        public void pcProvideFields(int[] fieldIndices) {

        }

        @Override
        public void pcReplaceField(int fieldIndex) {

        }

        @Override
        public void pcReplaceFields(int[] fieldIndex) {

        }

        @Override
        public void pcCopyFields(Object fromObject, int[] fields) {

        }

        @Override
        public void pcDirty(String fieldName) {

        }

        @Override
        public Object pcFetchObjectId() {
            return null;
        }

        @Override
        public Object pcGetVersion() {
            return null;
        }

        @Override
        public boolean pcIsDirty() {
            return false;
        }

        @Override
        public boolean pcIsTransactional() {
            return false;
        }

        @Override
        public boolean pcIsPersistent() {
            return false;
        }

        @Override
        public boolean pcIsNew() {
            return false;
        }

        @Override
        public boolean pcIsDeleted() {
            return false;
        }

        @Override
        public Boolean pcIsDetached() {
            return null;
        }

        @Override
        public PersistenceCapable pcNewInstance(StateManager sm, boolean clear) {
            return null;
        }

        @Override
        public PersistenceCapable pcNewInstance(StateManager sm, Object obj, boolean clear) {
            return null;
        }

        @Override
        public Object pcNewObjectIdInstance() {
            return null;
        }

        @Override
        public Object pcNewObjectIdInstance(Object obj) {
            return null;
        }

        @Override
        public void pcCopyKeyFieldsToObjectId(Object obj) {

        }

        @Override
        public void pcCopyKeyFieldsToObjectId(FieldSupplier supplier, Object obj) {

        }

        @Override
        public void pcCopyKeyFieldsFromObjectId(FieldConsumer consumer, Object obj) {

        }

        @Override
        public Object pcGetDetachedState() {
            return null;
        }

        @Override
        public void pcSetDetachedState(Object state) {

        }
    }

    static class DummyProxy implements Proxy {
        @Override
        public void setOwner(OpenJPAStateManager sm, int field) {
        }

        @Override
        public OpenJPAStateManager getOwner() {
            return null;
        }

        @Override
        public int getOwnerField() {
            return 0;
        }

        @Override
        public ChangeTracker getChangeTracker() {
            return null;
        }

        @Override
        public Object copy(Object orig) {
            return null;
        }
    }
}
