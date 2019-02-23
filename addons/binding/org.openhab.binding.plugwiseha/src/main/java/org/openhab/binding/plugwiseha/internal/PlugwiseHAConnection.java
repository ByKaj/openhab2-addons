package org.openhab.binding.plugwiseha.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.jetty.util.B64Code;
import org.openhab.binding.plugwiseha.internal.api.PlugwiseHAApiConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for Plugwise HA connection.
 *
 * @author Kaj Visser - Initial contribution
 *
 */
public class PlugwiseHAConnection {
    private final Logger logger = LoggerFactory.getLogger(PlugwiseHAConnection.class);

    private PlugwiseHAConfiguration config;

    private static final int TIMEOUT = 10 * 1000; // 10s
    private static final String USERNAME = "smile"; // Default

    public String getLocationData() {
        String result = null;

        // Read configuration
        // String gatewayUrl = "http://" + config.host + "/core/locations";
        // String smileId = config.smileId;
        String gatewayUrl = "http://gw-adam/core/locations";
        String smileId = "ldljmsjq";

        try {
            // Prepare base 64 string for basic authentication
            // String authString = USERNAME + ":" + smileId;
            // byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            // String authStringEnc = new String(authEncBytes);

            // Prepare base 64 string for basic authentication
            String authString = PlugwiseHAApiConstants.API_USERNAME + ":" + smileId;
            String authStringEnc = B64Code.encode(authString);

            // Open connection, set authentication header and timeout
            URL url = new URL(gatewayUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            urlConnection.setConnectTimeout(TIMEOUT);

            // Get result in an InputStream
            InputStream is = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            // Read InputStream and append to buffer
            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();

            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            result = sb.toString();

            // Debug
            logger.debug("----------------------------------------");
            logger.debug(result);
            logger.debug("----------------------------------------");

        } catch (MalformedURLException e) {
            logger.error("MalformedURLException: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("IOException: {}", e.getMessage());
        }

        return result;
    }

}
