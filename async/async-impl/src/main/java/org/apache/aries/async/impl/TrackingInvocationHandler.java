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
package org.apache.aries.async.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

class TrackingInvocationHandler implements InvocationHandler, net.sf.cglib.proxy.InvocationHandler {

	private static final Map<Class<?>, Object> RETURN_VALUES;
	
	static {
		Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();
		
		map.put(boolean.class, Boolean.FALSE);
		map.put(byte.class, Byte.valueOf((byte)0));
		map.put(short.class, Short.valueOf((short)0));
		map.put(char.class, Character.valueOf((char)0));
		map.put(int.class, Integer.valueOf(0));
		map.put(float.class, Float.valueOf(0));
		map.put(long.class, Long.valueOf(0));
		map.put(double.class, Double.valueOf(0));
		
		RETURN_VALUES = Collections.unmodifiableMap(map);
	}
	
	/**
	 * 
	 */
	private final AsyncService asyncService;
	private final ServiceTracker<LogService, LogService> logServiceTracker;
	private final Bundle clientBundle;
	private final ServiceReference<?> ref;
	private final Object delegate;
	
	public TrackingInvocationHandler(AsyncService asyncService, 
			Bundle clientBundle, ServiceTracker<LogService, LogService> logServiceTracker, 
			ServiceReference<?> ref) {
		this.asyncService = asyncService;
		this.logServiceTracker = logServiceTracker;
		this.clientBundle = clientBundle;
		this.ref = ref;
		this.delegate = null;
	}
	public TrackingInvocationHandler(AsyncService asyncService, 
			Bundle clientBundle,ServiceTracker<LogService, LogService> logServiceTracker, 
			Object service) {
		this.asyncService = asyncService;
		this.logServiceTracker = logServiceTracker;
		this.clientBundle = clientBundle;
		this.delegate = service;
		this.ref = null;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		asyncService.registerInvocation(new MethodCall(clientBundle, logServiceTracker, 
				ref, delegate, method, args));
		Class<?> returnType = method.getReturnType();
		return RETURN_VALUES.get(returnType);
	}
	
}
