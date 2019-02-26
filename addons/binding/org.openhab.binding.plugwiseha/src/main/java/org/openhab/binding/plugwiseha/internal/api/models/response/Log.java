package org.openhab.binding.plugwiseha.internal.api.models.response;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Response model for the Log info
 *
 * @author Kaj Visser - Initial contribution
 *
 */
@XStreamAlias("point_log")
public class Log {

    @XStreamAlias("id")
    @XStreamAsAttribute
    private String id;

    @XStreamAlias("updated_date")
    private String updatedDate;

    @XStreamAlias("type")
    private String type;

    @XStreamAlias("unit")
    private String unit;

    @XStreamAlias("period")
    private Period period;

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

    public Period getPeriod() {
        return period;
    }

}
