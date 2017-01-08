package org.si.tool;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class ThreadHelper {
	private static final ThreadHelper instance = new ThreadHelper(); 
	private ThreadLocal<Jedis> threadJedis = new ThreadLocal<Jedis>();
	private JedisPool pool;
	
	private ThreadHelper() {}
	
	public static ThreadHelper getInstance() {
		return instance;
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}

	public Jedis getJedis() {
		Jedis conn = threadJedis.get();
		if(conn == null){
			conn = pool.getResource();
			threadJedis.set(conn);
		}
		return conn;
	}

}
