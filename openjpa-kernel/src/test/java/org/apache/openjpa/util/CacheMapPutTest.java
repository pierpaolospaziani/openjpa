package org.apache.openjpa.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CacheMapPutTest {

    private CacheMap cacheMap;

    private Object key;
    private Object value;
    private boolean alreadyExists;
    private int mapSize;
    private boolean isCacheFull;
    private boolean isKeyPinned;
    private final String alreadyPresentValue = "it's not possible to test everything";

    private Object leastRecentlyUsedKey = null;


    public CacheMapPutTest(Object key,Object value, boolean alreadyExists, int mapSize,
                           boolean isFull, boolean isPinned) {
        configure(key, value, alreadyExists, mapSize, isFull, isPinned);
    }

    public void configure(Object key, Object value, boolean alreadyExist, int mapSize,
                          boolean isFull, boolean isPinned){
        this.key = key;
        this.value = value;
        this.alreadyExists = alreadyExist;
        this.mapSize = mapSize;
        this.isCacheFull = isFull;
        this.isKeyPinned = isPinned;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getTestParameters() {
        return Arrays.asList(new Object[][]{
                // key, value, alreadyExists, mapSize, isCacheFull, isKeyPinned
                {null, new Object(), false, -1, false, false},
                {new Object(), null, false, 0, false, false },
                {new Object(), new Object(), true, 1, false, false},
                {new Object(), new Object(), false, 1, true, false},
                {new Object(), new Object(), false, 1, false, true},
                /* added to increase coverage: */
                {new Object(), new Object(), true, 1, false, true},
                {new Object(), new Object(), true, 1, true, false},
        });
    }


    @Before
    public void setUp(){
        // this.cacheMap = new CacheMap(true, this.mapSize, this.mapSize, .75F, 16);
        this.cacheMap = new CacheMap(true, this.mapSize, this.mapSize + 2, .75F, 16);

        // counting the added elements
        int count = 0;
        /* adding the same elements if alreadyExists. It differs from the base object to check if the
           returned value is the same. */
        if (this.alreadyExists) {
            this.cacheMap.put(this.key, this.alreadyPresentValue);
            this.leastRecentlyUsedKey = this.key;
            count++;
        }

        /* adding elements if cache should be full */
        if (this.isCacheFull){
            for (; count < this.mapSize + 2; count++) {
                this.cacheMap.put(count, count);
                /* this is to take trace of the least recently used value */
                if (leastRecentlyUsedKey == null)
                    leastRecentlyUsedKey = count;
            }
        }

        if (this.isKeyPinned){
            this.cacheMap.pin(this.key);
        }
    }

    @Test
    public void putTest() {

        if (this.isCacheFull && !this.isKeyPinned){
            /* checking if the least recently used value is not in cache, but it's still present in the
               softMap */
            if (alreadyExists) {
                Assert.assertFalse(this.cacheMap.cacheMap.containsKey(this.leastRecentlyUsedKey));
                Assert.assertTrue(this.cacheMap.softMap.containsKey(this.leastRecentlyUsedKey));
            }
            else{
                Assert.assertFalse(this.cacheMap.cacheMap.containsKey(this.leastRecentlyUsedKey));
                Assert.assertTrue(this.cacheMap.softMap.containsKey(this.leastRecentlyUsedKey));
            }
        }

        Object oldValue = this.cacheMap.put(this.key, this.value);

        if (this.alreadyExists && this.mapSize != 0) {
            //the two values should be equals
            Assert.assertEquals(oldValue, this.alreadyPresentValue);
        } else {
            Assert.assertNull(oldValue);
        }

        // check if i can read the same value
        Object redValue = this.cacheMap.get(this.key);
        Assert.assertEquals(this.value, redValue);
    }
}
