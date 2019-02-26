package org.openhab.binding.plugwiseha.internal.xml;

import org.eclipse.smarthome.config.xml.util.XmlDocumentReader;
import org.openhab.binding.plugwiseha.internal.api.models.response.Location;
import org.openhab.binding.plugwiseha.internal.api.models.response.Locations;
import org.openhab.binding.plugwiseha.internal.api.models.response.Log;
import org.openhab.binding.plugwiseha.internal.api.models.response.Logs;
import org.openhab.binding.plugwiseha.internal.api.models.response.Period;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class PlugwiseHAXmlReader extends XmlDocumentReader<Locations> {

    private XStream xstream;

    public PlugwiseHAXmlReader() {
        StaxDriver driver = new StaxDriver();
        this.xstream = new XStream(driver);
        // this.xstream.processAnnotations(Locations.class);
        this.xstream.processAnnotations(Location.class);
        this.xstream.processAnnotations(Logs.class);
        this.xstream.processAnnotations(Log.class);
        this.xstream.processAnnotations(Period.class);
        this.xstream.ignoreUnknownElements();

        // super.setClassLoader(Locations.class.getClassLoader());
    }

    @Override
    public void registerConverters(XStream xstream) {
    }

    @Override
    public void registerAliases(XStream xstream) {

        // Locations
        xstream.alias("locations", Locations.class);
        // xstream.aliasField("location", Locations.class, "locations");

        /*
         * // Location
         * xstream.alias("location", Location.class);
         * xstream.aliasField("id", Location.class, "id");
         * xstream.useAttributeFor(Location.class, "id");
         * xstream.aliasField("name", Location.class, "name");
         * xstream.aliasField("type", Location.class, "type");
         * xstream.aliasField("preset", Location.class, "preset");
         * xstream.aliasField("logs", Location.class, "logs");
         *
         * // Log
         * xstream.alias("point_log", Log.class);
         * xstream.aliasField("id", Log.class, "id");
         * xstream.useAttributeFor(Log.class, "id");
         * xstream.aliasField("updated_date", Log.class, "updatedDate");
         * xstream.aliasField("type", Log.class, "type");
         * xstream.aliasField("unit", Log.class, "unit");
         *
         * // Log: flatten the period tag because there's only one measurement
         * // xstream.addImplicitCollection(Log.class, "period");
         * xstream.aliasField("measurement", Log.class, "measurement");
         */

        // Ignore unknown elements, and the actuator_functionalities and appliances for now.
        // Also makes the binding more tolerant for future changes to the XML schema.
        xstream.ignoreUnknownElements();
    }

    /**
     * Reads the XML document containing a specific XML tag from the specified {@link String} and converts it to the
     * according object.
     * <p>
     * This method returns {@code null} if the given string is {@code null}.
     *
     * @param xml string with the XML document to be read (could be null)
     * @return the conversion result object (could be null)
     * @throws ConversionException if the specified document contains invalid content
     */
    public Locations readFromXML(String xml) throws ConversionException {
        if (xml != null) {
            return (Locations) this.xstream.fromXML(xml);
        }

        return null;
    }
}
