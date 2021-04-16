package com.wooyoo.learning.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2014-2016, by topcheer, All rights reserved.
 * -----------------------------------------------------------------
 *
 * File: RedisUtils.java
 * Author: wang.kt
 * Version: V100R001C01
 * Create: 2019-09-24 16:35
 *
 * Changes (from 2019-09-24)
 * -----------------------------------------------------------------
 * 2019-09-24 : Create RedisUtils.java (wang.kt);
 * -----------------------------------------------------------------
 */
public class RedisUtils_pydx {

    private static final String KEY = "spdb";

    public static final String UID = "UID";

    public static final String DID = "DID";

    public static final String UUID = "UUID";

    public static String createUuid(){

        return java.util.UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }


    public static void saveRedisMsg(RedisTemplate redis , String key , String uid , String did, String uuid){

        JSONObject object = new JSONObject();

        object.put(UID,uid);

        object.put(DID,did);

        object.put(UUID,uuid);

        redis.opsForValue().set(KEY + key, object.toJSONString());

    }


    public static void delRedisMsg(RedisTemplate redis , String key ){
        if(redis.hasKey(KEY + key)){
            redis.delete(KEY + key);
        }
    }

    /**
     * @Description  通过key值，验证uuid是否相等
     * @Author       wkt
     * @Date         2019-09-24 17:20
     * @Param
     * @Return
     * @Exception
     */
    public static Boolean verifyRedisUuid(RedisTemplate redis, String key, String uuid){
        if(redis.hasKey(KEY + key)){
            String redisMsg = (String) redis.opsForValue().get((KEY + key));

            JSONObject object = JSONObject.parseObject(redisMsg);

            return object.getString(UUID).equals(uuid);
        }else{
            return false;
        }
    }

    public static JSONObject getRedisMsg(RedisTemplate redis, String key){

        if(redis.hasKey(KEY + key)){

            String redisMsg = (String) redis.opsForValue().get((KEY + key));

            return JSONObject.parseObject(redisMsg);
        }else{
            return null;
        }
    }
}
