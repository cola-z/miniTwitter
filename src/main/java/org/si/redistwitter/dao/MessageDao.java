package org.si.redistwitter.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

public class MessageDao {
	static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
    
	public long addMessage(Jedis jedis, String uid, String message) {
		
		long id = -1;
		try {
		    id = jedis.incr("status:id");
		    Pipeline pipeline = jedis.pipelined();
		    String key = "status:" + id;
		    Map<String, String> data = new HashMap<>();
		    String messageId = id + "";
		    data.put("id", messageId);
		    data.put("uid", uid);
		    data.put("message", message);
		    Date d = new Date();
		    data.put("posted", d.getTime() + "");
		    pipeline.hmset(key, data);
		    pipeline.hincrBy("user:" + uid, "posts", 1);
		    pipeline.zadd("message:" + uid, (new Date()).getTime(), messageId);
		    pipeline.sync();
		    return id;
		}finally{
			if (jedis != null) {
		        jedis.close();
		    }
		}
		
	}
	
	public List<Map<String, String>> getLotMessages(Jedis jedis, String uid) {
		String idstr = jedis.get("user:id");
		int idlimit = Integer.parseInt(idstr);
		int uidval = Integer.parseInt(uid);
		Pipeline pipeline = jedis.pipelined();
		List<Map<String, String>> result = new ArrayList<>();
		for(int i = 1; i <= idlimit; i++) {
			if(i != uidval) {
				List<Map<String, String>> list = getStatusMessage(jedis, i + "", 1, 3);
				result.addAll(list);
			}	
		}
		pipeline.sync();
		return result;
		//return null;
	}
	
	//public List<Map<String, String>> getStatusMessage(Jedis jedis, String uid, String timeline, long page, long count) {
	public List<Map<String, String>> getStatusMessage(Jedis jedis, String uid, long page, long count) {
		
		//try {
		    Set<String> statuses = jedis.zrevrange("message:" + uid, (page - 1), page * count - 1);
			
		    Pipeline pipeline = jedis.pipelined();
		    List<Map<String, String>> result = new ArrayList<>();
		    for(String messageId: statuses) {
		    	result.add( jedis.hgetAll("status:" + messageId) );
		    }
		    /*for(long messageId = (page - 1) * count + 1; messageId < page * count + 1; messageId++) {
		    	result.add( jedis.hgetAll("status:" + messageId) );
		    }*/
		    pipeline.sync();
		    return result;
		/*}finally{
			if (jedis != null) {
		        jedis.close();
		    }
		}*/
		
	}
	
    /*public List<Map<String, String>> getStatusMessage2(Jedis jedis, String uid, long page, long count) {
		    Pipeline pipeline = jedis.pipelined();
		    List<Map<String, String>> result = new ArrayList<>();
		    for(long messageId = (page - 1) * count + 1; messageId < page * count + 1; messageId++) {
		    	result.add( jedis.hgetAll("status:" + messageId) );
		    }
		    pipeline.sync();
		    return result;
	}*/
	
	public long updateTimeline(Jedis jedis, String uid, String message) {
		long id = addMessage(jedis, uid, message);
		if(id <= 0) return -1;
		String posted = jedis.hget("status:" + uid, "posted");
		if(posted == null) return -1;
		jedis.zadd("status:" + uid, Double.parseDouble(posted), id + "");
		//post = Double.parseDouble(posted), id
		syndicate_status(jedis, uid, Double.parseDouble(posted), id + "");
		return id;
	}

	private void syndicate_status(Jedis jedis, String uid, double post, String id) {
		int start = 0;
		Set<String> followers = jedis.zrangeByScore("followers:" + uid, start, Double.MAX_VALUE, 0, 100);
		Pipeline pipeline = jedis.pipelined();
		for(String follower : followers) {
			String key = "home:" + follower;
			jedis.zadd(key, post, id);
			pipeline.zremrangeByRank(key, 0, 100 - 1);
		}
		pipeline.sync();
		if(followers.size() >= 100) {
            // 延迟操作
		}
	}
	
	public void deleteMessage(Jedis jedis, String uid, String mid) {
		String key = "status:" + mid;
		Pipeline pipeline = jedis.pipelined();
		pipeline.del(key);
		pipeline.zrem("profile:" + uid, mid);
		pipeline.zrem("home:" + uid, mid);
		pipeline.hincrBy("user:" + uid, "posts", -1);
		pipeline.sync();
	}
	
}
