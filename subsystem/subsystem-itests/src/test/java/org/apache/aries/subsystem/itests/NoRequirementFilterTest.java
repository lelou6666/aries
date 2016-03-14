/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.aries.subsystem.itests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;

/*
 * Generic requirements are not required to specify the filter directive, in
 * which case it would match any capability from the same namespace.
 * 
 * Generic capabilities are not required to use the namespace as an attribute.
 */
public class NoRequirementFilterTest extends SubsystemTest {
	/*
	 * Subsystem-SymbolicName: application.a.esa
	 * Subsystem-Content: bundle.a.jar
	 */
	private static final String APPLICATION_A = "application.a.esa";
	/*
	 * Bundle-SymbolicName: bundle.a.jar
	 * Require-Capability: y
	 */
	private static final String BUNDLE_A = "bundle.a.jar";
	/*
	 * Bundle-SymbolicName: bundle.b.jar
	 * Provide-Capability: y
	 */
	private static final String BUNDLE_B = "bundle.b.jar";
	
	private static void createApplicationA() throws IOException {
		createApplicationAManifest();
		createSubsystem(APPLICATION_A, BUNDLE_A);
	}
	
	private static void createApplicationAManifest() throws IOException {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, APPLICATION_A);
		attributes.put(SubsystemConstants.SUBSYSTEM_CONTENT, BUNDLE_A);
		createManifest(APPLICATION_A + ".mf", attributes);
	}
	
	private void createBundleA() throws IOException {
		createBundle(name(BUNDLE_A), new Header(Constants.REQUIRE_CAPABILITY, "y"));
	}
	
	private void createBundleB() throws IOException {
		createBundle(name(BUNDLE_B), new Header(Constants.PROVIDE_CAPABILITY, "y"));
	}
	
	@Override
	protected void createApplications() throws Exception {
		createBundleA();
		createBundleB();
		createApplicationA();
	}
	
	public void setUp() throws Exception {
		super.setUp();
		registerRepositoryService(BUNDLE_A);
	}
	
	@Test
	public void testNoFilterDirectiveWithNoNamespaceAttribute() throws Exception {
		Bundle bundleB = installBundleFromFile(BUNDLE_B);
		try {
			Subsystem subsystem = installSubsystemFromFile(APPLICATION_A);
			try {
				startSubsystem(subsystem);
				stopSubsystem(subsystem);
			}
			finally {
				uninstallSubsystemSilently(subsystem);
			}
		}
		finally {
			uninstallSilently(bundleB);
		}
	}
}
