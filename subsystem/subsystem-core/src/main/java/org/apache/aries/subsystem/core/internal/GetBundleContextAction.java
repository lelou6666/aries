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
package org.apache.aries.subsystem.core.internal;

import java.security.PrivilegedAction;
import java.util.EnumSet;

import org.eclipse.equinox.region.Region;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.subsystem.Subsystem.State;

public class GetBundleContextAction implements PrivilegedAction<BundleContext> {
	private final BasicSubsystem subsystem;
	
	public GetBundleContextAction(BasicSubsystem subsystem) {
		this.subsystem = subsystem;
	}
	
	@Override
	public BundleContext run() {
		if (EnumSet.of(State.INSTALL_FAILED, State.UNINSTALLED).contains(
				subsystem.getState()))
			return null;
		BasicSubsystem subsystem = Utils.findScopedSubsystemInRegion(this.subsystem);
		Region region = subsystem.getRegion();
		String bundleName = RegionContextBundleHelper.SYMBOLICNAME_PREFIX + subsystem.getSubsystemId();
		Bundle bundle = region.getBundle(bundleName, RegionContextBundleHelper.VERSION);
		return bundle.getBundleContext();
	}
}
