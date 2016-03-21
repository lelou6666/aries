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

import java.io.Serializable;
import java.lang.reflect.Method;
<<<<<<< HEAD
import java.lang.reflect.Proxy;
=======
import java.util.ArrayDeque;
import java.util.Deque;
>>>>>>> refs/remotes/apache/trunk
import java.util.List;

import org.apache.aries.blueprint.Interceptor;
import org.apache.aries.proxy.InvocationListener;
import org.osgi.service.blueprint.reflect.ComponentMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collaborator which ensures preInvoke and postInvoke occur before and after
 * method invocation
 */
public class Collaborator implements InvocationListener, Serializable {

    /** Serial version UID for this class */
    private static final long serialVersionUID = -58189302118314469L;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Collaborator.class);

    private transient List<Interceptor> interceptors = null;
    private transient ComponentMetadata cm = null;

    public Collaborator(ComponentMetadata cm, List<Interceptor> interceptors) {
        this.cm = cm;
        this.interceptors = interceptors;
    }

    /**
     * Invoke the preCall method on the interceptor
     * 
     * @param o
     *            : The Object being invoked
     * @param m
     *            : method
     * @param parameters
     *            : method paramters
     * @throws Throwable
     */
    public Object preInvoke(Object o, Method m, Object[] parameters)
            throws Throwable {
        Deque<StackElement> stack = new ArrayDeque<StackElement>(interceptors.size());
        if (interceptors != null) {
          try{
            for (Interceptor im : interceptors) {
                Collaborator.StackElement se = new StackElement(im);

                // should we do this before or after the preCall ?
                stack.push(se);

                // allow exceptions to propagate
                se.setPreCallToken(im.preCall(cm, m, parameters));
            }
          } catch (Throwable t) {
            postInvokeExceptionalReturn(stack, o, m, t);
            throw t;
          }
        }
        return stack;
    }

    /**
     * Called when the method is called and returned normally
     */
    public void postInvoke(Object token, Object o, Method method, 
         Object returnType) throws Throwable {
        
<<<<<<< HEAD
        // Added method to unwrap from the collaborator.
        if (method.getName().equals("unwrapObject")
                && method.getDeclaringClass() == WrapperedObject.class) {
            toReturn = object;
        } else
        // Unwrap calls for equals
        if (method.getName().equals("equals")
                && method.getDeclaringClass() == Object.class) {
            // replace the wrapper with the unwrapped object, to
            // enable object identity etc to function.
            if (args[0] instanceof WrapperedObject) {
                // unwrap in the WrapperedObject case
                args[0] = ((WrapperedObject) args[0]).unwrapObject();
            } else if (AsmInterceptorWrapper.isProxyClass(args[0].getClass())
	                    || Proxy.isProxyClass(args[0].getClass())) {
                // unwrap in the asm case
                args[0] = AsmInterceptorWrapper.unwrapObject(args[0]);
            }
            toReturn = delegate.invoke(proxy, method, args);
        } else if (method.getName().equals("finalize") && method.getParameterTypes().length == 0) {
            // special case finalize, don't route through to delegate because that will get its own call
            toReturn = null;
        } else 
        // Proxy the call through to the delegate, wrapping call in 
        // interceptor invocations.
        {
            Stack<Collaborator.StackElement> calledInterceptors = new Stack<Collaborator.StackElement>();
            boolean inInvoke = false;
            try {
                preCallInterceptor(interceptors, cm, method, args,
                        calledInterceptors);
                inInvoke = true;
                toReturn = delegate.invoke(proxy, method, args);
                inInvoke = false;
                postCallInterceptorWithReturn(cm, method, toReturn,
                        calledInterceptors);

            } catch (Throwable e) {
                // log the exception e
                LOGGER.error("invoke", e);

                // if we catch an exception we decide carefully which one to
                // throw onwards
                Throwable exceptionToRethrow = null;
                // if the exception came from a precall or postcall interceptor
                // we will rethrow it
                // after we cycle through the rest of the interceptors using
                // postCallInterceptorWithException
                if (!inInvoke) {
                    exceptionToRethrow = e;
                }
                // if the exception didn't come from precall or postcall then it
                // came from invoke
                // we will rethrow this exception if it is not a runtime
                // exception
                else {
                    if (!(e instanceof RuntimeException)) {
                        exceptionToRethrow = e;
                    }
                }
=======
        Deque<StackElement> calledInterceptors =
                    (Deque<StackElement>) token;
        if(calledInterceptors != null) {
            while (!calledInterceptors.isEmpty()) {
                Collaborator.StackElement se = calledInterceptors.pop();
>>>>>>> refs/remotes/apache/trunk
                try {
                    se.interceptor.postCallWithReturn(cm, method, returnType, se
                            .getPreCallToken());
                } catch (Throwable t) {
                    LOGGER.debug("postCallInterceptorWithReturn", t);
                    // propagate this to invoke ... further interceptors will be
                    // called via the postCallInterceptorWithException method
                    throw t;
                }
            } // end while
        }
    }

    /**
     * Called when the method is called and returned with an exception
     */
    public void postInvokeExceptionalReturn(Object token, Object o, Method method,
                 Throwable exception) throws Throwable {
        Throwable tobeRethrown = null;
        Deque<StackElement> calledInterceptors =
          (Deque<StackElement>) token;
        while (!calledInterceptors.isEmpty()) {
            Collaborator.StackElement se = calledInterceptors.pop();

            try {
                se.interceptor.postCallWithException(cm, method, exception, se
                        .getPreCallToken());
            } catch (Throwable t) {
                // log the exception
                LOGGER.debug("postCallInterceptorWithException", t);
                if (tobeRethrown == null) {
                    tobeRethrown = t;
                } else {
                  LOGGER.warn("Discarding post-call with interceptor exception", t);
                }
            }

        } // end while

        if (tobeRethrown != null)
            throw tobeRethrown;
    }

    // info to store on interceptor stack during invoke
    private static class StackElement {
        private final Interceptor interceptor;
        private Object preCallToken;

        private StackElement(Interceptor i) {
            interceptor = i;
        }

        private void setPreCallToken(Object preCallToken) {
            this.preCallToken = preCallToken;
        }

        private Object getPreCallToken() {
            return preCallToken;
        }

    }
}