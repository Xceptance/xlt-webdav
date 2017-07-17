package com.xceptance.xlt.webdav.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.xceptance.xlt.api.actions.AbstractAction;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.webdav.util.WebDavContext;

/**
 * Basic webdav action.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public abstract class AbstractWebDavAction<T> extends AbstractAction
{
    /**
     * The underlying Sardine client that does all the magic.
     */
    private Sardine sardine;

    /**
     * The user name to use if the server requires authentication.
     */
    private String userName;

    /**
     * The password to use if the server requires authentication.
     */
    private String userPassword;

    /**
     * Related host name to perform actions
     */
    private String hostName;

    /**
     * Related webdav directory relative to host name
     */
    private String webDavPath;

    /**
     * The exception that was thrown on the network layer.
     */
    private Exception exception;

    /**
     * The status code.
     */
    private int statusCode = -1;

    /**
     * The content type of the response.
     */
    private String responseContentType;

    /**
     * Previous performed action
     */
    private AbstractWebDavAction<?> previousWebDavAction;

    /**
     * Initial setup method for first connect to consume the basics datapoints
     */
    protected AbstractWebDavAction(final String hostName, final String webDavPath, final String userName, final String userPassword)
    {
        super(null, null);

        this.hostName = hostName;
        this.webDavPath = webDavPath;
        this.userName = userName;
        this.userPassword = userPassword;

        // Creates configured sardine client
        sardine = new CustomizedSardineImpl();
        sardine.setCredentials(userName, userPassword);

        // ok, let us see if gzip is desired
        if (XltProperties.getInstance().getProperty("com.xceptance.xlt.http.gzip", true))
        {
            sardine.enableCompression();
        }
        else
        {
            sardine.disableCompression();
        }

        WebDavContext.setActiveAction(this);
    }

    /**
     * Basic constructor for actions without specific named results
     */
    public AbstractWebDavAction()
    {
        this(null);
    }

    /**
     * Basic constructor for actions with specific named results
     *
     * @param timerName
     *            Name which should be used for this action in results
     */
    public AbstractWebDavAction(final String timerName)
    {
        super(WebDavContext.getActiveAction(), timerName);

        // get previously performed action if any
        previousWebDavAction = WebDavContext.getActiveAction();

        // Assume information from previous action
        if (previousWebDavAction != null)
        {
            sardine = previousWebDavAction.getSardine();
            userName = previousWebDavAction.getUserName();
            userPassword = previousWebDavAction.getUserPassword();
            hostName = previousWebDavAction.getHostName();
            webDavPath = previousWebDavAction.getWebDavPath();
        }

        WebDavContext.setActiveAction(this);
    }

    /**
     * Sets a timer name in a pseudo functional way to avoid too many constructors. There is already a method
     * setTimerName but it is void, hence not simple to use.
     *
     * @param newTimerName
     *            new time name
     */
    @SuppressWarnings("unchecked")
    public T timerName(final String newTimerName)
    {
        setTimerName(newTimerName);

        return (T) this;
    }

    /**
     * @return Previously performed action
     */
    @Override
    public AbstractWebDavAction<?> getPreviousAction()
    {
        return previousWebDavAction;
    }

    /**
     * Returns the underlying Sardine client that performs the actual communication with the WebDAV server.
     *
     * @return the Sardine client
     */
    public Sardine getSardine()
    {
        return sardine;
    }

    /**
     * Returns the user name used if the server requires authentication.
     *
     * @return the user name
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * Returns the password used if the server requires authentication.
     *
     * @return the password
     */
    public String getUserPassword()
    {
        return userPassword;
    }

    /**
     * @return Host name of webdav server
     */
    public String getHostName()
    {
        return hostName;
    }

    /**
     * @return Relative path of wendav's home directory at the server
     */
    public String getWebDavPath()
    {
        return webDavPath;
    }

    /**
     * Returns an absolute URL for the passed relative path.
     *
     * @param relativePath
     *            the relative path to append
     * @return the resulting absolute URL
     */
    public String getUrl(final String relativePath)
    {
        final StringBuilder url = new StringBuilder(256);

        url.append(StringUtils.stripEnd(hostName, "/"));
        url.append("/");
        url.append(StringUtils.strip(webDavPath, "/"));
        url.append("/");
        url.append(StringUtils.stripStart(relativePath, "/"));

        return encodeUrl(url.toString());
    }

    /**
     * Returns the full absolute url of the current state
     *
     * @param resource
     *            the dav resource to create the url from
     * @return absolute url
     */
    public String getUrl(final DavResource resource)
    {
        final StringBuilder url = new StringBuilder(256);

        url.append(StringUtils.stripEnd(hostName, "/"));
        url.append("/");
        url.append(StringUtils.stripStart(resource.getPath(), "/"));

        return encodeUrl(url.toString());
    }

    /**
     * Encodes special characters in the given URL and returns the encoded URL.
     *
     * @param url
     *            the absolute URL to sanitize
     * @return the encoded URL
     */
    private String encodeUrl(final String url)
    {
        // Sardine needs encoded URLs, but returns decoded ones. Odd, but this requires that we split the URL, encode
        // it, and put it back together.
        try
        {
            final URL u = new URL(url);
            final URI uri = new URI(u.getProtocol(), u.getUserInfo(), u.getHost(), u.getPort(), u.getPath(), u.getQuery(), u.getRef());

            return uri.toURL().toString();
        }
        catch (MalformedURLException | URISyntaxException e)
        {
            // turn this into a runtime exception so we don't have to do the catch it
            // craziness everywhere, bad is bad for the surrounding code
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the underlying {@link Sardine} client and releases any resources held by it.
     *
     * @throws IOException
     *             if anything goes wrong
     */
    public void releaseClient() throws IOException
    {
        try
        {
            sardine.shutdown();
        }
        finally
        {
            sardine = null;
        }
    }

    /**
     * Returns the status code. Will return -1 if no execution has taken place so far.
     *
     * @return the response code, -1 if no execution has taken place
     */
    public int getStatusCode()
    {
        return statusCode;
    }

    /**
     * Sets the status code
     *
     * @param statusCode
     *            the HTTP status code
     */
    public void setStatusCode(final int statusCode)
    {
        this.statusCode = statusCode;
    }

    /**
     * Returns the last response code. Will return -1 if no execution has taken place so far.
     *
     * @return the response code, -1 if no execution has taken place
     */
    public String getResponseContentType()
    {
        return responseContentType;
    }

    /**
     * Sets content type to this action Used by WebdavContext to log responds
     *
     * @param contentType
     *            Responded content type
     */
    public void setResponseContentType(final String contentType)
    {
        this.responseContentType = contentType;
    }

    /**
     * Returns the last response code. Will return -1 if no execution has taken place so far.
     *
     * @return the response code, -1 if no execution has taken place
     */
    public Exception getException()
    {
        return exception;
    }

    /**
     * Sets thrown exception message to this action Used by WebdavContext to log exceptions while executing the action
     * <p>
     * Called automatically by the framework.
     *
     * @param exception
     *            the exception if one was thrown at all
     */
    public void setException(final Exception exception)
    {
        this.exception = exception;
    }
}
