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

public class NeverTest extends AbstractIntegrationTest {
    @Inject
    @Filter("(tranAttribute=Never)")
    TestBean bean;

    /**
     * Test with client transaction - an exception is thrown because transactions are not allowed
     * @throws Exception
     */
    @Test
    public void testInsertFails() throws Exception {
        clientTransaction = true;
        assertInsertFails();
    }
    
    @Test
    public void testDelegateInsertFails() throws Exception {
        clientTransaction = false;
        assertDelegateInsertFails();
    }

    @Override
    protected TestBean getBean() {
        return bean;
    }

}
