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
     * Sardine client returned by SardineFactory
     */
    private Sardine sardine;

    /**
     * Related user name for credentials
     */
    private String userName;

    /**
     * Related user name for credentials
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
     * Exception message text if thrown
     */
    private Exception exception;

    /**
     * Http response code
     */
    private int statusCode = -1;

    /**
     * Http content type
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
     * @return Related sardine client
     */
    public Sardine getSardine()
    {
        return sardine;
    }

    /**
     * @return Credential user name
     */
    public String getUserName()
    {
        return userName;
    }

    /**
     * @return Credential user password
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
     * Returns the full absolute url of the current state
     *
     * @param relativePath
     *            the relative path to append
     * @return absolute url
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
     * Sardine needs encoded urls, but returns decoded, odd, but this requires that we split the url, encode and put it
     * back together. This routine does
     *
     * @param path
     *            the full url to sanitize
     * @return an encoded path
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    private String encodeUrl(final String url)
    {
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
     * Sardine client shutdown and release (implicit given by WebdavContext's clean method) which must to be used at the
     * end of your testcase
     *
     * @throws IOException
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
     * Returns the last response code. Will return -1 if no execution has taken place so far.
     *
     * @return the response code, -1 if no execution has taken place
     */
    public int getStatusCode()
    {
        return statusCode;
    }

    /**
     * Sets http response code to this action Used by WebdavContext to log responds
     *
     * @param statusCode
     *            Responded http response code
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
     *
     * @param exception
     *            Thrown exception message
     */
    public void setException(final Exception exception)
    {
        this.exception = exception;
    }
}
