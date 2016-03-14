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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.aries.proxy.FinalModifierException;
import org.apache.aries.proxy.InvocationListener;
import org.apache.aries.proxy.UnableToProxyException;
import org.apache.aries.proxy.impl.AbstractProxyManager;
import org.apache.aries.proxy.impl.AsmProxyManager;
import org.apache.aries.proxy.impl.ProxyHandler;
import org.apache.aries.proxy.impl.SingleInstanceDispatcher;
import org.apache.aries.proxy.impl.gen.ProxySubclassGenerator;
import org.apache.aries.proxy.impl.gen.ProxySubclassMethodHashSet;
import org.apache.aries.util.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * This class uses the {@link ProxySubclassGenerator} to test
 */
public class ProxySubclassGeneratorTest extends AbstractProxyTest
{
  private static final Class<?> FINAL_METHOD_CLASS = ProxyTestClassFinalMethod.class;
  private static final Class<?> FINAL_CLASS = ProxyTestClassFinal.class;
  private static final Class<?> GENERIC_CLASS = ProxyTestClassGeneric.class;
  private static final Class<?> COVARIANT_CLASS = ProxyTestClassCovariantOverride.class;
  private static ProxySubclassMethodHashSet<String> expectedMethods = new ProxySubclassMethodHashSet<String>(
      12);
  private InvocationHandler ih = null;
  Class<?> generatedProxySubclass = null;
  Object o = null;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception
  {
    ih = new FakeInvocationHandler();
    ((FakeInvocationHandler)ih).setDelegate(getTestClass().newInstance());
    generatedProxySubclass = getGeneratedSubclass();
    o = getProxyInstance(generatedProxySubclass);
  }


  /**
   * Test that the methods found declared on the generated proxy subclass are
   * the ones that we expect.
   */
  @Test
  public void testExpectedMethods() throws Exception
  {
    Class<?> superclass = getTestClass();

    do {
      Method[] declaredMethods = superclass.getDeclaredMethods();
      List<Method> listOfDeclaredMethods = new ArrayList<Method>();
      for (Method m : declaredMethods) {
        int i = m.getModifiers();
        if (Modifier.isPrivate(i) || Modifier.isFinal(i)) {
          // private or final don't get added
        } else if (!(Modifier.isPublic(i) || Modifier.isPrivate(i) || Modifier.isProtected(i))) {
          // the method is default visibility, check the package
          if (m.getDeclaringClass().getPackage().equals(getTestClass().getPackage())) {
            // default vis with same package gets added
            listOfDeclaredMethods.add(m);
          }
        } else {
          listOfDeclaredMethods.add(m);
        }
      }

      declaredMethods = listOfDeclaredMethods.toArray(new Method[] {});
      ProxySubclassMethodHashSet<String> foundMethods = new ProxySubclassMethodHashSet<String>(
          declaredMethods.length);
      foundMethods.addMethodArray(declaredMethods);
      // as we are using a set we shouldn't get duplicates
      expectedMethods.addAll(foundMethods);
      superclass = superclass.getSuperclass();
    } while (superclass != null);

    // add the getter and setter for the invocation handler to the expected
    // set
    // and the unwrapObject method
    Method[] ihMethods = new Method[] {
        generatedProxySubclass.getMethod("setInvocationHandler",
            new Class[] { InvocationHandler.class }),
        generatedProxySubclass.getMethod("getInvocationHandler", new Class[] {}) };
    expectedMethods.addMethodArray(ihMethods);

    Method[] generatedProxySubclassMethods = generatedProxySubclass.getDeclaredMethods();
    ProxySubclassMethodHashSet<String> generatedMethods = new ProxySubclassMethodHashSet<String>(
        generatedProxySubclassMethods.length);
    generatedMethods.addMethodArray(generatedProxySubclassMethods);

    // check that all the methods we have generated were expected
    for (String gen : generatedMethods) {
      assertTrue("Unexpected method: " + gen, expectedMethods.contains(gen));
    }
    // check that all the expected methods were generated
    for (String exp : expectedMethods) {
      assertTrue("Method was not generated: " + exp, generatedMethods.contains(exp));
    }
    // check the sets were the same
    assertEquals("Sets were not the same", expectedMethods, generatedMethods);

  }
  
  /**
   * Test a method marked final
   */
  @Test
  public void testFinalMethod() throws Exception
  {
    try {
      ProxySubclassGenerator.getProxySubclass(FINAL_METHOD_CLASS);
    } catch (FinalModifierException e) {
      assertFalse("Should have found final method not final class", e.isFinalClass());
    }
  }

