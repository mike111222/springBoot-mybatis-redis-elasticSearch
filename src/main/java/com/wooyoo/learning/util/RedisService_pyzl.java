package com.wooyoo.learning.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService_pyzl {

	private static final Logger logger = Logger.getLogger(RedisService_pyzl.class);

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 缓存有效期（单位：秒)
	 */
//	@Value("${redistest.expireTime.minutes}")
	private int expireTime=30;

	public static final int forever = -1;

	public void setValue(String key, String value) {
		logger.info("set key:" + key + ",value:" + value + ",expire:"
				+ expireTime);
		redisTemplate.opsForValue().set(key, value);
		redisTemplate.expire(key, expireTime, TimeUnit.MINUTES);
	}

	/**
	 *
	 * @param key
	 * @param value
	 * @param expire
	 *            单位秒seconds
	 */
	public void setValue(String key, String value, int expire) {
		logger.info("set key:" + key + ",value:" + value + ",expire:" + expire);
		redisTemplate.opsForValue().set(key, value);
		if (expire > 0) {
			redisTemplate.expire(key, expire, TimeUnit.SECONDS);
		}
	}

	/**
	 * 取得缓存中得值，不改变过期时间
	 *
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		logger.info("get key:" + key);
		String value = redisTemplate.opsForValue().get(key);
		return value;
	}

	/**
	 * 取得缓存中得值，重设默认过期时间
	 *
	 * @param key
	 * @return
	 */
	public String getValueResetExpire(String key) {
		logger.info("get key:" + key);
		String value = redisTemplate.opsForValue().get(key);
		if (value != null) {
			redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
		}
		return value;
	}

	/**
	 * 取得缓存中得值，设置指定的过期时间<br>
	 * 如果过期时间<=0的话，就不改变过期时间。
	 *
	 * @param key
	 * @param expire
	 *            单位秒
	 * @return
	 */
	public String getValue(String key, int expire) {
		logger.info("get key:" + key);
		if (expire > 0) {
			redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
		}
		return redisTemplate.opsForValue().get(key);
	}

	/**
	 * 删除缓存中的值<br>
	 * 如果过期时间<=0的话，就不改变过期时间。
	 *
	 * @param key
	 *            单位秒
	 * @return
	 */
	public void del(String key) {
		logger.info("delete key:" + key);
		redisTemplate.delete(key);
	}
}
