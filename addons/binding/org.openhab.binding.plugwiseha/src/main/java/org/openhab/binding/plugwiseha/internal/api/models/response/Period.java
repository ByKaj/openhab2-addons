package org.openhab.binding.plugwiseha.internal.api.models.response;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Response model for the period within a log
 *
 * @author Kaj Visser - Initial contribution
 *
 */
@XStreamAlias("period")
public class Period {

    @XStreamAlias("measurement")
    private String measurement;

    public String getMeasurement() {
        return measurement;
    }
}
