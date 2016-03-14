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
package org.apache.aries.versioning.utils;

import java.lang.reflect.Modifier;


public class MethodDeclaration extends GenericDeclaration {
    private final String desc;

    MethodDeclaration(int access, String name, String desc, String signature, String[] exceptions) {
        super(access, name, signature);
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(getAccess());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        //int result = super.hashCode();
        int result = prime + ((desc == null) ? 0 : desc.hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        //if (getClass() != obj.getClass()) return false;
        MethodDeclaration other = (MethodDeclaration) obj;
        if (desc == null) {
            if (other.desc != null) return false;
        } else if (!desc.equals(other.desc)) return false;
        if (getName() == null) {
            if (other.getName() != null) return false;
        } else if (!getName().equals(other.getName())) return false;

        return true;
    }

}
