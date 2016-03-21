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
package org.apache.aries.subsystem.itests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.aries.subsystem.core.archive.Clause;
import org.apache.aries.subsystem.core.archive.RequireCapabilityHeader;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.resolver.ResolutionException;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.service.subsystem.SubsystemException;

/*
 * Contains a series of tests related to resolution.
 */
public class ResolutionTest extends SubsystemTest {
	/*
	 * Subsystem-SymbolicName: application.a.esa
	 * Subsystem-Content: bundle.a.jar
	 */
	private static final String APPLICATION_A = "application.a.esa";
	/*
	 * Subsystem-SymbolicName: application.b.esa
	 * Subsystem-Content: bundle.d.jar
	 */
	private static final String APPLICATION_B = "application.b.esa";
	/*
	 * Subsystem-SymbolicName: application.c.esa
	 * Subsystem-Content: bundle.e.jar
	 */
	private static final String APPLICATION_C = "application.c.esa";
	/*
	 * Subsystem-SymbolicName: application.d.esa
	 * Subsystem-Content: bundle.f.jar
	 */
	private static final String APPLICATION_D = "application.d.esa";
	/* Subsystem-SymbolicName: application.e.esa
	 * Subsystem-Content: bundle.g.jar
	 */
	private static final String APPLICATION_E = "application.e.esa";
	/*
	 * Bundle-SymbolicName: bundle.a.jar
	 * Require-Capability: a
	 */
	private static final String BUNDLE_A = "bundle.a.jar";
	/*
	 * Bundle-SymbolicName: bundle.b.jar
	 * Provide-Capability: a
	 * Require-Capability: b
	 */
	private static final String BUNDLE_B = "bundle.b.jar";
	/*
	 * Bundle-SymbolicName: bundle.c.jar
	 * Provide-Capability: b
	 */
	private static final String BUNDLE_C = "bundle.c.jar";
	/*
	 * Bundle-SymbolicName: bundle.d.jar
	 * Bundle-RequiredExecutionEnvironment: JavaSE-100.100
	 */
	private static final String BUNDLE_D = "bundle.d.jar";
	/*
	 * Bundle-SymbolicName: bundle.e.jar
	 * Bundle-RequiredExecutionEnvironment: J2SE-1.4, J2SE-1.5,		J2SE-1.6,JavaSE-1.7
	 */
	private static final String BUNDLE_E = "bundle.e.jar";

	/*
	 * Bundle-SymbolicName: bundle.f.jar
	 * Bundle-NativeCode: \
	 *   native.file; osname=Linux; processor=x86, \
	 *   native.file; osname=Linux; processor=x86-64, \
	 *   native.file; osname=Win32; processor=x86, \
	 *   native.file; osname=Win32; processor=x86-64, \
	 *   native.file; osname="mac os x"; processor=x86-64
	 */
	private static final String BUNDLE_F = "bundle.f.jar";

	/*
	 * Bundle-SymbolicName: bundle.f.jar
	 * Bundle-NativeCode: \
	 *   native.file; osname=noMatch; processor=noMatch
	 */
	private static final String BUNDLE_G = "bundle.g.jar";
	
	@Before
	public void createApplications() throws Exception {
		if (createdApplications) {
			return;
		};
		createBundleA();
		createBundleB();
		createBundleC();
		createBundleD();
		createBundleE();
		createBundleF();
		createBundleG();
		createApplicationA();
		createApplicationB();
		createApplicationC();
		createApplicationD();
		createApplicationE();
		createdApplications = true;
	}
	
	private static void createApplicationA() throws IOException {
		createApplicationAManifest();
		createSubsystem(APPLICATION_A, BUNDLE_A);
	}
	
