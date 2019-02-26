package org.openhab.binding.plugwiseha.internal.api.models.response;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Alias for a list of point logs
 *
 * @author Kaj Visser - Initial contribution
 *
 */
@XStreamAlias("logs")
public class Logs extends ArrayList<Log> {

}
