package org.si.redistwitter.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/*import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;*/

public class TestMessageDao {
	static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
	Jedis jedis = null;
	MessageDao messageDao;
	
	@Before
	public void setUp(){
		messageDao = new MessageDao();
		jedis = pool.getResource();
	}
	
	@Test
	public void testGetStatusMessage() {
		System.out.println(messageDao.getStatusMessage(jedis, "1", 2, 3));
	}
	
	@Test
	public void testGetLotMessages() {
		System.out.println(messageDao.getLotMessages(jedis, "1"));
	}
	
	@After
	public void tearDown(){
		messageDao = null;
		pool.destroy();
	}

}