	private static void createApplicationAManifest() throws IOException {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, APPLICATION_A);
		createManifest(APPLICATION_A + ".mf", attributes);
	}
	
	private static void createApplicationB() throws IOException {
		createApplicationBManifest();
		createSubsystem(APPLICATION_B, BUNDLE_D);
	}
	
	private static void createApplicationBManifest() throws IOException {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, APPLICATION_B);
		createManifest(APPLICATION_B + ".mf", attributes);
	}
	
	private static void createApplicationC() throws IOException {
		createApplicationCManifest();
		createSubsystem(APPLICATION_C, BUNDLE_E);
	}
	
	private static void createApplicationCManifest() throws IOException {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, APPLICATION_C);
		createManifest(APPLICATION_C + ".mf", attributes);
	}

	private static void createApplicationD() throws IOException {
		createApplicationDManifest();
		createSubsystem(APPLICATION_D, BUNDLE_F);
	}

	private static void createApplicationDManifest() throws IOException {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, APPLICATION_D);
		createManifest(APPLICATION_D + ".mf", attributes);
	}

	private static void createApplicationE() throws IOException {
		createApplicationEManifest();
		createSubsystem(APPLICATION_E, BUNDLE_G);
	}

	private static void createApplicationEManifest() throws IOException {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, APPLICATION_E);
		createManifest(APPLICATION_E + ".mf", attributes);
	}

	private void createBundleA() throws IOException {
		createBundle(name(BUNDLE_A), new Header(Constants.REQUIRE_CAPABILITY, "a"));
	}
	
	private void createBundleB() throws IOException {
		createBundle(name(BUNDLE_B), 
				provideCapability("a"),
				requireCapability("b"));
	}
	
	private void createBundleC() throws IOException {
		createBundle(name(BUNDLE_C), provideCapability("b"));
	}
	
	@SuppressWarnings("deprecation")
	private void createBundleD() throws IOException {
		createBundle(name(BUNDLE_D), new Header(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT, "JavaSE-100.100"));
	}
	
	@SuppressWarnings("deprecation")
	private void createBundleE() throws IOException {
		createBundle(name(BUNDLE_E), new Header(Constants.BUNDLE_REQUIREDEXECUTIONENVIRONMENT, "J2SE-1.4, J2SE-1.5,		J2SE-1.6,JavaSE-1.7"));
	}
	
	private void createBundleF() throws IOException {
		createBundle(Collections.singletonList("native.file"), name(BUNDLE_F), new Header(Constants.BUNDLE_NATIVECODE,
				"native.file; osname=Linux; processor=x86,"
				+ "native.file; osname=Linux; processor=x86-64,"
				+ "native.file; osname=Win32; processor=x86,"
				+ "native.file; osname=Win32; processor=x86-64,"
				+ "native.file; osname=\"MacOSX\"; processor=x86-64"));
	}
	private void createBundleG() throws IOException {
		createBundle(Collections.singletonList("native.file"), name(BUNDLE_G), new Header(Constants.BUNDLE_NATIVECODE,
				"native.file; osname=noMatch; processor=noMatch"));
	}

	/*
	 * Test that the right regions are used when validating capabilities.
	 * 
	 * Application A contains a content bundle requiring capability A. Bundle B
	 * provides capability A and is available as an installable resource from a
	 * repository service. Bundle B also requires capability B. Bundle C is an
	 * already installed resource in the root subsystem providing capability B.
	 * When validating capability A, the subsystem should use the root region as
	 * the from region, and its own region as the to region. When validating 
	 * capability B, the subsystem should use the root region as the from region
	 * as well as for the to region.
	 */
	@Test
	public void testContentWithNonConstituentDependencyWithNonConstituentDependency() throws Exception {
		// Register a repository service containing bundle B requiring
		// capability B and providing capability A.
		registerRepositoryService(BUNDLE_B);
		Subsystem root = getRootSubsystem();
		// Install unmanaged bundle C providing capability B as a constituent 
		// of the root subsystem.
		Bundle bundleC = installBundleFromFile(BUNDLE_C, root);
		try {
			// Install application A with content bundle A requiring
			// capability A.
			Subsystem applicationA = installSubsystemFromFile(APPLICATION_A);
			// Make sure the Require-Capability exists for capability a...
			assertHeaderExists(applicationA, Constants.REQUIRE_CAPABILITY);
			// ...but not for capability b.
			RequireCapabilityHeader header = new RequireCapabilityHeader(applicationA.getSubsystemHeaders(null).get(Constants.REQUIRE_CAPABILITY));
			assertEquals("Wrong number of clauses", 1, header.getClauses().size());
			Clause clause = header.getClauses().iterator().next();
			assertEquals("Wrong path", "a", clause.getPath());
			assertEquals("Wrong resolution directive", Constants.RESOLUTION_MANDATORY, clause.getDirective(Constants.RESOLUTION_DIRECTIVE).getValue());
			assertEquals("Wrong effective directive", Constants.EFFECTIVE_RESOLVE, clause.getDirective(Constants.EFFECTIVE_DIRECTIVE).getValue());
			try {
				// Make sure the runtime resolution works as well.
				applicationA.start();
			}
			catch (SubsystemException e) {
				fail("Application A should have started");
			}
			finally {
				stopAndUninstallSubsystemSilently(applicationA);
			}
		}
		catch (SubsystemException e) {
			fail("Application A should have installed." + e.getMessage());
		}
		finally {
			uninstallSilently(bundleC);
		}
	}
	
	/*
	 * BREE headers must be converted into osgi.ee requirements.
	 * 
	 * The subsystem should fail to resolve and install if the required
	 * execution environment is not present.
	 */
	@Test
	public void testMissingBundleRequiredExecutionEnvironment() throws Exception {
		Subsystem applicationB = null;
		try {
			applicationB = installSubsystemFromFile(APPLICATION_B);
			fail("Missing BREE should result in installation failure");
		}
		catch (Exception e) {
			e.printStackTrace();
			assertTrue("Installation failure should be due to resolution error", e.getCause() instanceof ResolutionException);
		}
		finally {
			uninstallSubsystemSilently(applicationB);
		}
	}
	
	/*
	 * BREE headers must be converted into osgi.ee requirements.
	 * 
	 * The subsystem should resolve and install if at least one of the specified
	 * execution environments is present.
	 */
	@Test
	public void testMultipleBundleRequiredExecutionEnvironments() throws Exception {
		Subsystem applicationC = null;
		try {
			applicationC = installSubsystemFromFile(APPLICATION_C);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Installation should succeed when at least one BREE is present");
		}
		finally {
			uninstallSubsystemSilently(applicationC);
		}
	}

	@Test
	public void testNativeCodeRequirement() throws Exception {
		Subsystem applicationD = null;
		try {
			applicationD = installSubsystemFromFile(APPLICATION_D);
			applicationD.start();
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Installation should succeed for Bundle-NativeCode");
		}
		finally {
			uninstallSubsystemSilently(applicationD);
		}
	}

	@Test
	public void testMissingNativeCodeRequirement() throws Exception {
		Subsystem applicationE = null;
		try {
			applicationE = installSubsystemFromFile(APPLICATION_E);
			// TODO this should fail to intsall
		} catch (SubsystemException e) {
			e.printStackTrace();
			fail("Installation should succeed for Bundle-NativeCode");
		}
		try {
			applicationE.start();
			fail("Expected to fail to install");
		}
		catch (Exception e) {
			// expected 
		}
		finally {
			uninstallSubsystemSilently(applicationE);
		}
	}
}
