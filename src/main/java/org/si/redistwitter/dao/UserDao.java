package org.si.redistwitter.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.si.redistwitter.object.User;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Tuple;

public class UserDao {
	static JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
	    
	public long addUser(Jedis jedis, String userName, String password) {
		long id = -1;
		try {
		    id = jedis.incr("user:id");
		    Pipeline pipeline = jedis.pipelined();
		    String key = "user:" + id;
		    Map<String, String> hash = new HashMap<>();
		    hash.put("uid", id + "");
		    hash.put("userName", userName);
		    hash.put("followers", "0");
		    hash.put("following", "0");
		    hash.put("posts", "0");
		    pipeline.hmset(key, hash);
		    pipeline.hset("user:" + userName, password, id + "");
		    pipeline.sync();
		    return id;
		}finally{
			if (jedis != null) {
		        jedis.close();
		    }
		}
	}
	
	/*public long createProfile(Jedis jedis, String userName, String password, String uid) {
		return jedis.hset("profile:" + userName, password, uid);
	}*/
	
	public int followUser(Jedis jedis, String uid, String otheruid) {
		/*String key = "following:" + uid;
		String otherkey = "followers:" + otheruid;*/
		String key = "following";
		String otherkey = "followers";
		if(jedis.zscore(key, otherkey) != null) {
			return 0;
		}
		Pipeline pipeline = jedis.pipelined();
		long now = new Date().getTime();
		Response<Long> following = pipeline.zadd(key, now, otheruid);
		Response<Long> followers = pipeline.zadd(otherkey, now, uid);
		Response<Set<Tuple>> statusAndScore = pipeline.zrevrangeWithScores("profile:" + otheruid, 0, 100 - 1);
		pipeline.sync();
		pipeline.hincrBy("user:"+uid, key, following.get());
		pipeline.hincrBy("user:"+otheruid, otherkey, followers.get());
		if(statusAndScore != null) {
			Set<Tuple> tuples = statusAndScore.get();
			Map<String, Double> scoreMembers = new HashMap<>();
			for(Tuple t : tuples) {
				scoreMembers.put(t.getElement(), t.getScore());
			}
			pipeline.zadd("home:" + uid, scoreMembers);
		}
		pipeline.zremrangeByRank("home:" + uid, 0, 100 - 1);
		pipeline.sync();
		return 1;
	}
	
    public int unfollowUser(Jedis jedis, String uid, String otheruid) {
    	String key = "following:" + uid;
		String otherkey = "followers:" + otheruid;
		if(jedis.zscore(key, otherkey) < 0) {
			return 0;
		}
		Pipeline pipeline = jedis.pipelined();
		Response<Long> following = pipeline.zrem(key, otheruid);
		Response<Long> followers = pipeline.zrem(otherkey, uid);
		Response<Set<String>> statuses = pipeline.zrevrange("profile:" + otheruid, 0, 100 - 1);
		pipeline.sync();
		pipeline.hincrBy("user:"+uid, key, following.get());
		pipeline.hincrBy("user:"+otheruid, otherkey, followers.get());
		if(statuses != null) {
			Set<String> status = statuses.get();
			ArrayList<String> sp = new ArrayList<>();
			for(String s : status) {
				sp.add(s);
			}
			pipeline.zrem("home:" + uid, (String[]) sp.toArray());
		}
		pipeline.sync();
		return 1;
	}

	public User login(Jedis jedis, String userName, String password) {
		/*Pipeline pipeline = jedis.pipelined();
		Response<String> uid = pipeline.hget("user:" + username, password);
		pipeline.sync();
		Response<Map<String, String>> userinfo = pipeline.hgetAll("user:" + uid.get());
		pipeline.sync();*/
		String uid = jedis.hget("user:" + userName, password);
		Map<String, String> uinfo = jedis.hgetAll("user:" + uid);
		User user = null;
		if(!uinfo.isEmpty()) {
			user = new User();
			user.setUid(Integer.parseInt(uinfo.get("uid")));
			user.setUserName(userName);
			user.setPassword(password);
			user.setFollowing(Integer.parseInt(uinfo.get("following")));
			user.setFollowers(Integer.parseInt(uinfo.get("followers")));
			user.setPosts(Integer.parseInt(uinfo.get("posts")));
		}
		return user;
	}

}
