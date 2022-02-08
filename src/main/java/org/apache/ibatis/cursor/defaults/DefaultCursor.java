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
package org.apache.ibatis.cursor.defaults;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetWrapper;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 对DefaultCursor类⽽⾔，结果集中的所有记录都已经存储在了内存中，DefaultCursor类只负责逐⼀给出这些记录⽽已。
 * This is the default implementation of a MyBatis Cursor.
 * This implementation is not thread safe.
 *
 * @author Guillaume Darmont / guillaume@dropinocean.com
 */
public class DefaultCursor<T> implements Cursor<T> {

  // ResultSetHandler stuff
  private final DefaultResultSetHandler resultSetHandler;
  private final ResultMap resultMap;
  private final ResultSetWrapper rsw;
  private final RowBounds rowBounds;
  protected final ObjectWrapperResultHandler<T> objectWrapperResultHandler = new ObjectWrapperResultHandler<>();

  private final CursorIterator cursorIterator = new CursorIterator();
  private boolean iteratorRetrieved;

  private CursorStatus status = CursorStatus.CREATED;
  private int indexWithRowBound = -1;

  private enum CursorStatus {

    /**
     * 新创建游标，结果集尚未消费
     * A freshly created cursor, database ResultSet consuming has not started.
     */
    CREATED,
    /**
     * 游标正在被使用，结果集正在被消费
     * A cursor currently in use, database ResultSet consuming has started.
     */
    OPEN,
    /**
     * 游标已关闭，结果集未被完全消费
     * A closed cursor, not fully consumed.
     */
    CLOSED,
    /**
     * 游标已关闭，结果集被完全消费
     * A fully consumed cursor, a consumed cursor is always closed.
     */
    CONSUMED
  }

  public DefaultCursor(DefaultResultSetHandler resultSetHandler, ResultMap resultMap, ResultSetWrapper rsw, RowBounds rowBounds) {
    this.resultSetHandler = resultSetHandler;
    this.resultMap = resultMap;
    this.rsw = rsw;
    this.rowBounds = rowBounds;
  }

  @Override
  public boolean isOpen() {
    return status == CursorStatus.OPEN;
  }

  @Override
  public boolean isConsumed() {
    return status == CursorStatus.CONSUMED;
  }

  @Override
  public int getCurrentIndex() {
    return rowBounds.getOffset() + cursorIterator.iteratorIndex;
  }

  /**
   * 返回迭代器
   *
   * @return 迭代器
   */
  @Override
  public Iterator<T> iterator() {
    //迭代器已经给出
    if (iteratorRetrieved) {
      throw new IllegalStateException("Cannot open more than one iterator on a Cursor");
    }
    //游标已经关闭
    if (isClosed()) {
      throw new IllegalStateException("A Cursor is already closed.");
    }
    //设置迭代器已给出状态
    iteratorRetrieved = true;
    //返回迭代器
    return cursorIterator;
  }

  @Override
  public void close() {
    if (isClosed()) {
      return;
    }

    ResultSet rs = rsw.getResultSet();
    try {
      if (rs != null) {
        rs.close();
      }
    } catch (SQLException e) {
      // ignore
    } finally {
      status = CursorStatus.CLOSED;
    }
  }

  /**
   * 获取下一个RowBound
   * 考虑到边界限制（翻页限制），从数据库中获取下个对象
   *
   * @return 下一个对象
   */
  protected T fetchNextUsingRowBound() {
    //从数据库中获取下个对象
    T result = fetchNextObjectFromDatabase();
    //如果对象存在但是不满足边界限制，则持续读取数据库结果中的下一个，直到边界其实位置
    while (objectWrapperResultHandler.fetched && indexWithRowBound < rowBounds.getOffset()) {
      result = fetchNextObjectFromDatabase();
    }
    return result;
  }

  /**
   * 从数据库中获取下个对象
   * fetchNextObjectFromDatabase⽅法的中⽂含义为“从数据库获取下⼀个对象”，从⽅法名称上看，该⽅法似乎会从数据库中查询
   * 下⼀条记录。但实际上并⾮如此，该⽅法并不会引发数据库查询操作。因为，在该⽅法被调⽤之前，数据库查询的结果集已经
   * 完整地保存在了 rsw变量中。fetchNextObjectFromDatabase⽅法只是从结果集中取出下⼀条记录，⽽⾮真正地去数据库查询下⼀
   * 条记录。
   *
   * @return 下个对象
   */
  protected T fetchNextObjectFromDatabase() {
    if (isClosed()) {
      return null;
    }

    try {
      objectWrapperResultHandler.fetched = false;
      status = CursorStatus.OPEN;
      if (!rsw.getResultSet().isClosed()) {
        //从结果集中取出一条数据将转换成对象，并将其存入objectWrapperResultHandler中
        resultSetHandler.handleRowValues(rsw, resultMap, objectWrapperResultHandler, RowBounds.DEFAULT, null);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    T next = objectWrapperResultHandler.result;
    if (objectWrapperResultHandler.fetched) {
      indexWithRowBound++;
    }
    // No more object or limit reached
    if (!objectWrapperResultHandler.fetched || getReadItemsCount() == rowBounds.getOffset() + rowBounds.getLimit()) {
      close();
      status = CursorStatus.CONSUMED;
    }
    objectWrapperResultHandler.result = null;

    return next;
  }

  private boolean isClosed() {
    return status == CursorStatus.CLOSED || status == CursorStatus.CONSUMED;
  }

  private int getReadItemsCount() {
    return indexWithRowBound + 1;
  }

  protected static class ObjectWrapperResultHandler<T> implements ResultHandler<T> {

    protected T result;
    protected boolean fetched;

    @Override
    public void handleResult(ResultContext<? extends T> context) {
      this.result = context.getResultObject();
      context.stop();
      fetched = true;
    }
  }

  protected class CursorIterator implements Iterator<T> {

    /**
     * Holder for the next object to be returned.
     */
    T object;

    /**
     * Index of objects returned using next(), and as such, visible to users.
     */
    int iteratorIndex = -1;

    @Override
    public boolean hasNext() {
      if (!objectWrapperResultHandler.fetched) {
        object = fetchNextUsingRowBound();
      }
      return objectWrapperResultHandler.fetched;
    }

    @Override
    public T next() {
      // Fill next with object fetched from hasNext()
      T next = object;

      if (!objectWrapperResultHandler.fetched) {
        next = fetchNextUsingRowBound();
      }

      if (objectWrapperResultHandler.fetched) {
        objectWrapperResultHandler.fetched = false;
        object = null;
        iteratorIndex++;
        return next;
      }
      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Cannot remove element from Cursor");
    }
  }
}
