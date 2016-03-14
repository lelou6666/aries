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
package org.apache.aries.util;

import java.io.IOException;

/**
 * This unchecked exception wraps an IOException.
 */
public class IORuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * @param message A detail message.
     * @param cause The original IOException.
     */
    public IORuntimeException(String message, IOException cause) {
        super(message, cause);
    }

    /**
     * @param cause The original IOException.
     */
    public IORuntimeException(IOException cause) {
        super(cause);
    }

    /**
     * @see java.lang.Throwable#getCause()
     */
    @Override
    public IOException getCause() {
        return (IOException) super.getCause();
    }
}
