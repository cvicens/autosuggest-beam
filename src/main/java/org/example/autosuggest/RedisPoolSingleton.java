package org.example.autosuggest;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPoolSingleton {
    private JedisPool jedisPool = null;

    private static RedisPoolSingleton instance;
    
    private RedisPoolSingleton(String host, Integer port) {
        this.jedisPool = new JedisPool(new JedisPoolConfig(), host, port);
    }
    
    public static synchronized RedisPoolSingleton getInstance(String host, Integer port) {
        if (instance == null){
            instance = new RedisPoolSingleton(host, port);
        }
        return instance;
    }

	/**
	 * @return the jedisPool
	 */
	public JedisPool getJedisPool() {
		return jedisPool;
	}

	/**
	 * @param jedisPool the jedisPool to set
	 */
	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
}