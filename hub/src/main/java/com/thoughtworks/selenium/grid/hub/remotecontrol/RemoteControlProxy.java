package com.thoughtworks.selenium.grid.hub.remotecontrol;

import com.thoughtworks.selenium.grid.HttpClient;
import com.thoughtworks.selenium.grid.HttpParameters;
import com.thoughtworks.selenium.grid.Response;
import com.thoughtworks.selenium.grid.hub.HubServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/**
 * Local interface to a real remote control running somewhere in the grid.
 */
public class RemoteControlProxy {

    private final static Log LOGGER = LogFactory.getLog(HubServer.class);

    private boolean sessionInProgress;
    private final HttpClient httpClient;
    private final String environment;
    private final String host;
    private final int port;


    public RemoteControlProxy(String host, int port, String environment, HttpClient httpClient) {
        if (null == host) {
            throw new IllegalArgumentException("host cannot be null");
        }
        if (null == environment) {
            throw new IllegalArgumentException("environment cannot be null");
        }
        this.host = host;
        this.port = port;
        this.environment = environment;
        this.sessionInProgress = false;
        this.httpClient = httpClient;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String environment() {
        return environment;
    }

    public String remoteControlPingURL() {
        return remoteControlURLFor("/core/Blank.html");
    }

    public String remoteControlDriverURL() {
        return remoteControlURLFor("/selenium-server/driver/");
    }

    public String remoteControlURLFor(String path) {
        return "http://" + host + ":" + port + path;
    }

    public Response forward(HttpParameters parameters) throws IOException {
        return httpClient.post(remoteControlDriverURL(), parameters);
    }

    public String toString() {
        return "[RemoteControlProxy " + host + ":" + port + "#"
                + sessionInProgress + "]";
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final RemoteControlProxy otherRemoteControl = (RemoteControlProxy) other;
        return host.equals(otherRemoteControl.host)
                && port == otherRemoteControl.port;
    }

    public int hashCode() {
        return (host + port).hashCode();
    }

    public boolean sesssionInProgress() {
        return sessionInProgress;
    }

    public void registerNewSession() {
        if (sessionInProgress) {
            throw new IllegalStateException("Exceeded concurrent session max for " + toString());
        }
        sessionInProgress = true;
    }

    public void unregisterSession() {
        if (!sessionInProgress) {
            throw new IllegalStateException("Unregistering session on an idle remote control : " + toString());
        }
        sessionInProgress = false;
    }

    public boolean canHandleNewSession() {
        return  !sesssionInProgress();
    }

	public boolean unreliable() {
        final Response response;

        try {
            LOGGER.debug("Polling Remote Control at " + host + ":" + port);
            response = httpClient.get(remoteControlPingURL());
        } catch (Exception e) {
            LOGGER.warn("Remote Control at " + host + ":" + port + " is unresponsive");
            return true;
        }
        if (response.statusCode() != 200) {
            LOGGER.warn("Remote Control at " + host + ":" + port + " did not respond correctly");
            return true;
        }
        return false;
    }

}
