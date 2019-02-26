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

    @XStreamAlias("id")
    @XStreamAsAttribute
    private String id;

    @XStreamAlias("name")
    private String name;

    @XStreamAlias("type")
    private String type;

    @XStreamAlias("preset")
    private String preset;

    // @XStreamImplicit
    // private Logs logs = new Logs();

    public Location(String id, String name, String type, String preset) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.preset = preset;
    }

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

    // public Logs getLogs() {
    // return logs != null ? logs : (Logs) Collections.<Log> emptyList();
    // }
}
