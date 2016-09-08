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
import com.xceptance.xlt.api.engine.RequestData;
import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.webdav.util.WebDAVContext;

/**
 * Basic webdav action.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public abstract class AbstractWebDAVAction<T> extends AbstractAction
{
    // Sardine client returned by SardineFactory
    private Sardine sardine;

    // Related user name for credentials
    private String userName;

    // Related user name for credentials
    private String userPassword;

    // Related host name to perform actions
    private String hostName;

    // Related webdav directory relative to host name
    private String webDAVPath;

    // Exception message text if thrown
    private String exMessage;

    // Http response code
    private int httpResponseCode = -1;

    // Http content type
    private String httpContentType;

    // Request data for analytic purpose
    private RequestData requestData;

    // Previous performed action
    private AbstractWebDAVAction previousWebDAVAction;
    
    // the DavResource returned by this action, if any
    private DavResource response;

    /**
     * Initial setup method for first connect to consume the basics
     * datapoints
     */
    protected AbstractWebDAVAction(String hostname, String webDAVPath, String username, String password)
    {
    	super(null, null);
    	
    	this.hostName = hostname;
    	this.webDAVPath = webDAVPath;
    	this.userName = username;
    	this.userPassword = password;
    	
        initialisation();
        WebDAVContext.setActiveAction(this);
    }
    
    /**
     * Basic constructor for actions without specific named results
     */
    public AbstractWebDAVAction()
    {
        super(WebDAVContext.getActiveAction(), null);

        // redundant initialisation tasks
        this.initialisation();

        WebDAVContext.setActiveAction(this);
    }

    /**
     * Basic constructor for actions with specific named results
     *
     * @param timerName
     *            Name which should be used for this action in results
     */
    public AbstractWebDAVAction(String timerName)
    {
        super(WebDAVContext.getActiveAction(), timerName);

        // redundant initialisation tasks
        this.initialisation();

        WebDAVContext.setActiveAction(this);
    }

    /**
     * Does redundant tasks during the construction
     */
    private void initialisation()
    {
        // Set previously performed action if exists, otherwise NULL
        this.previousWebDAVAction = WebDAVContext.getActiveAction();

        // Assume information from previous action
        if (this.previousWebDAVAction != null)
        {
            this.sardine = this.previousWebDAVAction.getSardine();
            this.userName = this.previousWebDAVAction.getUserName();
            this.userPassword = this.previousWebDAVAction.getUserPassword();
            this.hostName = this.previousWebDAVAction.getHostName();
            this.webDAVPath = this.previousWebDAVAction.getWebDAVPath();
        }

        // Provide sardine client if necessary
        if (this.sardine == null)
        {
            // Creates configured sardine client
            this.sardine = new CustomizedSardineImpl();
            this.getSardine().setCredentials(userName, userPassword);
            
            // ok, let us see if gzip is desired
            if (XltProperties.getInstance().getProperty("com.xceptance.xlt.http.gzip", true))
            {
            	this.sardine.enableCompression();
            }
            else
            {
            	this.sardine.disableCompression();
            }
        }

        // Initialisation of response values
        this.exMessage = null; // no Exception
        this.httpResponseCode = -1; // Not executed yet
        this.httpContentType = null; // No Response
    }

    /**
     * Sets a timer name in a pseudo functional way to avoid
     * too many constructors. There is already a method setTimerName
     * but it is void, hence not simple to use.
     * 
     * @param newTimerName new time name
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
    public AbstractWebDAVAction getPreviousAction()
    {
        return this.previousWebDAVAction;
    }

    /**
     * @return Related sardine client
     */
    public Sardine getSardine()
    {
        return this.sardine;
    }

    /**
     * @return Credential user name
     */
    public String getUserName()
    {
        return this.userName;
    }

    /**
     * @return Credential user password
     */
    public String getUserPassword()
    {
        return this.userPassword;
    }

    /**
     * @return Host name of webdav server
     */
    public String getHostName()
    {
        return this.hostName;
    }

    /**
     * Sets server host name
     *
     * @param hostName
     *            Servers host name for example: "http://localhost/"
     */
    private void setHostName(String hostName)
    {
        this.hostName = hostName;
    }

    /**
     * @return Relative path of wendav's home directory at the server
     */
    public String getWebDAVPath()
    {
        return this.webDAVPath;
    }
    
    /**
     * Sets your webdav home directory path if used, otherwise use "" you can also set your favorite path, to shorten
     * your input relativePath's
     *
     * @param webdavDir
     *            Relative webdav home directory related to hostname, for example: "webdav/"
     */
    private void setWebDAVPath(String webDAVPath)
    {
        this.webDAVPath = webDAVPath;
    }
    
    /**
     * Returns the full absolute url of the current state
     * 
     * @param relativePath the relative path to append
     * @return absolute url
     */
	public String getURL(final String relativePath)
    {    	
    	// build us a new valid url
    	final StringBuilder url = new StringBuilder(256);
    	
    	// hostname first, strip /
    	url.append(StringUtils.stripEnd(this.hostName, "/"));
    	
    	final StringBuilder path = new StringBuilder(256); 
    	path.append("/");
    	path.append(StringUtils.strip(this.webDAVPath, "/"));
    	path.append("/");
    	path.append(StringUtils.stripStart(relativePath, "/"));

    	url.append(path);
    	
    	return urlEncode(url.toString());
    }

    /**
     * Returns the full absolute url of the current state
     * 
     * @param resource the dav resource to create the url from
     * @return absolute url
     */
	public String getURL(final DavResource resource)
    {    	
    	// build us a new valid url
    	final StringBuilder url = new StringBuilder(256);
    	
    	// hostname first, strip /
    	url.append(StringUtils.stripEnd(this.hostName, "/"));
    	url.append("/");
    	url.append(StringUtils.stripStart(resource.getPath(), "/"));

    	return urlEncode(url.toString());
    }
	
	/**
	 * Sardine needs encoded urls, but returns decoded, odd, but this requires that
	 * we split the url, encode and put it back together. This routine does 
	 * 
	 * @param path the full url to sanitize
	 * @return an encoded path
	 * @throws MalformedURLException 
	 * @throws URISyntaxException 
	 */
	private String urlEncode(final String url)
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
     * @throws IOException 
     */
    public void releaseClient() throws IOException
    {
        // Clean shutdown of sardine client
        try
        {
            this.sardine.shutdown();
        }
        finally
        {
            sardine = null;
        }
    }

    /**
     * Sets http response code to this action Used by WebdavContext to log responds
     *
     * @param httpResponseCode
     *            Responded http response code
     */
    public void setHttpResponseCode(int httpResponseCode)
    {
        this.httpResponseCode = httpResponseCode;
    }

    /**
     * Sets content type to this action Used by WebdavContext to log responds
     *
     * @param httpContentType
     *            Responded content type
     */
    public void setHttpContentType(String httpContentType)
    {
        this.httpContentType = httpContentType;
    }

    /**
     * Sets thrown exception message to this action Used by WebdavContext to log exceptions while executing the action
     *
     * @param exMessage
     *            Thrown exception message
     */
    public void setExMessage(String exMessage)
    {
        this.exMessage = exMessage;
    }

    /**
     * Use this to release DavResource objects after performing an action
     */
    public void free()
    {
        this.response = null;
    }

    /**
     * Returns the DavResource source object that came back from an operation.
     * No all operations return these!
     *
     * @return DavResource response object
     */
    public DavResource getResponse()
    {
        return this.response;
    }

	/**
	 * Returns the last response code. Will return -1 if no execution has 
	 * taken place so far.
	 * 
	 * @return the response code, -1 if no execution has taken place
	 */
	public int getHttpResponseCode() 
	{
		return httpResponseCode;
	}

    
}