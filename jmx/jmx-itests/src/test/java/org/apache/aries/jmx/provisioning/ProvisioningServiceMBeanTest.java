/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.aries.jmx.provisioning;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.osgi.service.provisioning.ProvisioningService.PROVISIONING_AGENT_CONFIG;
import static org.osgi.service.provisioning.ProvisioningService.PROVISIONING_REFERENCE;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.inject.Inject;
import javax.management.openmbean.TabularData;

import org.apache.aries.jmx.AbstractIntegrationTest;
import org.apache.aries.jmx.codec.PropertyData;
import org.junit.Ignore;
import org.junit.Test;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.osgi.jmx.JmxConstants;
import org.osgi.jmx.service.provisioning.ProvisioningServiceMBean;
import org.osgi.service.provisioning.ProvisioningService;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class ProvisioningServiceMBeanTest extends AbstractIntegrationTest {
	@Inject
	ProvisioningService ps;

    @Configuration
    public Option[] configuration() {
		return options(
				jmxRuntime()
				);
    }

    @Ignore("For now.. Cannot find public repo for org.eclipse.equinox.ip")
    @Test
    @SuppressWarnings({ "unchecked"})
    public void testMBeanInterface() throws Exception {
        ProvisioningServiceMBean mbean = getMBean(ProvisioningServiceMBean.OBJECTNAME, ProvisioningServiceMBean.class);
        Dictionary<String, Object> info;
        
        // add information URL (create temp zip file)
        File provZip = createProvAgentConfigZip();
        mbean.addInformationFromZip(provZip.toURI().toURL().toExternalForm());
        
        //check the info has been added
        info = ps.getInformation();
        assertNotNull(info);
        assertTrue(info.size() >= 1);
        assertProvAgentConfigCorrect(info);
        
        // test list information
        TabularData data = mbean.listInformation();
        assertNotNull(data);
        assertEquals(JmxConstants.PROPERTIES_TYPE, data.getTabularType());
        assertTrue(data.values().size() >= 1);
        PropertyData<byte[]> configEntry = PropertyData.from(data.get(new Object[] {PROVISIONING_AGENT_CONFIG }));
        assertNotNull(configEntry);
        assertArrayEquals(new byte[] { 10, 20, 30 }, configEntry.getValue());
        
        // test add information
        PropertyData<String> reference = PropertyData.newInstance(PROVISIONING_REFERENCE, "rsh://0.0.0.0/provX");
        data.put(reference.toCompositeData());
        mbean.addInformation(data);
        info = ps.getInformation();
        assertNotNull(info);
        assertTrue(info.size() >= 2);
        assertProvAgentConfigCorrect(info);
        String ref = (String) info.get(PROVISIONING_REFERENCE);
        assertNotNull(ref);
        assertEquals("rsh://0.0.0.0/provX", ref);
        
        // test set information
        data.clear();
        PropertyData<String> newRef = PropertyData.newInstance(PROVISIONING_REFERENCE, "rsh://0.0.0.0/newProvRef");
        data.put(newRef.toCompositeData());
        mbean.setInformation(data);
        info = ps.getInformation();
        assertNotNull(info);
        assertTrue(info.size() >= 1);
        assertNull(info.get(PROVISIONING_AGENT_CONFIG));
        ref = (String) info.get(PROVISIONING_REFERENCE);
        assertNotNull(ref);
        assertEquals("rsh://0.0.0.0/newProvRef", ref);
        
    }

	private void assertProvAgentConfigCorrect(Dictionary<String, Object> info) {
		byte[] config = (byte[]) info.get(PROVISIONING_AGENT_CONFIG);
        assertNotNull(config);
        assertArrayEquals(new byte[] { 10, 20, 30 }, config);
	}

	private File createProvAgentConfigZip() throws IOException, FileNotFoundException {
		File  provZip = File.createTempFile("Prov-jmx-itests", ".zip");
        Manifest man = new Manifest();
        man.getMainAttributes().putValue("Manifest-Version", "1.0");
        man.getMainAttributes().putValue("Content-Type", "application/zip");
        JarOutputStream jout = new JarOutputStream(new FileOutputStream(provZip), man);
        ZipEntry entry = new ZipEntry(PROVISIONING_AGENT_CONFIG);
        jout.putNextEntry( entry );
        jout.write(new byte[] { 10, 20, 30 });
        jout.closeEntry();
        jout.flush();
        jout.close();
        
        provZip.deleteOnExit();
		return provZip;
	}
}
