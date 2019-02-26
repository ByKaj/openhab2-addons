package org.openhab.binding.plugwiseha.internal.discovery;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.openhab.binding.plugwiseha.internal.PlugwiseHABindingConstants;
import org.openhab.binding.plugwiseha.internal.api.models.response.Location;
import org.openhab.binding.plugwiseha.internal.handler.GatewayStatusListener;
import org.openhab.binding.plugwiseha.internal.handler.PlugwiseHABridgeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlugwiseHADiscoveryService extends AbstractDiscoveryService implements GatewayStatusListener {

    private final Logger logger = LoggerFactory.getLogger(PlugwiseHADiscoveryService.class);
    private static final int TIMEOUT = 5;

    private PlugwiseHABridgeHandler bridge;
    private ThingUID bridgeUID;

    public PlugwiseHADiscoveryService(PlugwiseHABridgeHandler bridge) {
        super(PlugwiseHABindingConstants.SUPPORTED_THING_TYPES_UIDS, TIMEOUT);

        this.bridge = bridge;
        this.bridgeUID = this.bridge.getThing().getUID();
        this.bridge.addGatewayStatusListener(this);
    }

    @Override
    protected void startScan() {
        discoverDevices();
    }

    @Override
    protected void startBackgroundDiscovery() {
        discoverDevices();
    }

    @Override
    protected synchronized void stopScan() {
        super.stopScan();
        removeOlderResults(getTimestampOfLastScan());
    }

    @Override
    public void gatewayStatusChanged(ThingStatus status) {
        if (status == ThingStatus.ONLINE) {
            discoverDevices();
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        bridge.removeGatewayStatusListener(this);
    }

    private void discoverDevices() {
        if (bridge.getThing().getStatus() != ThingStatus.ONLINE) {
            logger.debug("Plugwise HA Boiler Gateway not online, scanning postponed.");
            return;
        }

        // for (Location location : bridge.getLocations()) {

        // addZoneDiscoveryResult(location);

        /*
         * for (Gateway gateway : location.getGateways()) {
         * for (TemperatureControlSystem tcs : gateway.getTemperatureControlSystems()) {
         * addDisplayDiscoveryResult(location, tcs);
         * for (Zone zone : tcs.getZones()) {
         * addZoneDiscoveryResult(location, zone);
         * }
         * }
         * }
         */
        // }

        stopScan();
    }

    private void addZoneDiscoveryResult(Location location) {

        String id = location.getId();
        String name = location.getName();
        ThingUID thingUID = new ThingUID(PlugwiseHABindingConstants.THING_TYPE_ZONE, bridgeUID, id);

        Map<String, Object> properties = new HashMap<>(2);
        // properties.put(EvohomeBindingConstants.PROPERTY_ID, id);
        // properties.put(EvohomeBindingConstants.PROPERTY_NAME, name);

        addDiscoveredThing(thingUID, properties, name);
    }

    private void addDiscoveredThing(ThingUID thingUID, Map<String, Object> properties, String displayLabel) {
        DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withProperties(properties)
                .withBridge(bridgeUID).withLabel(displayLabel).build();
        thingDiscovered(discoveryResult);
    };

}
