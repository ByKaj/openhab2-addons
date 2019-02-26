package org.openhab.binding.plugwiseha.internal.api.models.response;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Alias for a list of locations
 *
 * @author Kaj Visser - Initial contribution
 *
 */
@XStreamAlias("locations")
public class Locations {

    @XStreamImplicit
    private List<Location> locationList = new ArrayList<Location>();;

    public void add(Location location) {
        locationList.add(location);
    }

    public List<Location> getLocations() {
        return locationList;
    }
}
