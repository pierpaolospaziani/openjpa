package org.apache.openjpa.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ProxyManagerImplCopyArrayTest {
    private static ProxyManagerImpl proxyManagerImpl;
    private Object orig;
    private final Class<? extends Exception> expectedException;
    private int[] expectedArray;
    private enum ObjType {
        NULL, INVALID, EMPTY, VALID
    }
    public ProxyManagerImplCopyArrayTest(ObjType origType, Class<? extends Exception> expectedException){
        this.expectedException = expectedException;
        switch(origType) {
            case NULL:
                this.orig = null;
                break;
            case INVALID:
                this.orig = String.class;
                break;
            case EMPTY:
                this.orig = new int[0];
                break;
            case VALID:
                int[] array = new int[]{1, 2, 3};
                this.orig = array;
                expectedArray = array;
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
                { ObjType.NULL,                   null              },  // 0
                { ObjType.INVALID,   IllegalArgumentException.class },  // 1
                { ObjType.EMPTY,                  null              },  // 2
                { ObjType.VALID,                  null              }   // 3
        });
    }

    @Test
    public void copyArrayTest() {
        if (expectedException == null) {
            Assertions.assertDoesNotThrow(() -> {
                // Codice di test che non dovrebbe sollevare un'eccezione
                Object result = proxyManagerImpl.copyArray(this.orig);
                if (this.orig instanceof int[]){
                    assert ((int[]) this.orig).length != 0 || ((int[]) result).length == 0;
                    for (int i=0; i<((int[]) result).length; i++)
                        Assertions.assertEquals(expectedArray[i], ((int[]) result)[i]);
                } else Assert.assertNull(result);
            });
        } else {
            Assertions.assertThrows(expectedException, () -> {
                // Codice di test che dovrebbe sollevare un'eccezione
                try {
                    proxyManagerImpl.copyArray(this.orig);
                } catch (Exception e){
                    throw e.getCause();
                }
                Assert.fail();
            });
        }
    }
}
