package org.openhab.binding.plugwiseha.internal.api.models.response;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Response model for the Location
 *
 * @author Kaj Visser - Initial contribution
 *
 */
@XStreamAlias("location")
public class Location {

    @XStreamAsAttribute
    private String id;

    @XStreamAlias("name")
    private String name;

    @XStreamAlias("type")
    private String type;

    @XStreamAlias("preset")
    private String preset;

    @XStreamAlias("logs")
    private Logs logs;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getPreset() {
        return preset;
    }

    public Logs getLogs() {
        return logs;
    }
}
