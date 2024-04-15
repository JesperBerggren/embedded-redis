package redis.embedded;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RedisServerClusterTest {

    private RedisServer redisServer1;
    private RedisServer redisServer2;

    @BeforeAll
    public void setUp() throws Exception {
        redisServer1 = RedisServer.builder()
                .port(6300)
                .build();

        redisServer2 = RedisServer.builder()
                .port(6301)
                .slaveOf("localhost", 6300)
                .build();

        redisServer1.start();
        redisServer2.start();
    }

    @Test
    public void testSimpleOperationsAfterRun() throws Exception {
        JedisPool pool = null;
        Jedis jedis = null;
        try {
            pool = new JedisPool("localhost", 6300);
            jedis = pool.getResource();
            jedis.mset("abc", "1", "def", "2");

            assertEquals("1", jedis.mget("abc").get(0));
            assertEquals("2", jedis.mget("def").get(0));
            assertEquals(null, jedis.mget("xyz").get(0));
        } finally {
            if (jedis != null)
                pool.returnResource(jedis);
        }
    }


    @AfterAll
    public void tearDown() throws Exception {
        redisServer1.stop();
        redisServer2.stop();
    }
}
