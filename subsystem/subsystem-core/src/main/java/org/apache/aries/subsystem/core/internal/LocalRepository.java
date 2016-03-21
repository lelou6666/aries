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

import java.util.Collection;
import java.util.Map;

import org.apache.aries.subsystem.core.capabilityset.CapabilitySetRepository;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class LocalRepository implements org.apache.aries.subsystem.core.repository.Repository {
	private final CapabilitySetRepository repository;
	
	public LocalRepository(Collection<Resource> resources) {
		repository = new CapabilitySetRepository();
		addResources(resources);
	}
	
	@Override
	public Map<Requirement, Collection<Capability>> findProviders(
			Collection<? extends Requirement> requirements) {
		return repository.findProviders(requirements);
	}
	
	private void addResources(Collection<Resource> resources) {
		for (Resource resource : resources) {
			addResource(resource);
		}
	}
	
	private void addResource(Resource resource) {
		repository.addResource(resource);
		if (resource instanceof RawSubsystemResource) {
			addResources(((RawSubsystemResource)resource).getResources());
		}
	}
}
