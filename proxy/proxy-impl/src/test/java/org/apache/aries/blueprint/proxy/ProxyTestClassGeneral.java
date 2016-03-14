/*
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
package org.apache.aries.blueprint.proxy;

public class ProxyTestClassGeneral extends ProxyTestClassSuper
{

  public String testMethod(String x, int y, Object z)
  {
    somePrivateMethod();
    return x;
  }

  public String testArgs(double a, short b, long c, char d, byte e, boolean f)
  {
    return Character.toString(d);
  }

  protected void testReturnVoid()
  {
  }

  int testReturnInt()
  {
    return 17;
  }

  public Integer testReturnInteger()
  {
    return Integer.valueOf(1);
  }

  private void somePrivateMethod()
  {

  }

  public boolean equals(Object o) {
    return o == this;
  }
  
  public void testException() {
    throw new RuntimeException();
  }
  
  public void testInternallyCaughtException() {
    try {
      try {
        throw new RuntimeException();
      } catch (RuntimeException re) {
        // no op
      }
    } catch (Exception e) {
      
    }
  }
}
