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
package org.apache.aries.subsystem.core.internal;

import java.security.PrivilegedAction;

import org.osgi.service.subsystem.Subsystem.State;
import org.osgi.service.subsystem.SubsystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAction implements PrivilegedAction<Object> {
	private static final Logger logger = LoggerFactory.getLogger(AbstractAction.class);
	
	protected final boolean disableRootCheck;
	protected final BasicSubsystem requestor;
	protected final BasicSubsystem target;
	
	public AbstractAction(BasicSubsystem requestor, BasicSubsystem target, boolean disableRootCheck) {
		this.requestor = requestor;
		this.target = target;
		this.disableRootCheck = disableRootCheck;
	}
	
	protected void checkRoot() {
		if (!disableRootCheck && target.isRoot())
			throw new SubsystemException("This operation may not be performed on the root subsystem");
	}
	
	protected void checkValid() {
		BasicSubsystem s = (BasicSubsystem)Activator.getInstance().getSubsystemServiceRegistrar().getSubsystemService(target);
		if (s != target)
			throw new IllegalStateException("Detected stale subsystem instance: " + s);
	}
	
	protected void waitForStateChange(State fromState) {
		long then = System.currentTimeMillis() + 60000;
		synchronized (target) {
			if (logger.isDebugEnabled())
				logger.debug("Request to wait for state change of subsystem {} from state {}", target.getSymbolicName(), target.getState());
			while (target.getState().equals(fromState)) {
				if (logger.isDebugEnabled())
					logger.debug("{} equals {}", target.getState(), fromState);
				// State change has not occurred.
				long now = System.currentTimeMillis();
				if (then <= now)
					// Wait time has expired.
					throw new SubsystemException("Operation timed out while waiting for the subsystem to change state from " + fromState);
				try {
					if (logger.isDebugEnabled())
						logger.debug("Waiting for {} ms", then - now);
					// Wait will never be called with zero or a negative
					// argument due to previous check.
					target.wait(then - now);
				}
				catch (InterruptedException e) {
					// Reset the interrupted flag.
					Thread.currentThread().interrupt();
					throw new SubsystemException(e);
				}
			}
			if (logger.isDebugEnabled())
				logger.debug("Done waiting for subsystem {} in state {} to change from state {}", new Object[]{target.getSymbolicName(), target.getState(), fromState});
		}
	}
}
