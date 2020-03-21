package com.imooc.utils;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @Title: Redis 工具类
 */
@Component
@Slf4j
public class RedisOperator {

	private final RedissonClient redissonClient;

	@Autowired
	public RedisOperator(RedissonClient redissonClient){
		this.redissonClient = redissonClient;
	}

	/**
	 * 获取字符串对象
	 *
	 * @param objectName
	 * @param <T>
	 * @return
	 */
	public <T> RBucket<T> getRBucket(String objectName){
		RBucket<T> bucket=redissonClient.getBucket(objectName);
		return bucket;
	}

	/**
	 * 获取Map对象
	 *
	 * @param objectName
	 * @return
	 */
	public <K,V> RMap<K, V> getRMap(String objectName){
		RMap<K, V> map=redissonClient.getMap(objectName);
		return map;
	}

	/**
	 * 获取有序集合
	 *
	 * @param objectName
	 * @return
	 */
	public <V> RSortedSet<V> getRSortedSet(String objectName){
		RSortedSet<V> sortedSet=redissonClient.getSortedSet(objectName);
		return sortedSet;
	}

	/**
	 * 获取集合
	 * @param objectName
	 * @return
	 */
	public <V> RSet<V> getRSet(String objectName){
		RSet<V> rSet=redissonClient.getSet(objectName);
		return rSet;
	}

	/**
	 * 获取列表
	 * @param objectName
	 * @return
	 */
	public <V> RList<V> getRList(String objectName){
		RList<V> rList=redissonClient.getList(objectName);
		return rList;
	}

	/**
	 * 获取队列
	 *
	 * @param objectName
	 * @return
	 */
	public <V> RQueue<V> getRQueue(String objectName){
		RQueue<V> rQueue=redissonClient.getQueue(objectName);
		return rQueue;
	}

	/**
	 * 获取双端队列
	 *
	 * @param objectName
	 * @return
	 */
	public <V> RDeque<V> getRDeque(String objectName){
		RDeque<V> rDeque=redissonClient.getDeque(objectName);
		return rDeque;
	}

	/**
	 * 根据name进行加锁操作
	 *
	 * @param lockName
	 * @param leaseTime 锁定时长
	 * @param unit 锁定时长单位
	 */
	public void lock(String lockName, long leaseTime, TimeUnit unit) {
		RLock lock = redissonClient.getLock(lockName);
		//lock提供带timeout参数，timeout结束强制解锁，防止死锁 ：1分钟
		lock.lock(leaseTime, unit);
	}

	/**
	 * 根据name进行解锁操作，与lock一一对应
	 *
	 * @param lockName
	 */
	public void unlock(String lockName) {
		RLock lock = redissonClient.getLock(lockName);
		lock.unlock();
	}

	/**
	 * 尝试获取锁
	 * @param lockKey
	 * @param unit 时间单位
	 * @param waitTime 最多等待时间
	 * @param leaseTime 上锁后自动释放锁时间
	 * @return
	 */
	public boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
		RLock lock = redissonClient.getLock(lockKey);
		try {
			return lock.tryLock(waitTime, leaseTime, unit);
		} catch (InterruptedException e) {
			return false;
		}
	}
	
	/**
	 * 实现命令：expire 设置过期时间，单位秒
	 * 
	 * @param key
	 * @return
	 */
	public void expire(String key, long timeout) {
		RBucket<Object> bucket = redissonClient.getBucket(key);
		boolean exists = bucket.isExists();
		if(!exists){
			log.warn("RedisOperator expire warn, the key : {} is not exists!", key);
			return;
		}
		bucket.expire(timeout, TimeUnit.SECONDS);
	}
	
	/**
	 * 实现命令：INCR key，增加key一次
	 * 
	 * @param key
	 * @return
	 */
	public long incr(String key, long delta) {
		RBucket<Object> bucket = redissonClient.getBucket(key);
		boolean exists = bucket.isExists();
		if(!exists){
			log.warn("RedisOperator incr warn, the key : {} is not exists!", key);
			return -0L;
		}
		RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
		return atomicLong.incrementAndGet();
	}

	/**
	 * 实现命令：DEL key，删除一个key
	 * 
	 * @param key
	 */
	public void del(String key) {
		RBucket<Object> bucket = redissonClient.getBucket(key);
		boolean exists = bucket.isExists();
		if(!exists){
			log.warn("RedisOperator del warn, the key : {} is not exists!", key);
			return;
		}
		bucket.delete();
	}

	/**
	 * 实现命令：SET key value，设置一个key-value（将字符串值 value关联到 key）
	 * 
	 * @param key
	 * @param value
	 */
	public void set(String key, String value) {
		RBucket<Object> bucket = redissonClient.getBucket(key);
		bucket.set(value);
	}

	/**
	 * 实现命令：SET key value EX seconds，设置key-value和超时时间（秒）
	 * 
	 * @param key
	 * @param value
	 * @param timeout
	 *            （以秒为单位）
	 */
	public void set(String key, String value, long timeout) {
		RBucket<Object> bucket = redissonClient.getBucket(key);
		bucket.set(value, timeout, TimeUnit.SECONDS);
	}


}