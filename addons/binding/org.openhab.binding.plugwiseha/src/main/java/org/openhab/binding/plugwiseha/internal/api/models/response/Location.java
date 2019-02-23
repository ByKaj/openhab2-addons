package org.openhab.binding.plugwiseha.internal.api.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the Location
 *
 * @author Kaj Visser - Initial contribution
 *
 */
public class Location {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("preset")
    private String preset;

    @SerializedName("logs")
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