  /**
   * Test a class marked final
   */
  @Test
  public void testFinalClass() throws Exception
  {
    try {
      ProxySubclassGenerator.getProxySubclass(FINAL_CLASS);
    } catch (FinalModifierException e) {
      assertTrue("Should have found final class", e.isFinalClass());
    }
  }

  /**
   * Test a private constructor
   */
  @Test
  public void testPrivateConstructor() throws Exception
  {
    Object o = ProxySubclassGenerator.newProxySubclassInstance(
        ProxyTestClassPrivateConstructor.class, ih);
    assertNotNull("The new instance was null", o);

  }

  /**
   * Test a generating proxy class of class with package access constructor.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testPackageAccessCtor() throws Exception  {
      Class<ProxyTestClassPackageAccessCtor> proxyClass = 
              (Class<ProxyTestClassPackageAccessCtor>) ProxySubclassGenerator.getProxySubclass(ProxyTestClassPackageAccessCtor.class);
      ProxyTestClassPackageAccessCtor proxy = (ProxyTestClassPackageAccessCtor) getProxyInstance(proxyClass); 
      assertNotNull("The new instance was null", proxy);
  }
//  /**
//   * Test object equality between real and proxy using a Collaborator
//   */
//  @Test
//  public void testObjectEquality() throws Exception
//  {
//    Object delegate = getTestClass().newInstance();
//    InvocationHandler collaborator = new Collaborator(null, null, AsmInterceptorWrapper.passThrough(delegate));
//    Object o = ProxySubclassGenerator.newProxySubclassInstance(getTestClass(), collaborator);
//    //Calling equals on the proxy with an arg of the unwrapped object should be true
//    assertTrue("The proxy object should be equal to its delegate",o.equals(delegate));
//    InvocationHandler collaborator2 = new Collaborator(null, null, AsmInterceptorWrapper.passThrough(delegate));
//    Object o2 = ProxySubclassGenerator.newProxySubclassInstance(getTestClass(), collaborator2);
//    //The proxy of a delegate should equal another proxy of the same delegate
//    assertTrue("The proxy object should be equal to another proxy instance of the same delegate", o2.equals(o));
//  }
//  
//  private static class ProxyTestOverridesFinalize {
//      public boolean finalizeCalled = false;
//      
//      @Override
//      protected void finalize() {
//          finalizeCalled = true;
//      }
//  }
//  
//  @Test
//  public void testFinalizeNotCalled() throws Exception {
//      ProxyTestOverridesFinalize testObj = new ProxyTestOverridesFinalize();
//      InvocationHandler ih = new Collaborator(null, null, AsmInterceptorWrapper.passThrough(testObj));
//      Object o = ProxySubclassGenerator.newProxySubclassInstance(ProxyTestOverridesFinalize.class, ih);
//      
//      Method m = o.getClass().getDeclaredMethod("finalize");
//      m.setAccessible(true);
//      m.invoke(o);
//      
//      assertFalse(testObj.finalizeCalled);
//  }
  

  /**
   * Test a covariant override method
   */
  @Test
  public void testCovariant() throws Exception
  {
    ((FakeInvocationHandler)ih).setDelegate(COVARIANT_CLASS.newInstance());
    o = ProxySubclassGenerator.newProxySubclassInstance(COVARIANT_CLASS, ih);
    generatedProxySubclass = o.getClass();
    Method m = generatedProxySubclass.getDeclaredMethod("getCovariant", new Class[] {});
    Object returned = m.invoke(o);
    assertTrue("Object was of wrong type: " + returned.getClass().getSimpleName(), COVARIANT_CLASS
        .isInstance(returned));
  }
  
  /**
   * Test a covariant override method
   */
  @Test
  public void testGenerics() throws Exception
  {
    ((FakeInvocationHandler)ih).setDelegate(GENERIC_CLASS.newInstance());
    super.testGenerics();
  }

  @Test
  public void testClassLoaders() throws Exception {
    ClassLoader clA = new LimitedClassLoader("org.apache.aries.proxy.test.a", null, null);
    ClassLoader clB = new LimitedClassLoader("org.apache.aries.proxy.test.b", "org.apache.aries.proxy.test.a", clA);
    ClassLoader clC = new LimitedClassLoader("org.apache.aries.proxy.test.c", "org.apache.aries.proxy.test.b", clB);

    Class<?> clazzA = clA.loadClass("org.apache.aries.proxy.test.a.ProxyTestClassA");
    Class<?> clazzB = clB.loadClass("org.apache.aries.proxy.test.b.ProxyTestClassB");
    Class<?> clazzC = clC.loadClass("org.apache.aries.proxy.test.c.ProxyTestClassC");

    final Object object = clazzC.getConstructor(String.class).newInstance("hello");

    o = new AsmProxyManager().createNewProxy(null, Arrays.asList(clazzA, clazzB, clazzC), constantly(object), null);
    generatedProxySubclass = o.getClass();
    Method m = generatedProxySubclass.getDeclaredMethod("hello", new Class[] {});
    Object returned = m.invoke(o);
    assertEquals("hello", returned);
  }

