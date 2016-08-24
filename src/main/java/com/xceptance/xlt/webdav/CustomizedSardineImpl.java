package com.xceptance.xlt.webdav;

import java.io.IOException;
import java.net.ProxySelector;

import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.github.sardine.impl.SardineImpl;
import com.xceptance.common.lang.ReflectionUtils;
import com.xceptance.xlt.engine.XltDnsResolver;

/**
 * A sample test case.
 */
public class CustomizedSardineImpl extends SardineImpl
{
    /**
     * 
     */
    public CustomizedSardineImpl()
    {
        super();
    }

    /**
     * @param builder
     * @param username
     * @param password
     */
    public CustomizedSardineImpl(HttpClientBuilder builder, String username, String password)
    {
        super(builder, username, password);
    }

    /**
     * @param builder
     */
    public CustomizedSardineImpl(HttpClientBuilder builder)
    {
        super(builder);
    }

    /**
     * @param username
     * @param password
     * @param selector
     */
    public CustomizedSardineImpl(String username, String password, ProxySelector selector)
    {
        super(username, password, selector);
    }

    /**
     * @param username
     * @param password
     */
    public CustomizedSardineImpl(String username, String password)
    {
        super(username, password);
    }

    /**
     * @param bearerAuth
     */
    public CustomizedSardineImpl(String bearerAuth)
    {
        super(bearerAuth);
    }

    protected HttpClientBuilder configure(ProxySelector selector, CredentialsProvider credentials)
    {
        // let the super class create a HttpClientBuilder instance
        HttpClientBuilder builder = super.configure(selector, credentials);

        // set a custom DNS resolver
        builder.setDnsResolver(new XltDnsResolver());

        return builder;
    }

    protected <T> T execute(HttpRequestBase request, ResponseHandler<T> responseHandler) throws IOException
    {
        wrapHttpClientIfNeeded();
        return super.execute(request, responseHandler);
    }

    protected HttpResponse execute(HttpRequestBase request) throws IOException
    {
        wrapHttpClientIfNeeded();
        return super.execute(request);
    }

    private void wrapHttpClientIfNeeded()
    {
        CloseableHttpClient client = ReflectionUtils.readInstanceField(this, "client");

        if (client instanceof CloseableHttpClientWrapper)
        {
            // already wrapped -> nothing to do
        }
        else
        {
            // wrap it
            client = new CloseableHttpClientWrapper(client);
            ReflectionUtils.writeInstanceField(this, "client", client);
        }
    }
}
