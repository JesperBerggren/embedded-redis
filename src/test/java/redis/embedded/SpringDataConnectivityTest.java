package redis.embedded;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Connection;
import redis.clients.jedis.exceptions.JedisConnectionException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpringDataConnectivityTest {

    private RedisServer redisServer;
    private Connection client;

    @BeforeAll
    public void setUp() throws Exception {
        redisServer = new RedisServer(6379);
        redisServer.start();
    }

    @Test
    public void checkUnknownHost() {
        assertThrows(JedisConnectionException.class, () -> {
            this.client = new Connection("someunknownhost", 6379);
            this.client.connect();
        });
    }

    @Test
    public void checkWrongPort() {
        assertThrows(JedisConnectionException.class, () -> {
            this.client = new Connection("127.0.0.1", 55665);
            this.client.connect();
        });
    }

    @Test
    public void connectIfNotConnectedWhenSettingTimeoutInfinite() {
        this.client = new Connection("localhost", 6379);
        this.client.setTimeoutInfinite();
    }

    @Test
    public void checkCloseable() {
        this.client = new Connection("localhost", 6379);
        this.client.connect();
        this.client.close();
    }

    @AfterAll
    public void tearDown() throws Exception {
        if (this.client != null) {
            this.client.close();
        }

        if (this.redisServer != null) {
            this.redisServer.stop();
        }
    }
}
