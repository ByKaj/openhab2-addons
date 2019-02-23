package org.openhab.binding.plugwiseha.internal.handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.openhab.binding.plugwiseha.internal.PlugwiseHAConfiguration;
import org.openhab.binding.plugwiseha.internal.RunnableWithTimeout;
import org.openhab.binding.plugwiseha.internal.api.PlugwiseHAApiClient;
import org.openhab.binding.plugwiseha.internal.api.models.response.Locations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the bridge for this binding. Controls the authentication sequence.
 * Manages the scheduler for getting updates from the API and updates the Things it contains.
 *
 * @author Kaj Visser - Initial contribution
 *
 */
public class PlugwiseHABridgeHandler extends BaseBridgeHandler {

    private final Logger logger = LoggerFactory.getLogger(PlugwiseHABridgeHandler.class);
    private final HttpClient httpClient;
    private PlugwiseHAConfiguration configuration;
    private PlugwiseHAApiClient apiClient;
    private List<GatewayStatusListener> listeners = new CopyOnWriteArrayList<GatewayStatusListener>();

    protected ScheduledFuture<?> refreshTask;

    public PlugwiseHABridgeHandler(Bridge bridge, HttpClient httpClient) {
        super(bridge);
        this.httpClient = httpClient;
    }

    @Override
    public void initialize() {
        configuration = getConfigAs(PlugwiseHAConfiguration.class);

        if (checkConfig()) {
            try {
                apiClient = new PlugwiseHAApiClient(configuration, this.httpClient);
            } catch (Exception e) {
                logger.error("Could not start API client", e);
                updateGatewayStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Could not create Plugwise HA API client");
            }

            if (apiClient != null) {
                // Initialization can take a while, so kick it off on a separate thread
                scheduler.schedule(() -> {
                    if (apiClient.login()) {
                        startRefreshTask();
                    } else {
                        updateGatewayStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                                "Authentication failed.");
                    }
                }, 0, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public void dispose() {
        disposeRefreshTask();
        disposeApiClient();
        listeners.clear();
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
    }

    public Locations getLocations() {
        return apiClient.getLocations();
    }

    public void addGatewayStatusListener(GatewayStatusListener listener) {
        listeners.add(listener);
        listener.gatewayStatusChanged(getThing().getStatus());
    }

    public void removeGatewayStatusListener(GatewayStatusListener listener) {
        listeners.remove(listener);
    }

    public void setPermanentSetPoint(String zoneId, double newTemp) {
        tryToCall(() -> apiClient.setHeatingZoneOverride(zoneId, newTemp));
    }

    private void tryToCall(RunnableWithTimeout action) {
        try {
            action.run();
        } catch (TimeoutException e) {
            updateGatewayStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Timeout on executing request.");
        }
    }

    private boolean checkConfig() {
        String errorMessage = "";

        if (configuration == null) {
            errorMessage = "Configuration is missing or corrupted.";
        } else if (StringUtils.isEmpty(configuration.host)) {
            errorMessage = "Hostname or IP-address is not configured.";
        } else if (StringUtils.isEmpty(configuration.smileId)) {
            errorMessage = "Smile ID is not configured.";
        } else {
            return true;
        }

        updateGatewayStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, errorMessage);

        return false;
    }

    private void updateGatewayStatus(ThingStatus newStatus) {
        updateGatewayStatus(newStatus, ThingStatusDetail.NONE, null);
    }

    private void updateGatewayStatus(ThingStatus newStatus, ThingStatusDetail detail, String message) {
        // Prevent spamming the log file
        if (!newStatus.equals(getThing().getStatus())) {
            updateStatus(newStatus, detail, message);
            updateListeners(newStatus);
        }
    }

    private void updateListeners(ThingStatus status) {
        for (GatewayStatusListener listener : listeners) {
            listener.gatewayStatusChanged(status);
        }
    }

    private void disposeApiClient() {
        if (apiClient != null) {
            apiClient.logout();
        }
        apiClient = null;
    }

    private void disposeRefreshTask() {
        if (refreshTask != null) {
            refreshTask.cancel(true);
        }
    }

    private void startRefreshTask() {
        disposeRefreshTask();

        refreshTask = scheduler.scheduleWithFixedDelay(this::update, 0, configuration.refreshInterval,
                TimeUnit.SECONDS);
    }

    private void update() {
        try {
            apiClient.update();
            updateGatewayStatus(ThingStatus.ONLINE);
            updateThings();
        } catch (Exception e) {
            updateGatewayStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR, e.getMessage());
            logger.debug("Failed to update installation status.", e);
        }
    }

    private void updateThings() {
        /*
         * Map<String, TemperatureControlSystemStatus> idToTcsMap = new HashMap<>();
         * Map<String, ZoneStatus> idToZoneMap = new HashMap<>();
         * Map<String, GatewayStatus> tcsIdToGatewayMap = new HashMap<>();
         * Map<String, String> zoneIdToTcsIdMap = new HashMap<>();
         * Map<String, ThingStatus> idToTcsThingsStatusMap = new HashMap<>();
         *
         * // First, create a lookup table
         * for (LocationStatus location : apiClient.getInstallationStatus()) {
         * for (GatewayStatus gateway : location.getGateways()) {
         * for (TemperatureControlSystemStatus tcs : gateway.getTemperatureControlSystems()) {
         * idToTcsMap.put(tcs.getSystemId(), tcs);
         * tcsIdToGatewayMap.put(tcs.getSystemId(), gateway);
         * for (ZoneStatus zone : tcs.getZones()) {
         * idToZoneMap.put(zone.getZoneId(), zone);
         * zoneIdToTcsIdMap.put(zone.getZoneId(), tcs.getSystemId());
         * }
         * }
         * }
         * }
         *
         * // Then update the things by type, with pre-filtered info
         * for (Thing handler : getThing().getThings()) {
         * ThingHandler thingHandler = handler.getHandler();
         *
         * if (thingHandler instanceof EvohomeTemperatureControlSystemHandler) {
         * EvohomeTemperatureControlSystemHandler tcsHandler = (EvohomeTemperatureControlSystemHandler) thingHandler;
         * tcsHandler.update(tcsIdToGatewayMap.get(tcsHandler.getId()), idToTcsMap.get(tcsHandler.getId()));
         * idToTcsThingsStatusMap.put(tcsHandler.getId(), tcsHandler.getThing().getStatus());
         * }
         * if (thingHandler instanceof EvohomeHeatingZoneHandler) {
         * EvohomeHeatingZoneHandler zoneHandler = (EvohomeHeatingZoneHandler) thingHandler;
         * zoneHandler.update(idToTcsThingsStatusMap.get(zoneIdToTcsIdMap.get(zoneHandler.getId())),
         * idToZoneMap.get(zoneHandler.getId()));
         * }
         * }
         */
    }

}
