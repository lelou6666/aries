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
package org.apache.aries.jndi;

import java.util.Arrays;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class DirObjectFactoryHelper extends ObjectFactoryHelper implements DirObjectFactory {

    public DirObjectFactoryHelper(BundleContext defaultContext, BundleContext callerContext) {
        super(defaultContext, callerContext);
    }
    
    public Object getObjectInstance(Object obj,
                                    Name name,
                                    Context nameCtx,
                                    Hashtable<?, ?> environment,
                                    Attributes attrs) throws Exception {

        // Step 1
        if (obj instanceof Referenceable) {
            obj = ((Referenceable) obj).getReference();
        }

        Object result = obj;

        // Step 2
        if (obj instanceof Reference) {
            Reference ref = (Reference) obj;
            String className = ref.getFactoryClassName();

            if (className != null) {
                // Step 3
                result = getObjectInstanceUsingClassName(obj, className, obj, name, nameCtx, environment, attrs);
            }
        }

        // Step 4
        if (result == null || result == obj) {
            result = getObjectInstanceUsingObjectFactoryBuilders(obj, name, nameCtx, environment, attrs);
        }
        
        // Step 5
        if (result == null || result == obj) {                
            if ((obj instanceof Reference && ((Reference) obj).getFactoryClassName() == null) ||
                !(obj instanceof Reference)) {
                result = getObjectInstanceUsingObjectFactories(obj, name, nameCtx, environment, attrs);
            }
        }

<<<<<<< HEAD
=======
        // Extra, non-standard, bonus step. If javax.naming.OBJECT_FACTORIES is set as 
        // a property in the environment, use its value to construct additional object factories. 
        // Added under Aries-822, with reference 
        // to https://www.osgi.org/bugzilla/show_bug.cgi?id=138 
        if (result == null || result == obj) {
            result = getObjectInstanceViaContextDotObjectFactories(obj, name, nameCtx, environment, attrs);
        } 
        
>>>>>>> refs/remotes/apache/trunk
        return (result == null) ? obj : result;
    }

    private Object getObjectInstanceUsingObjectFactories(Object obj,
                                                         Name name,
                                                         Context nameCtx,
                                                         Hashtable<?, ?> environment,
                                                         Attributes attrs) 
        throws Exception {
<<<<<<< HEAD
    	
        Object result = null;
        ServiceReference[] refs = Utils.getReferencesPrivileged(callerContext, DirObjectFactory.class);
        if (refs != null) {
        	Arrays.sort(refs, Utils.SERVICE_REFERENCE_COMPARATOR);
        	for (ServiceReference ref : refs) {
        		DirObjectFactory factory = (DirObjectFactory) Utils.getServicePrivileged(callerContext, ref);

        		try {
        			result = factory.getObjectInstance(obj, name, nameCtx, environment, attrs);
        		} finally {
        			callerContext.ungetService(ref);
        		}

        		// if the result comes back and is not null and not the reference
        		// object then we should return the result, so break out of the
        		// loop we are in.
        		if (result != null && result != obj) {
        			break;
        		}
        	}
=======
        
        Object result = null;
        ServiceReference[] refs = Utils.getReferencesPrivileged(callerContext, DirObjectFactory.class);
        if (refs != null) {
            Arrays.sort(refs, Utils.SERVICE_REFERENCE_COMPARATOR);
            for (ServiceReference ref : refs) {
              
                if (canCallObjectFactory(obj, ref)) {
                    DirObjectFactory factory = (DirObjectFactory) Utils.getServicePrivileged(callerContext, ref);
    
                    try {
                        result = factory.getObjectInstance(obj, name, nameCtx, environment, attrs);
                    } finally {
                        callerContext.ungetService(ref);
                    }
    
                    // if the result comes back and is not null and not the reference
                    // object then we should return the result, so break out of the
                    // loop we are in.
                    if (result != null && result != obj) {
                        break;
                    }
                }
            }
>>>>>>> refs/remotes/apache/trunk
        }

        if (result == null) {
            result = getObjectInstanceUsingObjectFactories(obj, name, nameCtx, environment);
        }
        
        return (result == null) ? obj : result;
    }

<<<<<<< HEAD
=======
    private boolean canCallObjectFactory(Object obj, ServiceReference ref)
    {
      if (obj instanceof Reference) return true;
      
      Object prop = ref.getProperty("aries.object.factory.requires.reference");
      
      if (prop == null) return true;
      
      if (prop instanceof Boolean) return !!!(Boolean) prop; // if set to true we don't call.
      
      return true;
    }

>>>>>>> refs/remotes/apache/trunk
    private Object getObjectInstanceUsingClassName(Object reference,
                                                   String className,
                                                   Object obj,
                                                   Name name,
                                                   Context nameCtx,
                                                   Hashtable<?, ?> environment,
                                                   Attributes attrs)
        throws Exception {

        Tuple<ServiceReference, ObjectFactory> tuple = ObjectFactoryHelper.findObjectFactoryByClassName(defaultContext, className);
        Object result = null;
        
        if (tuple.second != null) {
<<<<<<< HEAD
        	try {
        		result = ((DirObjectFactory) tuple.second).getObjectInstance(reference, name, nameCtx, environment, attrs);
=======
            try {
                result = ((DirObjectFactory) tuple.second).getObjectInstance(reference, name, nameCtx, environment, attrs);
>>>>>>> refs/remotes/apache/trunk
            } finally {
                defaultContext.ungetService(tuple.first);
            }
        }

        return (result == null) ? obj : result;
    }
  
    private Object getObjectInstanceUsingObjectFactoryBuilders(Object obj,
                                                               Name name,
                                                               Context nameCtx,
                                                               Hashtable<?, ?> environment,
                                                               Attributes attrs) 
        throws Exception {
        ObjectFactory factory = null;
        ServiceReference[] refs = Utils.getReferencesPrivileged(callerContext, ObjectFactoryBuilder.class);
        if (refs != null) {
<<<<<<< HEAD
        	Arrays.sort(refs, Utils.SERVICE_REFERENCE_COMPARATOR);
        	for (ServiceReference ref : refs) {
        		ObjectFactoryBuilder builder = (ObjectFactoryBuilder) Utils.getServicePrivileged(callerContext, ref);
        		try {
        			factory = builder.createObjectFactory(obj, environment);
        		} catch (NamingException e) {
        			// TODO: log it
        		} finally {
        			callerContext.ungetService(ref);
        		}
        		if (factory != null) {
        			break;
        		}
        	}
=======
            Arrays.sort(refs, Utils.SERVICE_REFERENCE_COMPARATOR);
            for (ServiceReference ref : refs) {
                ObjectFactoryBuilder builder = (ObjectFactoryBuilder) Utils.getServicePrivileged(callerContext, ref);
                try {
                    factory = builder.createObjectFactory(obj, environment);
                } catch (NamingException e) {
                    // TODO: log it
                } finally {
                    callerContext.ungetService(ref);
                }
                if (factory != null) {
                    break;
                }
            }
>>>>>>> refs/remotes/apache/trunk
        }

        Object result = null;
        
        if (factory != null) {
            if (factory instanceof DirObjectFactory) {       
                result = ((DirObjectFactory) factory).getObjectInstance(obj, name, nameCtx, environment, attrs);
            } else {
                result = factory.getObjectInstance(obj, name, nameCtx, environment);
            }
        }
        
        return (result == null) ? obj : result;
    }

}
