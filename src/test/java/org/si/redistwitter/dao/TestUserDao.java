package org.si.redistwitter.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class TestUserDao {
	static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
	Jedis jedis = null;
	UserDao userDao;
	
	@Before
	public void setUp(){
		userDao = new UserDao();
		jedis = pool.getResource();
	}
	
	@Test
	public void testAddUser() {
		long id = userDao.addUser(jedis, "si", "");
		assertThat(" 用户返回的 id 必须大于 0 ", id,  greaterThan(0l));
	}
	
	@After
	public void tearDown(){
		userDao = null;
		pool.destroy();
	}

}
