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
package org.apache.ibatis.session;

/**
 * ResultContext接口表⽰结果上下⽂，其中存放了数据库操作的⼀个结果（对应数据库中的⼀条记录）
 *
 * @author Clinton Begin
 */
public interface ResultContext<T> {

  T getResultObject();

  int getResultCount();

  boolean isStopped();

  void stop();

}
