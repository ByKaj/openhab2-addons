/**
 * Copyright (c) 2014,2019 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.plugwiseha.internal.handler;

import static org.openhab.binding.plugwiseha.internal.PlugwiseHABindingConstants.*;

import java.util.concurrent.ScheduledFuture;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.openhab.binding.plugwiseha.internal.PlugwiseHAConfiguration;
import org.openhab.binding.plugwiseha.internal.PlugwiseHAConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link PlugwiseHAHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Kaj Visser - Initial contribution
 */
public class PlugwiseHAHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(PlugwiseHAHandler.class);

    @Nullable
    private PlugwiseHAConfiguration config;

    private final PlugwiseHAConnection connection = new PlugwiseHAConnection();

    private int refresh;
    private String locationData = null;

    ScheduledFuture<?> refreshJob;

    public PlugwiseHAHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (CHANNEL_1.equals(channelUID.getId())) {
            if (command instanceof RefreshType) {
                // TODO: handle data refresh
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information:
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    @Override
    public void initialize() {
        logger.debug("Initializing PlugwiseHA handler.");

        // TODO: Initialize the handler.
        // The framework requires you to return from this method quickly. Also, before leaving this method a thing
        // status from one of ONLINE, OFFLINE or UNKNOWN must be set. This might already be the real thing status in
        // case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN);

        // Example for background initialization:
        scheduler.execute(() -> {
            boolean thingReachable = true; // <background task with long running initialization here>

            config = getConfigAs(PlugwiseHAConfiguration.class);
            refresh = config.refreshInterval;

            doAutomaticRefresh();

            // when done do:
            if (thingReachable) {
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        });

        logger.debug("Finished initializing PlugwiseHA handler.");

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }

    @Override
    public void dispose() {
        refreshJob.cancel(true);
    }

    private void doAutomaticRefresh() {
        // refreshJob = scheduler.scheduleWithFixedDelay(() -> {
        try {
            boolean success = updateZoneData();

            if (success) {
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_ZONE_TYPE), getTemperature());
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_ZONE_PRESET), getTemperature());
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_ZONE_TEMPERATURE), getTemperature());
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_ZONE_TEMPERATURE_UPDATE), getTemperature());
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_ZONE_SETPOINT), getTemperature());
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_ZONE_SETPOINT_UPDATE), getTemperature());
                updateState(new ChannelUID(getThing().getUID(), CHANNEL_ZONE_MODE), getTemperature());
            }
        } catch (Exception e) {
            logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR, e.getMessage());
        }
        // }, 0, refresh, TimeUnit.SECONDS);
    }

    private synchronized boolean updateZoneData() {
        boolean result = false;

        // Get location data from gateway
        locationData = connection.getLocationData();

        if (!locationData.isEmpty()) {
            result = true;
        }

        return result;
    }

    private State getTemperature() {
        return UnDefType.UNDEF;
    }
}
