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
package org.apache.ibatis.executor.resultset;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.cursor.Cursor;

/**
 * 结果集处理器
 *
 * @author Clinton Begin
 */
public interface ResultSetHandler {

  /**
   * 将Statement的执⾏结果处理为List
   * 通过 handleResultSets⽅法的名称（Sets为复数形式）也能看出，它能够处理多结果集。在处理多结果集时，我们得到的是两层
   * 列表，即结果集列表和嵌套在其中的结果列表
   *
   * @param stmt
   * @param <E>
   * @return
   * @throws SQLException
   */
  <E> List<E> handleResultSets(Statement stmt) throws SQLException;

  /**
   * 将Statement的执⾏结果处理为Map
   *
   * @param stmt
   * @param <E>
   * @return
   * @throws SQLException
   */
  <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException;

  /**
   * 处理存储过程的输出结果
   *
   * @param cs
   * @throws SQLException
   */
  void handleOutputParameters(CallableStatement cs) throws SQLException;

}
