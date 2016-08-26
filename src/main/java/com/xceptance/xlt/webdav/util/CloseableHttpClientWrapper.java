package com.xceptance.xlt.webdav.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public class CloseableHttpClientWrapper extends CloseableHttpClient
{
    private static final Method doExecuteMethod;

    static
    {
        try
        {
            Class<?> internalHttpClientClass = Class.forName("org.apache.http.impl.client.InternalHttpClient");
            doExecuteMethod = internalHttpClientClass.getDeclaredMethod("doExecute", HttpHost.class, HttpRequest.class, HttpContext.class);
            doExecuteMethod.setAccessible(true);
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex)
        {
            throw new RuntimeException("Failed to access method", ex);
        }
    }

    private final CloseableHttpClient httpClient;

    /**
     * @param httpClient
     */
    public CloseableHttpClientWrapper(CloseableHttpClient httpClient)
    {
        this.httpClient = httpClient;
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws IOException
    {
        httpClient.close();
    }

    /**
     * {@inheritDoc}
     */
    public ClientConnectionManager getConnectionManager()
    {
        return httpClient.getConnectionManager();
    }

    /**
     * {@inheritDoc}
     */
    public HttpParams getParams()
    {
        return httpClient.getParams();
    }

    /**
     * {@inheritDoc}
     */
    protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException
    {
        WebdavContext.getActiveAction().initDataRecord();

        try
        {
            return invokeDoExecute(target, request, context);
        }
        catch (IOException ex)
        {
            // log event
            WebdavContext.getActiveAction().setExMessage(ex.getMessage());
            throw ex;
        }
        finally
        {
            WebdavContext.getActiveAction().doDataRecord();
        }
    }

    private CloseableHttpResponse invokeDoExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException
    {
        try
        {
            return (CloseableHttpResponse) doExecuteMethod.invoke(httpClient, target, request, context);
        }
        catch (Exception ex)
        {
            if (ex instanceof InvocationTargetException)
            {
                // unwrap causing exception and throw it directly
                Throwable cause = ex.getCause();

                if (cause instanceof IOException)
                {
                    throw (IOException) cause;
                }
            }

            // throw exception wrapped into a RuntimeException
            throw new RuntimeException("Failed to invoke method", ex);
        }
    }
}