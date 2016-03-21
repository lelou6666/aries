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
package org.apache.aries.subsystem.util.felix;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.aries.subsystem.obr.internal.AbstractCapability;
import org.osgi.resource.Resource;

public class OsgiContentCapability extends AbstractCapability {
	private final Map<String, Object> attributes = new HashMap<String, Object>();
	private final Resource resource;
	
	public OsgiContentCapability(Resource resource, String url) {
		// TOOD Add to constants.
		attributes.put("osgi.content", url);
		// TODO Any directives?
		this.resource = resource;
	}
	
	public OsgiContentCapability(Resource resource, URL url) {
		this(resource, url.toExternalForm());
	}

	public Map<String, Object> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	public Map<String, String> getDirectives() {
		return Collections.emptyMap();
	}

	public String getNamespace() {
		// TODO Add to constants.
		return "osgi.content";
	}

	public Resource getResource() {
		return resource;
	}
}
