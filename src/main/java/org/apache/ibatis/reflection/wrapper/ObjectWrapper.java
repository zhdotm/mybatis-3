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
package org.apache.ibatis.reflection.wrapper;

import java.util.List;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * 对象封装器
 *
 * @author Clinton Begin
 */
public interface ObjectWrapper {

  /**
   * 获得被包装对象某个属性的值
   *
   * @param prop
   * @return
   */
  Object get(PropertyTokenizer prop);

  /**
   * 设置被包装对象某个属性的值
   *
   * @param prop
   * @param value
   */
  void set(PropertyTokenizer prop, Object value);

  /**
   * 找到对应的属性名称
   *
   * @param name
   * @param useCamelCaseMapping
   * @return
   */
  String findProperty(String name, boolean useCamelCaseMapping);

  /**
   * 获得所有的属性 get⽅法名称
   *
   * @return
   */
  String[] getGetterNames();

  /**
   * 获得所有的属性 set⽅法名称
   *
   * @return
   */
  String[] getSetterNames();

  /**
   * 获得指定属性的 set⽅法的类型
   *
   * @param name
   * @return
   */
  Class<?> getSetterType(String name);

  /**
   * 获得指定属性的 get⽅法的类型
   *
   * @param name
   * @return
   */
  Class<?> getGetterType(String name);

  /**
   * 判断某个属性是否有对应的 set⽅法
   *
   * @param name
   * @return
   */
  boolean hasSetter(String name);

  /**
   * 判断某个属性是否有对应的 get⽅法
   *
   * @param name
   * @return
   */
  boolean hasGetter(String name);

  /**
   * 实例化某个属性的值
   *
   * @param name
   * @param prop
   * @param objectFactory
   * @return
   */
  MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

  boolean isCollection();

  void add(Object element);

  <E> void addAll(List<E> element);

}
