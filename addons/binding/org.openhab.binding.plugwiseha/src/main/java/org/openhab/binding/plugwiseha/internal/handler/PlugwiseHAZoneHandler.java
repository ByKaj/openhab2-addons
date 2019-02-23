package org.openhab.binding.plugwiseha.internal.handler;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.plugwiseha.internal.PlugwiseHABindingConstants;

public class PlugwiseHAZoneHandler extends BasePlugwiseHAHandler {

    private ThingStatus status;

    public PlugwiseHAZoneHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    public void update(ThingStatus status) {
        this.status = status;

        // Make the zone offline when the related display is offline
        // If the related display is not a thing, ignore this
        if (status != null && status.equals(ThingStatus.OFFLINE)) {
            updatePlugwiseHAThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, "Zone is offline");
        } else {
            updatePlugwiseHAThingStatus(ThingStatus.ONLINE);

            /*
             * updateState(PlugwiseHABindingConstants.CHANNEL_ZONE_TEMPERATURE,
             * new DecimalType(zoneStatus.getTemperature().getTemperature()));
             * updateState(EvohomeBindingConstants.ZONE_SET_POINT_STATUS_CHANNEL,
             * new StringType(zoneStatus.getHeatSetpoint().getSetpointMode()));
             * updateState(EvohomeBindingConstants.ZONE_SET_POINT_CHANNEL,
             * new DecimalType(zoneStatus.getHeatSetpoint().getTargetTemperature()));
             */
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command == RefreshType.REFRESH) {
            // update(status, zoneStatus);
        } else {
            PlugwiseHABridgeHandler bridge = getPlugwiseHABridge();
            if (bridge != null) {
                String channelId = channelUID.getId();
                if (PlugwiseHABindingConstants.CHANNEL_ZONE_SETPOINT.equals(channelId)
                        && command instanceof DecimalType) {
                    double newTemp = ((DecimalType) command).doubleValue();

                    if (newTemp >= 0 && newTemp <= 35) {
                        bridge.setPermanentSetPoint(getPlugwiseHAThingConfig().id, newTemp);
                    }
                }
            }
        }

    }

}
