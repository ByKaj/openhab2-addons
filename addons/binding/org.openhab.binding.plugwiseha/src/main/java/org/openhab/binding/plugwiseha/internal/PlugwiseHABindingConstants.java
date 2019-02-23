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
package org.openhab.binding.plugwiseha.internal;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link PlugwiseHABindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Kaj Visser - Initial contribution
 */
@NonNullByDefault
public class PlugwiseHABindingConstants {

    private static final String BINDING_ID = "plugwiseha";

    // Sample data
    public static final ThingTypeUID THING_TYPE_SAMPLE = new ThingTypeUID(BINDING_ID, "sample");
    public static final String CHANNEL_1 = "channel1";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_GATEWAY = new ThingTypeUID(BINDING_ID, "gateway");
    public static final ThingTypeUID THING_TYPE_ZONE = new ThingTypeUID(BINDING_ID, "zone");
    public static final ThingTypeUID THING_TYPE_HOME = new ThingTypeUID(BINDING_ID, "home");

    // Zone channels ID's
    public static final String CHANNEL_ZONE_TYPE = "Type";
    public static final String CHANNEL_ZONE_PRESET = "Preset";
    public static final String CHANNEL_ZONE_TEMPERATURE = "Temperature";
    public static final String CHANNEL_ZONE_TEMPERATURE_UPDATE = "TemperatureUpdate";
    public static final String CHANNEL_ZONE_SETPOINT = "SetPoint";
    public static final String CHANNEL_ZONE_SETPOINT_UPDATE = "SetPointUpdate";
    public static final String CHANNEL_ZONE_MODE = "Mode";

    // Home channels ID's
    public static final String CHANNEL_HOME_PRESET = "Preset";
    public static final String CHANNEL_HOME_TEMPERATURE = "OutdoorTemperature";
    public static final String CHANNEL_HOME_HUMIDITY = "OutdoorHumidity";
    public static final String CHANNEL_HOME_SOLAR_IRRADIANCE = "SolarIrradiance";
    public static final String CHANNEL_HOME_WIND_VECTOR = "WindVector";

    // List of Discovery properties
    public static final String LOCATION_ID = "locationId";
    public static final String LOCATION_NAME = "locationName";

    // List of all addressable things in openHAB (+ the bridge)
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.unmodifiableSet(
            Stream.of(THING_TYPE_ZONE, THING_TYPE_HOME, THING_TYPE_GATEWAY).collect(Collectors.toSet()));

}
