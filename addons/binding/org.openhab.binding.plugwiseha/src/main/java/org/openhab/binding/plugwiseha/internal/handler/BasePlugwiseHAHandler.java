package org.openhab.binding.plugwiseha.internal.handler;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.openhab.binding.plugwiseha.internal.api.models.response.Locations;
import org.openhab.binding.plugwiseha.internal.configuration.PlugwiseHAThingConfiguration;

/**
 * Base class for an Plugwise HA handler
 *
 * @author Kaj Visser - Initial contribution
 */
public abstract class BasePlugwiseHAHandler extends BaseThingHandler {
    private PlugwiseHAThingConfiguration configuration;

    public BasePlugwiseHAHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        configuration = getConfigAs(PlugwiseHAThingConfiguration.class);
        checkConfig();
    }

    @Override
    public void dispose() {
        configuration = null;
    }

    public String getId() {
        if (configuration != null) {
            return configuration.id;
        }
        return null;
    }

    /**
     * Returns the configuration of the Thing
     *
     * @return The parsed configuration or null
     */
    protected PlugwiseHAThingConfiguration getPlugwiseHAThingConfig() {
        return configuration;
    }

    /**
     * Retrieves the bridge
     *
     * @return The Plugwise HA bridge
     */
    protected PlugwiseHABridgeHandler getPlugwiseHABridge() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            return (PlugwiseHABridgeHandler) bridge.getHandler();
        }

        return null;
    }

    /**
     * Retrieves the Plugwise HA configuration from the bridge
     *
     * @return The current Plugwise HA configuration
     */
    protected Locations getLocations() {
        PlugwiseHABridgeHandler bridge = getPlugwiseHABridge();
        if (bridge != null) {
            return bridge.getLocations();
        }

        return null;
    }

    /**
     * Retrieves the Plugwise HA configuration from the bridge
     *
     * @return The current Plugwise HA configuration
     */
    protected void requestUpdate() {
        Bridge bridge = getBridge();
        if (bridge != null) {
            ((PlugwiseHABridgeHandler) bridge).getLocations();
        }
    }

    /**
     * Updates the status of the Plugwise HA thing when it changes
     *
     * @param newStatus The new status to update to
     */
    protected void updatePlugwiseHAThingStatus(ThingStatus newStatus) {
        updatePlugwiseHAThingStatus(newStatus, ThingStatusDetail.NONE, null);
    }

    /**
     * Updates the status of the Plugwise HA thing when it changes
     *
     * @param newStatus The new status to update to
     * @param detail    The status detail value
     * @param message   The message to show with the status
     */
    protected void updatePlugwiseHAThingStatus(ThingStatus newStatus, ThingStatusDetail detail, String message) {
        // Prevent spamming the log file
        if (!newStatus.equals(getThing().getStatus())) {
            updateStatus(newStatus, detail, message);
        }
    }

    /**
     * Checks the configuration for validity, result is reflected in the status of the Thing
     *
     * @param configuration The configuration to check
     */
    private void checkConfig() {
        if (configuration == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Configuration is missing or corrupted.");
        } else if (StringUtils.isEmpty(configuration.id)) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "ID not configured.");
        }
    }

}
