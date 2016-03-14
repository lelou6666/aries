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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.felix.bundlerepository.Property;
import org.osgi.framework.Version;

public class FelixProperty implements Property {
	private static Set<?> asSet(List<?> list) {
		return new HashSet<Object>(list);
	}
	
	private final String name;
	private final Object value;
	
	public FelixProperty(String name, Object value) {
		if (name == null)
			throw new NullPointerException("Missing required parameter: name");
		if (value == null)
			throw new NullPointerException("Missing required parameter: value");
		this.name = name;
		this.value = value;
	}
	
	public FelixProperty(Map.Entry<String, Object> entry) {
		this(entry.getKey(), entry.getValue());
	}

	public Object getConvertedValue() {
		if (value instanceof List)
			return asSet((List<?>)value);
		return value;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		if (value instanceof Version)
			return Property.VERSION;
		if (value instanceof Long)
			return Property.LONG;
		if (value instanceof Double)
			return Property.DOUBLE;
		if (value instanceof List<?>)
			return Property.SET;
		return null;
	}

	public String getValue() {
		return String.valueOf(value);
	}

}
