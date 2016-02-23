/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.datatorrent.lib.algo;

import java.util.HashMap;
import java.util.Map;

import com.datatorrent.api.DefaultInputPort;
import com.datatorrent.api.DefaultOutputPort;
import com.datatorrent.api.annotation.OperatorAnnotation;

import com.datatorrent.lib.util.BaseMatchOperator;

/**
 * This operator filters the incoming stream of key value pairs by obtaining the values corresponding to a specified key,
 * and comparing those values to a specified number.&nbsp;The first key value pair, in each window, to satisfy the comparison is emitted.
 * <p>
 * A compare metric on a Number tuple based on the property "key", "value", and "cmp"; the first match is emitted.
 *  The comparison is done by getting double value from the Number.
 * </p>
 * <p>
 * This module is a pass through<br>
 * The operators by default assumes immutable keys. If the key is mutable, use cloneKey to make a copy<br>
 * <br>
 * <b>StateFull : Yes, </b> tuple are processed in current window. <br>
 * <b>Partitions : No, </b>will yield wrong results. <br>
 * <br>
 * <br>
 * <b>Ports</b>:<br>
 * <b>data</b>: expects Map&lt;K,V extends Number&gt;<br>
 * <b>first</b>: emits HashMap&lt;K,V&gt;<br>
 * <br>
 * <b>Properties</b>:<br>
 * <b>key</b>: The key on which compare is done<br>
 * <b>value</b>: The value to compare with<br>
 * <b>cmp</b>: The compare function. Supported values are "lte", "lt", "eq", "neq", "gt", "gte". Default is "eq"<br>
 * <br>
 * <b>Specific compile time checks</b>:<br>
 * Key must be non empty<br>
 * Value must be able to convert to a "double"<br>
 * Compare string, if specified, must be one of "lte", "lt", "eq", "neq", "gt", "gte"<br>
 * <br>
 * </p>
 *
 * @displayName Emit First Match (Number)
 * @category Rules and Alerts
 * @tags filter, key value, numeric
 *
 * @since 0.3.2
 */

@OperatorAnnotation(partitionable = false)
public class FirstMatchMap<K, V extends Number> extends BaseMatchOperator<K,V>
{
  /**
   * Tuple emitted flag.
   */
  boolean emitted = false;

  /**
   * The port on which key value pairs are received.
   */
  public final transient DefaultInputPort<Map<K, V>> data = new DefaultInputPort<Map<K, V>>()
  {
    /**
     * Checks if required key,val pair exists in the HashMap. If so tuple is emitted, and emitted flag is set
     * to true
     */
    @Override
    public void process(Map<K, V> tuple)
    {
      if (emitted) {
        return;
      }
      V val = tuple.get(getKey());
      if (val == null) { // skip if key does not exist
        return;
      }
      if (compareValue(val.doubleValue())) {
        first.emit(cloneTuple(tuple));
        emitted = true;
      }
    }
  };

  /**
   * The output port on which the first satisfying key value pair is emitted.
   */
  public final transient DefaultOutputPort<HashMap<K, V>> first = new DefaultOutputPort<HashMap<K, V>>();

  /**
   * Resets emitted flag to false
   * @param windowId
   */
  @Override
  public void beginWindow(long windowId)
  {
    emitted = false;
  }
}
