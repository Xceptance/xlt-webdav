package com.xceptance.xlt.webdav.impl;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ProxySelector;
import java.text.MessageFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.github.sardine.Version;
import com.github.sardine.impl.SardineImpl;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.engine.XltDnsResolver;

/**
 * A sub class of {@link SardineImpl} that additionally logs the details of any HTTP request performed.
 * <p>
 * The functionality of the super class is not altered in any way. It is still fully responsible to execute the actual
 * WebDAV operations. However, in order to get access to all the request and response details of the underlying HTTP
 * communication, Sardine's HTTP client will be wrapped.
 *
 * @see CloseableHttpClientWrapper
 */
public class CustomizedSardineImpl extends SardineImpl
{
    /**
     * The field object to access the "client" field in {@link SardineImpl}. Since this field is private and cannot be
     * accessed otherwise, we have to use reflection.
     */
    private static final Field clientField;

    static
    {
        try
        {
            // get the "client" field object and make it accessible
            clientField = SardineImpl.class.getDeclaredField("client");
            clientField.setAccessible(true);
        }
        catch (SecurityException | NoSuchFieldException ex)
        {
            throw new RuntimeException("Failed to access field", ex);
        }
    }

    /**
     * Creates a new {@link CustomizedSardineImpl} object.
     */
    public CustomizedSardineImpl()
    {
        super();
    }

    /**
     * Creates a new {@link CustomizedSardineImpl} object and initializes it with the given parameters.
     *
     * @param builder
     *            a custom HTTP client builder
     * @param username
     *            the user name (for Basic authentication)
     * @param password
     *            the password (for Basic authentication)
     */
    public CustomizedSardineImpl(final HttpClientBuilder builder, final String username, final String password)
    {
        super(builder, username, password);
    }

    /**
     * Creates a new {@link CustomizedSardineImpl} object and initializes it with the given parameters.
     *
     * @param builder
     *            a custom HTTP client builder
     */
    public CustomizedSardineImpl(final HttpClientBuilder builder)
    {
        super(builder);
    }

    /**
     * Creates a new {@link CustomizedSardineImpl} object and initializes it with the given parameters.
     *
     * @param username
     *            the user name (for Basic authentication)
     * @param password
     *            the password (for Basic authentication)
     * @param selector
     *            a custom proxy selector
     */
    public CustomizedSardineImpl(final String username, final String password, final ProxySelector selector)
    {
        super(username, password, selector);
    }

    /**
     * Creates a new {@link CustomizedSardineImpl} object and initializes it with the given parameters.
     *
     * @param username
     *            the user name (for Basic authentication)
     * @param password
     *            the password (for Basic authentication)
     */
    public CustomizedSardineImpl(final String username, final String password)
    {
        super(username, password);
    }

    /**
     * Creates a new {@link CustomizedSardineImpl} object and initializes it with the given parameters.
     *
     * @param bearerAuth
     *            the Bearer authorization header value
     */
    public CustomizedSardineImpl(final String bearerAuth)
    {
        super(bearerAuth);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpClientBuilder configure(final ProxySelector selector, final CredentialsProvider credentials)
    {
        // let the super class create a HttpClientBuilder instance
        final HttpClientBuilder builder = super.configure(selector, credentials);

        // now additionally set a custom DNS resolver to get DNS resolution times
        builder.setDnsResolver(new XltDnsResolver());

        // configure a decent user agent name
        builder.setUserAgent(MessageFormat.format("Sardine/{0} (Xceptance Load Test, XLT {1}, WebDAV)",
                                                  Version.getImplementation(),
                                                  XltProperties.getInstance().getVersion()));

        return builder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> T execute(final HttpRequestBase request, final ResponseHandler<T> responseHandler) throws IOException
    {
        // check/wrap the client in case it has been recreated in the meantime
        wrapHttpClientIfNeeded();

        return super.execute(request, responseHandler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected HttpResponse execute(final HttpRequestBase request) throws IOException
    {
        // check/wrap the client in case it has been recreated in the meantime
        wrapHttpClientIfNeeded();

        return super.execute(request);
    }

    /**
     * Ensures that the {@link CloseableHttpClient} instance of the super class is properly wrapped in a
     * {@link CloseableHttpClientWrapper}. Otherwise we won't get any request details.
     */
    private void wrapHttpClientIfNeeded()
    {
        try
        {
            final CloseableHttpClient client = (CloseableHttpClient) clientField.get(this);

            if (client instanceof CloseableHttpClientWrapper)
            {
                // already wrapped -> nothing to do
            }
            else
            {
                // wrap it and set the wrapper at the super class
                clientField.set(this, new CloseableHttpClientWrapper(client));
            }
        }
        catch (final IllegalAccessException ex)
        {
            throw new RuntimeException("Failed to access field", ex);
        }
    }
}
