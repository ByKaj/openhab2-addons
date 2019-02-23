package org.openhab.binding.plugwiseha.internal.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.util.B64Code;
import org.openhab.binding.plugwiseha.internal.PlugwiseHAConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

public class PlugwiseHAApiAccess {
    private final Logger logger = LoggerFactory.getLogger(PlugwiseHAApiAccess.class);

    private static final int REQUEST_TIMEOUT_SECONDS = 10;

    private final HttpClient httpClient;
    private PlugwiseHAConfiguration configuration;
    private final Gson gson;
    private final XStream xstream;

    public PlugwiseHAApiAccess(PlugwiseHAConfiguration configuration, HttpClient httpClient) {
        StaxDriver driver = new StaxDriver();

        this.xstream = new XStream(driver);

        this.gson = new GsonBuilder().create();
        this.httpClient = httpClient;
        this.configuration = configuration;
    }

    /**
     * Issues an HTTP request on the API's URL. Makes sure that the request is correctly formatted.
     *
     * @param method      The HTTP method to use (POST, GET, ...)
     * @param url         The URL to query
     * @param headers     The optional additional headers to apply, can be null
     * @param requestData The optional request data to use, can be null
     * @param contentType The content type to use with the request data. Required when using requestData
     * @return The result of the request or null
     * @throws TimeoutException Thrown when a request times out
     */
    public <TOut> TOut doRequest(HttpMethod method, String url, Map<String, String> headers, String requestData,
            String contentType, Class<TOut> outClass) throws TimeoutException {

        TOut retVal = null;
        logger.debug("Requesting: [{}]", url);

        try {
            Request request = httpClient.newRequest(url).method(method);

            if (headers != null) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    request.header(header.getKey(), header.getValue());
                }
            }

            if (requestData != null) {
                request.content(new StringContentProvider(requestData), contentType);
            }

            ContentResponse response = request.timeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS).send();

            logger.debug("Response: {}", response);
            logger.debug("\n{}\n{}", response.getHeaders(), response.getContentAsString());

            if ((response.getStatus() == HttpStatus.OK_200) || (response.getStatus() == HttpStatus.ACCEPTED_202)) {
                String reply = response.getContentAsString();

                if (outClass != null) {
                    // retVal = new Gson().fromJson(reply, outClass);
                    retVal = (TOut) xstream.fromXML(reply);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.debug("Error in handling request: ", e);
        }

        return retVal;
    }

    /**
     * Issues an HTTP GET request on the API's URL, using an object that is serialized to JSON as input.
     * Makes sure that the request is correctly formatted.*
     *
     * @param url      The URL to query
     * @param outClass The type of the requested result
     * @return The result of the request or null
     * @throws TimeoutException Thrown when a request times out
     */
    public <TOut> TOut doAuthenticatedGet(String url, Class<TOut> outClass) throws TimeoutException {
        return doAuthenticatedRequest(HttpMethod.GET, url, null, outClass);
    }

    /**
     * Issues an HTTP request on the API's URL, using an object that is serialized to JSON as input.
     * Makes sure that the request is correctly formatted.*
     *
     * @param url              The URL to query
     * @param requestContainer The object to use as JSON data for the request
     * @throws TimeoutException Thrown when a request times out
     */
    public void doAuthenticatedPut(String url, Object requestContainer) throws TimeoutException {
        doAuthenticatedRequest(HttpMethod.PUT, url, requestContainer, null);
    }

    /**
     * Issues an HTTP request on the API's URL, using an object that is serialized to JSON as input.
     * Makes sure that the request is correctly formatted.*
     *
     * @param method           The HTTP method to use (POST, GET, ...)
     * @param url              The URL to query
     * @param headers          The optional additional headers to apply, can be null
     * @param requestContainer The object to use as JSON data for the request
     * @param outClass         The type of the requested result
     * @return The result of the request or null
     * @throws TimeoutException Thrown when a request times out
     */
    private <TOut> TOut doRequest(HttpMethod method, String url, Map<String, String> headers, Object requestContainer,
            Class<TOut> outClass) throws TimeoutException {

        String json = null;
        if (requestContainer != null) {
            json = this.gson.toJson(requestContainer);
        }

        return doRequest(method, url, headers, json, "application/xml", outClass);
    }

    /**
     * Issues an HTTP request on the API's URL, using an object that is serialized to JSON as input and
     * using the authentication applied to the type.
     * Makes sure that the request is correctly formatted.*
     *
     * @param method           The HTTP method to use (POST, GET, ...)
     * @param url              The URL to query
     * @param requestContainer The object to use as JSON data for the request
     * @param outClass         The type of the requested result
     * @return The result of the request or null
     * @throws TimeoutException Thrown when a request times out
     */
    private <TOut> TOut doAuthenticatedRequest(HttpMethod method, String url, Object requestContainer,
            Class<TOut> outClass) throws TimeoutException {
        Map<String, String> headers = null;

        // Get smile ID from configuration
        String smileId = configuration.smileId;

        if (smileId != null) {
            // Prepare base 64 string for basic authentication
            String authString = PlugwiseHAApiConstants.API_USERNAME + ":" + smileId;
            String authStringEnc = B64Code.encode(authString);

            headers = new HashMap<String, String>();

            headers.put("Authorization", "Basic " + authStringEnc);
            headers.put("Accept",
                    "application/json, application/xml, text/json, text/x-json, text/javascript, text/xml");
        }

        return doRequest(method, url, headers, requestContainer, outClass);
    }
}
