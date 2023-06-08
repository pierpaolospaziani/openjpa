package org.apache.openjpa.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@RunWith(Parameterized.class)
public class ProxyManagerImplCopyDateTest {
    private static ProxyManagerImpl proxyManagerImpl;
    private Date orig;
    private final Class<? extends Exception> expectedException;
    private enum ObjType {
        NULL, INVALID, VALID
    }
    public ProxyManagerImplCopyDateTest(ObjType origType, Class<? extends Exception> expectedException){
        this.expectedException = expectedException;
        switch(origType) {
            case NULL:
                this.orig = null;
                break;
            case INVALID:
                Date date = new Date();
                date.setMonth(14);
                this.orig = date;
                break;
            case VALID:
                LocalDateTime currentDateTime = LocalDateTime.now();
                this.orig = Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant());
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
                { ObjType.NULL,      null },  // 0
                { ObjType.NULL,      null },  // 1
                { ObjType.NULL,      null },  // 2
                { ObjType.INVALID,   null },  // 3
                { ObjType.INVALID,   null },  // 4
                { ObjType.INVALID,   null },  // 5
                { ObjType.VALID,     null },  // 6
                { ObjType.VALID,     null },  // 7
                { ObjType.VALID,     null },  // 8
        });
    }

    @Test
    public void copyDateTest() {
        if (expectedException == null) {
            Assertions.assertDoesNotThrow(() -> {
                // Codice di test che non dovrebbe sollevare un'eccezione
                Date result = proxyManagerImpl.copyDate(this.orig);
//                System.out.println(result);
            });
        } else {
            Assertions.assertThrows(expectedException, () -> {
                // Codice di test che dovrebbe sollevare un'eccezione

                Assert.fail();
            });
        }
    }
}