  private static class LimitedClassLoader extends ClassLoader {
    Set<String> providedPackages;
    Set<String> importedPackages;
    List<ClassLoader> parents;

    public LimitedClassLoader(String provided, String imported, ClassLoader parent) {
      providedPackages = Collections.singleton(provided);
      importedPackages = imported != null ? Collections.singleton(imported) : Collections.<String>emptySet();
      parents = parent != null ? Collections.singletonList(parent) : Collections.<ClassLoader>emptyList();
    }
    
    final Map<String, Object> clLocks = new HashMap<String, Object>();
    protected synchronized Object getClassLoadingLock (String name) {
    	if (!clLocks.containsKey(name)) { 
    		clLocks.put(name, new Object());
    	}
    	return clLocks.get(name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      synchronized (getClassLoadingLock(name)) {
        // First, check if the class has already been loaded
        Class<?> c = findLoadedClass(name);
        if (c == null) {
          String pkg = name.substring(0, name.lastIndexOf('.'));
          if (pkg.startsWith("java.") || pkg.startsWith("sun.reflect")) {
            return getClass().getClassLoader().loadClass(name);
          } else if (providedPackages.contains(pkg)) {
            c = findClass(name);
          } else if (importedPackages.contains(pkg)) {
            for (ClassLoader cl : parents) {
              try {
                c = cl.loadClass(name);
              } catch (ClassNotFoundException e) {
                // Ignore
              }
            }
          }
        }
        if (c == null) {
          throw new ClassNotFoundException(name);
        }
        if (resolve) {
          resolveClass(c);
        }
        return c;
      }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
      String pkg = name.substring(0, name.lastIndexOf('.'));
      if (getPackage(pkg) == null) {
        definePackage(pkg, null, null, null, null, null, null, null);
      }
      String path = name.replace('.', '/').concat(".class");
      InputStream is = LimitedClassLoader.class.getClassLoader().getResourceAsStream(path);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        IOUtils.copy(is, baos);
      } catch (IOException e) {
        throw new ClassNotFoundException(name, e);
      }
      byte[] buf = baos.toByteArray();
      return defineClass(name, buf, 0, buf.length);
    }
  }
  
  private Class<?> getGeneratedSubclass() throws Exception
  {
    return getProxyClass(getTestClass());
  }

  private class FakeInvocationHandler implements InvocationHandler
  {
    private Object delegate = null;
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
     * java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
      try {
      Object result = (delegate instanceof Callable) ? 
          method.invoke(((Callable<?>)delegate).call(), args) : 
          method.invoke(delegate, args) ;
      return result;
      } catch (InvocationTargetException ite) {
        throw ite.getTargetException();
      }
    }

    void setDelegate(Object delegate){
      this.delegate = delegate;
    }
    
  }

  @Override
  protected Object getProxyInstance(Class<?> proxyClass) {
    return getProxyInstance(proxyClass, ih);
  }
  
  private Object getProxyInstance(Class<?> proxyClass, InvocationHandler ih) {
    try {
      if(proxyClass.equals(ProxyTestClassChildOfAbstract.class))
        return proxyClass.newInstance();
      
      Object proxyInstance = proxyClass.getConstructor().newInstance();
      Method setIH = proxyInstance.getClass().getMethod("setInvocationHandler", InvocationHandler.class);
      setIH.invoke(proxyInstance, ih);
      return proxyInstance;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  protected Class<?> getProxyClass(Class<?> clazz) {
    try {
      return ProxySubclassGenerator.getProxySubclass(clazz);
    } catch (UnableToProxyException e) {
      return null;
    }
  }


  @Override
  protected Object setDelegate(Object proxy, Callable<Object> dispatcher) {
    AbstractProxyManager apm = new AsmProxyManager();
    return getProxyInstance(proxy.getClass(), new ProxyHandler(apm, dispatcher, null));  
  }


  @Override
  protected Object getProxyInstance(Class<?> proxyClass,
      InvocationListener listener) {
    AbstractProxyManager apm = new AsmProxyManager();
    return getProxyInstance(proxyClass, new ProxyHandler(apm, new SingleInstanceDispatcher(getProxyInstance(proxyClass)), listener));  
  }


  @Override
  protected Object getP3() {
    return new ProxyTestClassGeneral();
  }

  private Callable<Object> constantly(final Object result) {
    return new Callable<Object>() {
      public Object call() throws Exception {
        return result;
      }
    };
  }
}
