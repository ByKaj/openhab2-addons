package org.openhab.binding.plugwiseha.internal.api;

import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.openhab.binding.plugwiseha.internal.PlugwiseHAConfiguration;
import org.openhab.binding.plugwiseha.internal.api.models.response.Locations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the Plugwise HA client API
 *
 * @author Kaj Visser - Initial contribution
 *
 */
public class PlugwiseHAApiClient {

    private final Logger logger = LoggerFactory.getLogger(PlugwiseHAApiClient.class);
    private final HttpClient httpClient;
    private final PlugwiseHAConfiguration configuration;
    private final PlugwiseHAApiAccess apiAccess;

    private Locations locations = new Locations();

    public PlugwiseHAApiClient(PlugwiseHAConfiguration configuration, HttpClient httpClient) throws Exception {
        this.configuration = configuration;
        this.httpClient = httpClient;

        try {
            httpClient.start();
        } catch (Exception e) {
            logger.error("Could not start the HTTP client", e);
            throw new PlugwiseHAApiClientException("Could not start the HTTP client", e);
        }

        apiAccess = new PlugwiseHAApiAccess(configuration, httpClient);
    }

    /**
     * Closes the current connection to the API
     */
    public void close() {
        locations = null;

        if (httpClient.isStarted()) {
            try {
                httpClient.stop();
            } catch (Exception e) {
                logger.debug("Could not stop the HTTP client.", e);
            }
        }
    }

    public boolean login() {
        boolean result = false;

        try {
            locations = requestLocations();
            result = true;
        } catch (TimeoutException e) {
            logger.warn("Timeout while retrieving location information.");
            result = false;
        }

        return result;
    }

    public void logout() {
        close();
    }

    public void update() {
        try {
            locations = requestLocations();
        } catch (TimeoutException e) {
            logger.info("Timeout on update");
        }
    }

    public Locations getLocations() {
        return locations;
    }

    private Locations requestLocations() throws TimeoutException {
        Locations locations = new Locations();

        String url = PlugwiseHAApiConstants.API_BASE + configuration.host + PlugwiseHAApiConstants.API_ZONES;

        locations = apiAccess.doAuthenticatedGet(url, Locations.class);

        return locations;
    }

    public void setHeatingZoneOverride(String zoneId, double setPoint) throws TimeoutException {
        // HeatSetPoint setPointCommand = new HeatSetPointBuilder().setSetPoint(setPoint).build();
        // setHeatingZoneOverride(zoneId, setPointCommand);
        logger.debug("[TODO] setHeatingZoneOverride activated for zone " + zoneId + " with setpoint " + setPoint);
    }

    /*
     * private void setHeatingZoneOverride(String zoneId, HeatSetPoint heatSetPoint) throws TimeoutException {
     * String url = EvohomeApiConstants.URL_V2_BASE + EvohomeApiConstants.URL_V2_HEAT_SETPOINT;
     * url = String.format(url, zoneId);
     * apiAccess.doAuthenticatedPut(url, heatSetPoint);
     * }
     */
}
