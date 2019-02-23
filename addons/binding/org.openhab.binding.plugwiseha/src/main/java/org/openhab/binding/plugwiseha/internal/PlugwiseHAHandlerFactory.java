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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.eclipse.smarthome.io.net.http.HttpClientFactory;
import org.openhab.binding.plugwiseha.internal.discovery.PlugwiseHADiscoveryService;
import org.openhab.binding.plugwiseha.internal.handler.PlugwiseHABridgeHandler;
import org.openhab.binding.plugwiseha.internal.handler.PlugwiseHAZoneHandler;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link PlugwiseHAHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Kaj Visser - Initial contribution
 */

@Component(configurationPid = "binding.plugwiseha", service = ThingHandlerFactory.class)
public class PlugwiseHAHandlerFactory extends BaseThingHandlerFactory {

    private final Map<ThingUID, @Nullable ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();
    private HttpClient httpClient;

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return PlugwiseHABindingConstants.SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();
        if (thingTypeUID.equals(PlugwiseHABindingConstants.THING_TYPE_GATEWAY)) {
            PlugwiseHABridgeHandler bridge = new PlugwiseHABridgeHandler((Bridge) thing, httpClient);
            registerPlugwiseHADiscoveryService(bridge);
            return bridge;
        } else if (thingTypeUID.equals(PlugwiseHABindingConstants.THING_TYPE_ZONE)) {
            return new PlugwiseHAZoneHandler(thing);
        } else if (thingTypeUID.equals(PlugwiseHABindingConstants.THING_TYPE_HOME)) {
            // return new PlugwiseHAHomeHandler(thing);
        }

        return null;
    }

    private void registerPlugwiseHADiscoveryService(PlugwiseHABridgeHandler plugwiseHABridgeHandler) {
        PlugwiseHADiscoveryService discoveryService = new PlugwiseHADiscoveryService(plugwiseHABridgeHandler);

        this.discoveryServiceRegs.put(plugwiseHABridgeHandler.getThing().getUID(), bundleContext
                .registerService(DiscoveryService.class.getName(), discoveryService, new Hashtable<String, Object>()));
    }

    @Override
    public ThingHandler registerHandler(Thing thing) {
        return super.registerHandler(thing);
    }

    @Override
    protected void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof PlugwiseHABridgeHandler) {
            ServiceRegistration<?> serviceReg = this.discoveryServiceRegs.get(thingHandler.getThing().getUID());
            if (serviceReg != null) {
                PlugwiseHADiscoveryService service = (PlugwiseHADiscoveryService) bundleContext
                        .getService(serviceReg.getReference());
                if (service != null) {
                    service.deactivate();
                }
                serviceReg.unregister();
                discoveryServiceRegs.remove(thingHandler.getThing().getUID());
            }
        }
    }

    @Reference
    protected void setHttpClientFactory(HttpClientFactory httpClientFactory) {
        this.httpClient = httpClientFactory.getCommonHttpClient();
    }

    protected void unsetHttpClientFactory(HttpClientFactory httpClientFactory) {
        this.httpClient = null;
    }

}
