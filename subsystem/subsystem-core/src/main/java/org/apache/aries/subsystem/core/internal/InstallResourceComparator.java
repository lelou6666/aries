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

import java.util.Comparator;
import java.util.List;

import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.resource.Capability;
import org.osgi.resource.Resource;

public class InstallResourceComparator implements Comparator<Resource> {
	@Override
	public int compare(Resource r1, Resource r2) {
		String r1type = getResourceType(r1);
		String r2type = getResourceType(r2);
		if (r1type.equals(r2type))
			return 0;
		if (r1type.startsWith("osgi.subsystem"))
			return 1;
		return -1;
	}
	
	private String getResourceType(Resource r) {
		List<Capability> cl = r.getCapabilities(IdentityNamespace.IDENTITY_NAMESPACE);
		Capability c = cl.get(0);
		Object o = c.getAttributes().get(IdentityNamespace.CAPABILITY_TYPE_ATTRIBUTE);
		return String.valueOf(o);
	}
}
