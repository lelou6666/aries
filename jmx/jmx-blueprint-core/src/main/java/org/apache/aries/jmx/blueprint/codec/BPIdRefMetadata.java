/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.jmx.blueprint.codec;

import java.util.HashMap;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.OpenDataException;

import org.apache.aries.jmx.blueprint.BlueprintMetadataMBean;
import org.osgi.service.blueprint.reflect.IdRefMetadata;

public class BPIdRefMetadata implements BPNonNullMetadata {
    private String componentId;

    public BPIdRefMetadata(CompositeData idRef) {
        componentId = (String) idRef.get(BlueprintMetadataMBean.COMPONENT_ID);
    }

    public BPIdRefMetadata(IdRefMetadata idRef) {
        componentId = idRef.getComponentId();
    }

    public CompositeData asCompositeData() {
        HashMap<String, Object> items = new HashMap<String, Object>();
        items.put(BlueprintMetadataMBean.COMPONENT_ID, componentId);

        try {
            return new CompositeDataSupport(BlueprintMetadataMBean.ID_REF_METADATA_TYPE, items);
        } catch (OpenDataException e) {
            throw new RuntimeException(e);
        }
    }

    public String getComponentId() {
        return componentId;
    }
}
