/*
 *    Copyright 2009-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.executor;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;

/**
 * 执⾏器
 * 每个⼦包都为执⾏器提供了⼀些⼦功能。但是最终这些⼦功能均由 Executor接⼜及其实现类串接了起来，共同向外提供服务
 *
 * @author Clinton Begin
 */
public interface Executor {

  ResultHandler NO_RESULT_HANDLER = null;

  /**
   * 数据更新操作（增删改）
   *
   * @param ms
   * @param parameter
   * @return
   * @throws SQLException
   */
  int update(MappedStatement ms, Object parameter) throws SQLException;

  /**
   * 数据查询操作，返沪结果为列表
   *
   * @param ms
   * @param parameter
   * @param rowBounds
   * @param resultHandler
   * @param cacheKey
   * @param boundSql
   * @param <E>
   * @return
   * @throws SQLException
   */
  <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException;

  /**
   * 数据查询操作，返沪结果为列表
   *
   * @param ms
   * @param parameter
   * @param rowBounds
   * @param resultHandler
   * @param <E>
   * @return
   * @throws SQLException
   */
  <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;

  /**
   * 数据查询操作，返沪结果为游标形式
   *
   * @param ms
   * @param parameter
   * @param rowBounds
   * @param <E>
   * @return
   * @throws SQLException
   */
  <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException;

  /**
   * 清理缓存
   *
   * @return
   * @throws SQLException
   */
  List<BatchResult> flushStatements() throws SQLException;

  /**
   * 提交事务
   *
   * @param required
   * @throws SQLException
   */
  void commit(boolean required) throws SQLException;

  /**
   * 回滚事务
   *
   * @param required
   * @throws SQLException
   */
  void rollback(boolean required) throws SQLException;

  /**
   * 创建当前查询的缓存键值
   *
   * @param ms
   * @param parameterObject
   * @param rowBounds
   * @param boundSql
   * @return
   */
  CacheKey createCacheKey(MappedStatement ms, Object parameterObject, RowBounds rowBounds, BoundSql boundSql);

  /**
   * 本地缓存是否有指定值
   *
   * @param ms
   * @param key
   * @return
   */
  boolean isCached(MappedStatement ms, CacheKey key);

  /**
   * 清理本地缓存
   */
  void clearLocalCache();

  /**
   * 懒加载
   *
   * @param ms
   * @param resultObject
   * @param property
   * @param key
   * @param targetType
   */
  void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType);

  /**
   * 获取事务
   *
   * @return
   */
  Transaction getTransaction();

  /**
   * 关闭执行器
   *
   * @param forceRollback
   */
  void close(boolean forceRollback);

  /**
   * 判断执行器是否关闭
   *
   * @return
   */
  boolean isClosed();

  /**
   * 设置执行器包装
   *
   * @param executor
   */
  void setExecutorWrapper(Executor executor);

}
