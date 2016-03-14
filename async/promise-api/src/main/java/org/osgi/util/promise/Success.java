/*
 * Copyright (c) OSGi Alliance 2015. All Rights Reserved.
 *
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
package org.osgi.util.promise;

/**
 * Success callback for a Promise.
 * <p>
 * A Success callback is registered with a Promise using the Promise.then(Success) method and is called if the Promise is resolved successfully.
 * <p>
 * This is a functional interface and can be used as the assignment target for a lambda expression or method reference.
 * <p>
 * @param <T> The value type of the resolved Promise passed as input to this callback.
 * @param <R> The value type of the returned Promise from this callback.
 */
@org.osgi.annotation.versioning.ConsumerType
public interface Success<T,R> {
    /**
     * Success callback for a Promise.
     * <p>
     * This method is called if the Promise with which it is registered resolves successfully.
     * <p>
     * In the remainder of this description we will refer to the Promise returned by this method as the returned Promise and the Promise returned by Promise.then(Success) when this Success callback was registered as the chained Promise.
     * <p>
     * If the returned Promise is null then the chained Promise will resolve immediately with a successful value of null. If the returned Promise is not null then the chained Promise will be resolved when the returned Promise is resolved.
     *
     * @param resolved The successfully resolved Promise.
     * @return The Promise to use to resolve the chained Promise, or null if the chained Promise is to be resolved immediately with the value null.
     * @throws Exception The chained Promise will be failed with the thrown exception.
     */
    Promise<R> call(Promise<T> resolved) throws Exception;
}
