/**
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
package org.apache.aries.spifly;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/** Contains information needed for the byte code weaver.
 */
public class WeavingData {
    private final String className;
    private final String methodName;
    private final String[] argClasses;
    private final Set<ConsumerRestriction> argRestrictions;
    private final List<BundleDescriptor> allowedBundles;

    /**
     * Constructor.
     * @param className The class name of the call that needs to be woven.
     * @param methodName The method name of the call that needs to be woven.
     * @param argClasses The overload (class names of the signature) of the call
     * that needs to be woven. If <code>null</code> then all overloads of the method
     * need to be woven.
     * @param argRestrictions
     * @param allowedBundles
     */
    public WeavingData(String className, String methodName, String[] argClasses, Set<ConsumerRestriction> argRestrictions, List<BundleDescriptor> allowedBundles) {
        // TODO can we infer argClasses from restrictions?
        this.className = className;
        this.methodName = methodName;
        this.argClasses = argClasses;
        this.argRestrictions = argRestrictions;
        this.allowedBundles = allowedBundles;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<BundleDescriptor> getAllowedBundles() {
        return allowedBundles;
    }

    public String[] getArgClasses() {
        return argClasses;
    }

    public Set<ConsumerRestriction> getArgRestrictions() {
        return argRestrictions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(argClasses);
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
        result = prime * result + ((argRestrictions == null) ? 0 : argRestrictions.hashCode());
        result = prime * result + ((allowedBundles == null) ? 0 : allowedBundles.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        WeavingData other = (WeavingData) obj;
        if (!Arrays.equals(argClasses, other.argClasses))
            return false;

        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;

        if (methodName == null) {
            if (other.methodName != null)
                return false;
        } else if (!methodName.equals(other.methodName))
            return false;

        if (argRestrictions == null) {
            if (other.argRestrictions != null)
                return false;
        } else if (!argRestrictions.equals(other.argRestrictions))
            return false;

        if (allowedBundles == null) {
            if (other.allowedBundles != null)
                return false;
        } else if (!allowedBundles.equals(other.allowedBundles))
            return false;

        return true;
    }
}
