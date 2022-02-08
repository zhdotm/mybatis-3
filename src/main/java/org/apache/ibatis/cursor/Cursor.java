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
package org.apache.ibatis.cursor;

import java.io.Closeable;

/**
 * 有些时候，我们希望逐⼀读⼊和处理查询结果，⽽不是⼀次读⼊整个结果集。因为前者能够减少对内存的占⽤，这在处理⼤
 * 量的数据时会显得⼗分必要。游标就能够帮助我们实现这⼀⽬的，它⽀持我们每次从结果集中取出⼀条结果。
 * Cursor contract to handle fetching items lazily using an Iterator.
 * Cursors are a perfect fit to handle millions of items queries that would not normally fits in memory.
 * If you use collections in resultMaps then cursor SQL queries must be ordered (resultOrdered="true")
 * using the id columns of the resultMap.
 *
 * @author Guillaume Darmont / guillaume@dropinocean.com
 */
public interface Cursor<T> extends Closeable, Iterable<T> {

  /**
   * 游标是否开启
   *
   * @return true if the cursor has started to fetch items from database.
   */
  boolean isOpen();

  /**
   * 是否已经完成所有遍历
   *
   * @return true if the cursor is fully consumed and has returned all elements matching the query.
   */
  boolean isConsumed();

  /**
   * 返回当前元素的索引，未开始返回-1
   * Get the current item index. The first item has the index 0.
   *
   * @return -1 if the first cursor item has not been retrieved. The index of the current item retrieved.
   */
  int getCurrentIndex();
}
