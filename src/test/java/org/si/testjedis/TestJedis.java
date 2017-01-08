package org.si.testjedis;

//import java.util.Set;

//import redis.clients.jedis.Jedis;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

class AAA {
	private static final AAA instance = new AAA();
	private int i = 0;
	private AAA() {}
	public static final AAA getInstance() {
		return instance;
	}
	public void m() {
		System.out.println(++i);
	}
}

public class TestJedis {
    static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
    
	public static void main(String[] args) {
		/*Jedis jedis = null;
		try {
		  jedis = pool.getResource();
		  jedis.set("foo", "1");
		  String foobar = jedis.get("foo");
		  System.out.println(foobar);
		  jedis.incr("foo");
		  foobar = jedis.get("foo");
		  System.out.println(foobar);
		  jedis.zadd("sose", 0, "car"); jedis.zadd("sose", 0, "bike"); 
		  Set<String> sose = jedis.zrange("sose", 0, -1);
		}finally{
			if (jedis != null) {
		        jedis.close();
		    }
		}
		pool.destroy();*/
		final AAA aaa = AAA.getInstance();
		ExecutorService es = Executors.newFixedThreadPool(3);
		for(int i=0; i<3;i++) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			es.submit(new Runnable() {
				
				@Override
				public void run() {
					
					aaa.m();
				}
				
			});
		}
		es.shutdown();
		
	}

}
