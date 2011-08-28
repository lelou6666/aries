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
package org.apache.aries.subsystem.core.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;

import org.apache.aries.util.manifest.ManifestProcessor;

public abstract class Manifest {
	protected final Map<String, Header> headers = new HashMap<String, Header>();
	protected final java.util.jar.Manifest manifest;
	
	public Manifest(InputStream in) throws IOException {
		this(ManifestProcessor.parseManifest(in));
	}
	
	public Manifest(java.util.jar.Manifest manifest) {
		this.manifest = manifest;
		for (Map.Entry<Object, Object> entry : manifest.getMainAttributes().entrySet()) {
			Header header = HeaderFactory.createHeader(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
			headers.put(header.getName(), header);
		}
		if (headers.get(ManifestVersionHeader.NAME) == null)
			headers.put(ManifestVersionHeader.NAME, ManifestVersionHeader.DEFAULT);
	}
	
	public Manifest(File manifestFile) throws IOException {
		this(new FileInputStream(manifestFile));
	}
	
	protected Manifest() {
		manifest = null;
	}

	public Header getHeader(String name) {
		return headers.get(name);
	}

	public Collection<Header> getHeaders() {
		return Collections.unmodifiableCollection(headers.values());
	}

	public Header getManifestVersion() {
		return getHeader(Attributes.Name.MANIFEST_VERSION.toString());
	}
	
	public void write(OutputStream out) throws IOException {
		java.util.jar.Manifest m = new java.util.jar.Manifest();
		Attributes attributes = m.getMainAttributes();
		for (Header header : headers.values()) {
			attributes.putValue(header.getName(), header.getValue());
		}
		m.write(out);
	}
}
