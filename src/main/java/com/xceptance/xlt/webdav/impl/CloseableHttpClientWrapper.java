package com.xceptance.xlt.webdav.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.RequestLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import com.xceptance.xlt.api.engine.RequestData;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.engine.RequestExecutionContext;
import com.xceptance.xlt.engine.socket.SocketStatistics;
import com.xceptance.xlt.engine.socket.XltSockets;
import com.xceptance.xlt.webdav.util.WebDavContext;

/**
 * A wrapper around a {@link CloseableHttpClient} object that delegates all method calls to the wrapped object, but
 * additionally logs any HTTP request details to XLT. This instrumentation is done in the
 * {@link #doExecute(HttpHost, HttpRequest, HttpContext)} method.
 */
@SuppressWarnings("deprecation")
public class CloseableHttpClientWrapper extends CloseableHttpClient
{
    /**
     * The method object to access the "doExecute" method of the wrapped {@link CloseableHttpClient} instance. This
     * method is protected, so the only way to delegate the call to it is using reflection.
     */
    private static final Method doExecuteMethod;

    static
    {
        try
        {
            // get the "doExecute" method object and make it accessible
            final Class<?> internalHttpClientClass = Class.forName("org.apache.http.impl.client.InternalHttpClient");
            doExecuteMethod = internalHttpClientClass.getDeclaredMethod("doExecute", HttpHost.class, HttpRequest.class, HttpContext.class);
            doExecuteMethod.setAccessible(true);
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex)
        {
            throw new RuntimeException("Failed to access method", ex);
        }

        // initialize the network instrumentation layer
        XltSockets.initialize();
    }

    /**
     * The wrapped instance.
     */
    private final CloseableHttpClient httpClient;

    /**
     * Creates a new wrapper object around the passed httpClient instance.
     *
     * @param httpClient
     *            the instance to wrap
     */
    public CloseableHttpClientWrapper(final CloseableHttpClient httpClient)
    {
        this.httpClient = httpClient;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException
    {
        httpClient.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClientConnectionManager getConnectionManager()
    {
        return httpClient.getConnectionManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HttpParams getParams()
    {
        return httpClient.getParams();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CloseableHttpResponse doExecute(final HttpHost target, final HttpRequest request, final HttpContext context)
        throws IOException
    {
        final AbstractWebDavAction<?> action = WebDavContext.getActiveAction();

        // initialize the request data object which will carry all request/response details
        final RequestData requestData = new RequestData(action.getTimerName());

        try
        {
            // reset the request context (incl. network instrumentation and DNS layer) before executing the request
            RequestExecutionContext.getCurrent().reset();

            // now invoke doExceute() reflectively
            final CloseableHttpResponse response = invokeDoExecute(target, request, context);

            // ensure that the response is read into memory completely
            final HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                response.setEntity(new BufferedHttpEntity(entity));
            }

            // get the status code
            final int responseCode = response.getStatusLine().getStatusCode();

            // get the response content type if available
            final Header contentTypeHeader = response.getFirstHeader("Content-Type");
            final String responseContentType = (contentTypeHeader == null) ? "" : contentTypeHeader.getValue();

            // update the request data object
            requestData.setResponseCode(responseCode);
            requestData.setContentType(responseContentType);
            requestData.setFailed(responseCode >= 500);

            // update the current action
            action.setStatusCode(responseCode);
            action.setResponseContentType(responseContentType);

            // finally return the response
            return response;
        }
        catch (final IOException ex)
        {
            // update the request data object
            requestData.setFailed(true);

            // update the current action
            action.setException(ex);

            // rethrow the exception
            throw ex;
        }
        finally
        {
            //
            // gather the remaining request/response details and complete the request data object
            //

            // set the elapsed time
            requestData.setRunTime();

            // set any request info
            final RequestLine requestLine = request.getRequestLine();

            requestData.setHttpMethod(requestLine.getMethod());
            requestData.setUrl(requestLine.getUri());

            // set network statistics
            final SocketStatistics socketStatistics = RequestExecutionContext.getCurrent().getSocketMonitor().getSocketStatistics();

            requestData.setBytesSent(socketStatistics.getBytesSent());
            requestData.setBytesReceived(socketStatistics.getBytesReceived());
            requestData.setDnsTime(socketStatistics.getDnsLookupTime());
            requestData.setConnectTime(socketStatistics.getConnectTime());
            requestData.setSendTime(socketStatistics.getSendTime());
            requestData.setServerBusyTime(socketStatistics.getServerBusyTime());
            requestData.setReceiveTime(socketStatistics.getReceiveTime());
            requestData.setTimeToFirstBytes(socketStatistics.getTimeToFirstBytes());
            requestData.setTimeToLastBytes(socketStatistics.getTimeToLastBytes());

            // finally log the request data object
            Session.getCurrent().getDataManager().logDataRecord(requestData);
        }
    }

    /**
     * Invokes the {@link #doExecute(HttpHost, HttpRequest, HttpContext)} method on the wrapped
     * {@link CloseableHttpClient} object.
     *
     * @param target
     *            the HTTP target
     * @param request
     *            the HTTP request
     * @param context
     *            the HTTP context
     * @return the HTTP response
     * @throws IOException
     *             if anything goes wrong
     */
    private CloseableHttpResponse invokeDoExecute(final HttpHost target, final HttpRequest request, final HttpContext context)
        throws IOException
    {
        try
        {
            return (CloseableHttpResponse) doExecuteMethod.invoke(httpClient, target, request, context);
        }
        catch (final Exception ex)
        {
            if (ex instanceof InvocationTargetException)
            {
                // unwrap causing exception and throw it directly
                final Throwable cause = ex.getCause();

                if (cause instanceof IOException)
                {
                    throw (IOException) cause;
                }
            }
            else if (ex instanceof RuntimeException)
            {
                // throw it directly
                throw (RuntimeException) ex;
            }

            // throw any remaining exception wrapped in a RuntimeException
            throw new RuntimeException("Failed to invoke method", ex);
        }
    }
}
