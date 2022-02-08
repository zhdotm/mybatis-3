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
package org.apache.ibatis.scripting;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;

/**
 * 语⾔驱动类完成的主要⼯作就是⽣成SqlSource
 */
public interface LanguageDriver {

  /**
   * Creates a {@link ParameterHandler} that passes the actual parameters to the the JDBC statement.
   *
   * @param mappedStatement The mapped statement that is being executed
   * @param parameterObject The input parameter object (can be null)
   * @param boundSql        The resulting SQL once the dynamic language has been executed.
   * @return the parameter handler
   * @author Frank D. Martinez [mnesarco]
   * @see DefaultParameterHandler
   */
  ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql);

  /**
   * 解析映射⽂件⽣成 SqlSource，该方法在mybatis启动阶段读取映射接口或者映射文件时被调用
   * <p>
   * Creates an {@link SqlSource} that will hold the statement read from a mapper xml file.
   * It is called during startup, when the mapped statement is read from a class or an xml file.
   *
   * @param configuration The MyBatis configuration 配置信息
   * @param script        XNode parsed from a XML file 映射文件中的节点
   * @param parameterType input parameter type got from a mapper method or specified in the parameterType xml attribute. Can be null. 参数类型
   * @return the sql source
   */
  SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType);

  /**
   * Creates an {@link SqlSource} that will hold the statement read from an annotation.
   * It is called during startup, when the mapped statement is read from a class or an xml file.
   *
   * @param configuration The MyBatis configuration
   * @param script        The content of the annotation
   * @param parameterType input parameter type got from a mapper method or specified in the parameterType xml attribute. Can be null.
   * @return the sql source
   */
  SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType);

}
