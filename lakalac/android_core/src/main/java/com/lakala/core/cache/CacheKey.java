package com.lakala.core.cache;


import com.lakala.library.encryption.Digest;

/**
 * 数据缓存 Key
 *
 */
public class CacheKey {	
	private String keyEncode;

    /**
     * 私有构造函数,generate通过调用此构造函数生成CacheKey
     * @param key
     */
	private CacheKey(String key){
		this.keyEncode = Digest.md5(key == null ? "" : key);
	}

    /**
     * 返回Cache的键值
     * @return
     */
    public String getCacheKey(){
        return this.keyEncode;
    }

	@Override
	public String toString() {
		return keyEncode;
	}

    /**
     * 比较两个Key是否相等 ，可以接受的类型有 CacheKey、String
     * @param o 比较的对象
     * @return 相等为true ,不想等返回false
     */
	@Override
	public boolean equals(Object o) {
		if (o instanceof CacheKey){
			return this.getCacheKey().equals(((CacheKey) o).getCacheKey());
		}
		else if (o instanceof String){
			return this.getCacheKey().equals((String) o);
		}
		else{
			return false;
		}
	}
	
	/** 生成缓存 Key */
	public static CacheKey generate (String key){
		return new CacheKey(key);
	}
	
	/** 生成缓存 Key */
	public static CacheKey generate (String key1,String key2){
		return new CacheKey(String.format("%s.%s", key1, key2));
	}

    /** 生成缓存 Key */
    public static CacheKey generate (String key1,String key2,String key3){
        return new CacheKey(String.format("%s.%s.%s", key1, key2, key3));
    }

	/** 生成缓存 Key */
	public static CacheKey generate (String key1,int key2){
		return new CacheKey(String.format("%s.%d", key1, key2));
	}
	
	/** 生成缓存 Key */
	public static CacheKey generate (String key1,int key2,int key3){
		return new CacheKey(String.format("%s.%d.%d", key1, key2, key3));
	}
	
	/** 生成缓存 Key */
	public static CacheKey generate (String key1,String key2,int key3){
		return new CacheKey(String.format("%s.%s.%d", key1, key2, key3));
	}
}
