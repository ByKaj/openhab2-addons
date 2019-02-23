package org.openhab.binding.plugwiseha.internal.handler;

import org.eclipse.smarthome.core.thing.ThingStatus;

/**
 * Interface for a listener of the gateway status
 *
 * @author Kaj Visser - Initial contribution
 *
 */
public interface GatewayStatusListener {
    /**
     * Notifies the client that the status has changed.
     *
     * @param status The new status of the account thing
     */
    public void gatewayStatusChanged(ThingStatus status);
}
