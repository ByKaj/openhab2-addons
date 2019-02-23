package org.openhab.binding.plugwiseha.internal.api.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for the Log info
 *
 * @author Kaj Visser - Initial contribution
 *
 */
public class Log {

    @SerializedName("id")
    private String id;

    @SerializedName("updated_date")
    private String updatedDate;

    @SerializedName("type")
    private String type;

    @SerializedName("unit")
    private String unit;

    @SerializedName("measurement")
    private String measurement;

    public String getId() {
        return id;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public String getType() {
        return type;
    }

    public String getUnit() {
        return unit;
    }

    public String getMeasurement() {
        return measurement;
    }

}
