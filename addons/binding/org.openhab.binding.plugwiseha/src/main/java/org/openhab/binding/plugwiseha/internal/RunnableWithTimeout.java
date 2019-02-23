package org.openhab.binding.plugwiseha.internal;

import java.util.concurrent.TimeoutException;

/**
 * Provides an interface for a delegate that can throw a timeout
 *
 * @author Kaj Visser - Initial contribution
 *
 */
public interface RunnableWithTimeout {

    public abstract void run() throws TimeoutException;

}
