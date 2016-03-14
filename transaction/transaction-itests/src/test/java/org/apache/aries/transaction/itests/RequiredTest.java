/*  Licensed to the Apache Software Foundation (ASF) under one or more
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
package org.apache.aries.transaction.itests;

import javax.inject.Inject;

import org.apache.aries.transaction.test.TestBean;
import org.junit.Test;
import org.ops4j.pax.exam.util.Filter;

public class RequiredTest extends AbstractIntegrationTest {
    @Inject
    @Filter(timeout=120000, value="(tranAttribute=Required)")
    TestBean bean;

    @Test
    public void testInsertSuccesful() throws Exception {
        clientTransaction = false;
        assertInsertSuccesful();
    }
    
    @Test
    public void testInsertWithAppExceptionCommitted() throws Exception {
        assertInsertWithAppExceptionCommitted();
    }
    
    @Test
    public void testInsertWithRuntimeExceptionRolledBack() throws Exception {
        assertInsertWithRuntimeExceptionRolledBack();
    }

    @Override
    protected TestBean getBean() {
        return bean;
    }
}
