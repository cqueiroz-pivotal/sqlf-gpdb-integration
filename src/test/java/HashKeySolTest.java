import org.junit.Test;

import java.util.HashMap;

/**
 * Created by cq on 21/3/14.
 */
public class HashKeySolTest {

    @Test
    public void testHashKeySol(){

        final int TABLE_BUCKETS = 113;
        HashMap keyValue = new HashMap();
        for(int i = 0 ;i < 1000; i++) {
            for (int j = 0; j < 100; j++) {
                //real key
                int realKey = i;


                int bucketOfKey = realKey % TABLE_BUCKETS;
                int batchKeyValue = 0;

                if (keyValue.containsKey(bucketOfKey)) {
                    batchKeyValue = (Integer) keyValue.get(bucketOfKey) + TABLE_BUCKETS;
                    keyValue.put(bucketOfKey, batchKeyValue);
                } else {
                    batchKeyValue = bucketOfKey;
                    keyValue.put(bucketOfKey, bucketOfKey);
                }
                int dummyKey = batchKeyValue;

                System.out.println(dummyKey + " size kv: " + keyValue.size() + " bk: " + bucketOfKey);
            }
        }
    }
}
