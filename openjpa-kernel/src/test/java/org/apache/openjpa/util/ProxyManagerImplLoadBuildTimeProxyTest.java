package org.apache.openjpa.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ProxyManagerImplLoadBuildTimeProxyTest {
    private static ProxyManagerImpl proxyManagerImpl;
    private Class type;
    private ClassLoader loader;
    private final Class<? extends Exception> expectedException;
    private enum ObjType {
        NULL, INVALID, VALID
    }
    public ProxyManagerImplLoadBuildTimeProxyTest(ObjType typeType, ObjType loaderType, Class<? extends Exception> expectedException){
        this.expectedException = expectedException;
        switch(typeType) {
            case NULL:
                this.type = null;
                break;
            case INVALID:
                this.type = ProxyManagerImplLoadBuildTimeProxyTest.class;
                break;
            case VALID:
                this.type = String.class;
                break;
        }
        switch(loaderType) {
            case NULL:
                this.loader = null;
                break;
            case INVALID:
                this.loader = new InvalidClassLoader();
                break;
            case VALID:
                this.loader = String.class.getClassLoader();
//                this.loader = GeneratedClasses.getMostDerivedLoader(type, ProxyCollection.class);
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
                { ObjType.NULL,      ObjType.NULL,      null },  // 0
                { ObjType.NULL,      ObjType.INVALID,   null },  // 1
                { ObjType.NULL,      ObjType.VALID,     null },  // 2
                { ObjType.INVALID,   ObjType.NULL,      null },  // 3
                { ObjType.INVALID,   ObjType.INVALID,   null },  // 4
                { ObjType.INVALID,   ObjType.VALID,     null },  // 5
                { ObjType.VALID,     ObjType.NULL,      null },  // 6
                { ObjType.VALID,     ObjType.INVALID,   null },  // 7
                { ObjType.VALID,     ObjType.VALID,     null },  // 8
        });
    }

    @Test
    public void handleCallbackExceptionsTest() {
        try {
            // Utilizzo della reflection per ottenere il metodo
            Method loadBuildTimeProxy = ProxyManagerImpl.class.getDeclaredMethod("loadBuildTimeProxy", Class.class, ClassLoader.class);
            loadBuildTimeProxy.setAccessible(true);
            if (expectedException == null) {
                Assertions.assertDoesNotThrow(() -> {
                    // Codice di test che non dovrebbe sollevare un'eccezione
                    Object returnValue = loadBuildTimeProxy.invoke(proxyManagerImpl, this.type, this.loader);
                    System.out.println(returnValue);
//                    Assertions.assertEquals(loadedClass, this.type);
                });
            } else {
                Assertions.assertThrows(expectedException, () -> {
                    // Codice di test che dovrebbe sollevare un'eccezione
                    try {
                        loadBuildTimeProxy.invoke(proxyManagerImpl, this.type, this.loader);
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

    public static class InvalidClassLoader extends ClassLoader {
        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException {
            throw new ClassNotFoundException("Accesso negato!");
        }
    }
}
