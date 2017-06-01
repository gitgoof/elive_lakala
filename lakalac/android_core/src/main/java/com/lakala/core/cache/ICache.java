
package com.lakala.core.cache;

/*
 * 缓存接口
 * @author Michael
 *
 * @param <P> put 时的数据类型
 * @param <G> get 时的数据类型
 */
 public interface ICache<P,G>{

    /**
     * 设置Cache 类别
     * @param category 类别字符串，不能为空或空串。
     */
    void setCategory(String category);

    /**
     * 缓存数据，如果数据已经存在则会更新它。
     * @param key   数据的 key 必填且不能为空
     * @param date  过期日期，不填表示永不过期。
     * @param data  数据，必填且不能为空
     * @return 缓存成功返回 true;
     */

     boolean put(CacheKey key, ExpireDate date, P data);

    /**
     * 缓存数据，如果数据已经存在则会更新它。
     * @param key      数据的 key 必填且不能为空
     * @param version  数据的版本，版本号必须 >= 1，Integer.MAX_VALUE 表示永不过期
     * @param data     数据，必填且不能为空
     * @return 缓存成功返回 true;
     */
     boolean put(CacheKey key, int version, P data);

    /**
     * 缓存数据，如果数据已经存在则会更新它。
     * 如果 date == null 并且 version == Integer.MAX_VALUE，表示数据永不过期。
     * @param key      数据的 key 必填且不能为空
     * @param date     过期日期，传空值表示日永不过期
     * @param version  数据的版本，版本号必须 >= 1，Integer.MAX_VALUE 表示版本永不过期
     * @param data     数据，必填且不能为空
     * @return 缓存成功返回 true;
     */
     boolean put(CacheKey key, ExpireDate date, int version, P data);

    /**
     * 获取缓存的数据，如果数据已过期、或过期时间不正确、或Key不存在则返回 null。
     * @param key  数据的 key
     * @return 返回缓存的数据
     */
     G get(CacheKey key);

    /**
     * 获取缓存的数据，如果数据已过期（包括版本过期、日期过期）、或过期时间不正确、或Key不存在则返回 null。
     * @param key         数据的 key
     * @param newVersion  数据的最新版本，传 -1 表示不进行版本比较。
     * @return 返回缓存的数据
     */
     G get(CacheKey key, int newVersion);

    /**
     * 获取缓存的数据（包括过期数据）。
     * @param key  数据的 key
     * @return 返回缓存的数据，如果数据不存在则返回 null。
     */
     G getExpiredData(CacheKey key);

    /**
     * 获取缓存的数据的过期日期。
     * @param key  数据的 key
     * @return 过期日期，如果指定的key 不存在或数据格式错误则返回 null。
     */
     ExpireDate getExpireDate(CacheKey key);

    /**
     * 获取缓存的数据的版本。
     * @param key  数据的 key
     * @return 数据的版本，如果指定的key 不存在返回 -1。
     */
     int getVersion(CacheKey key);

    /**
     * 移除缓存的数据
     * @param key 数据的 key
     */
     void remove(CacheKey key);

    /**
     * 移除所有过期(日期过期)的缓存数据
     */
     void clean();

    /**
     * 查询由 key 指定的数据是否过期。
     * @param key
     * @return  数据过期或key不存在或保存的日期格式有错误返回 true，未过期返回 false;
     */
     boolean isExpire(CacheKey key);

    /**
     * 查询由 key 指定的数据是否过期。
     * @param key
     * @param newVersion  数据的最新版本.
     * @return  数据版本过期或key不存在 true，未过期返回 false;
     */
     boolean isExpire(CacheKey key, int newVersion);

     /**
     * 查询由 key 指定的数据是否过期，日期或版本有一项过期即视为过期。
     * @param key
     * @param newVersion  数据的最新版本，传 -1 表示不进行版本比较。
     * @return  数据过期或key不存在或保存的日期格式有错误返回 true，未过期返回 false;
     */
     boolean isExpireByDateAndVersion(CacheKey key, int newVersion);

    /**
     * 查询由key 指定的数据是否存在（包括过期数据）
     * @param key
     * @return 如果数据存在返回true，不存在返回 false。
     */
     boolean isExist(CacheKey key);

//    /**
//     * 获取 key 迭代器。
//     * @return
//     */
//     Iterator<CacheKey> keyIterator();
}

