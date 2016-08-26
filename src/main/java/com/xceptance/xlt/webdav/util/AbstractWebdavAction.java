package com.xceptance.xlt.webdav.util;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.impl.SardineImpl;
import com.xceptance.common.lang.ReflectionUtils;
import com.xceptance.xlt.api.actions.AbstractAction;
import com.xceptance.xlt.api.engine.RequestData;
import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.engine.XltDnsResolver;
import com.xceptance.xlt.engine.util.socket.SocketStatistics;
import com.xceptance.xlt.engine.util.socket.XltSockets;

import org.apache.http.*;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;

import java.io.IOException;
import java.net.ProxySelector;

/**
 * Basic webdav action.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public abstract class AbstractWebdavAction extends AbstractAction
{
    // Sardine client returned by SardineFactory
    protected Sardine sardine;

    // Related user name for credentials
    protected String userName;

    // Related user name for credentials
    protected String userPassword;

    // Related host name to perform actions
    protected String hostName;

    // Related webdav directory relative to host name
    protected String webdavDir;

    // Exception message text if thrown
    protected String exMessage;

    // Http response code
    protected int httpResponseCode;

    // Http content type
    protected String httpContentType;

    // Paths to check related resources
    // if two paths are in use (source and destination), this mirrors the destination path where the
    // http request is sent to
    protected String relativePath;

    protected String path;

    // Source resource to perform action
    // (lternatively to path description of its source)
    protected DavResource resourceSRC;

    protected boolean davResourceUsage;

    // Request data for analytic purpose
    protected RequestData requestData;

    // Previous performed action
    protected AbstractWebdavAction previousWebdavAction;

    /**
     * Basic constructor for actions without specific named results
     */
    public AbstractWebdavAction()
    {
        super(WebdavContext.getActiveAction(), null);

        // redundant initialisation tasks
        this.initialisation();

        WebdavContext.setActiveAction(this);
    }

    /**
     * Basic constructor for actions with specific named results
     *
     * @param timerName
     *            Name which should be used for this action in results
     */
    public AbstractWebdavAction(String timerName)
    {
        super(WebdavContext.getActiveAction(), timerName);

        // redundant initialisation tasks
        this.initialisation();

        WebdavContext.setActiveAction(this);
    }

    /**
     * Does redundant tasks during the construction
     */
    private void initialisation()
    {
        // Set previously performed action if exists, otherwise NULL
        this.previousWebdavAction = WebdavContext.getActiveAction();

        // Assume information from previous action
        if (this.previousWebdavAction != null)
        {
            this.sardine = this.previousWebdavAction.getSardine();
            this.userName = this.previousWebdavAction.getUserName();
            this.userPassword = this.previousWebdavAction.getUserPassword();
            this.hostName = this.previousWebdavAction.getHostName();
            this.webdavDir = this.previousWebdavAction.getWebdavDir();
        }
        else
        {
            // Initialisation to avoid NullPointerException and mismatching
            this.hostName = "";
            this.webdavDir = "";
        }

        // Provide sardine client if necessary
        if (this.sardine == null)
        {
            // Creates configured sardine client
            this.sardine = new SardineImpl()
            {
                protected HttpClientBuilder configure(ProxySelector selector, CredentialsProvider credentials)
                {
                    // Let the super class create a configured HttpClientBuilder instance
                    HttpClientBuilder builder = super.configure(selector, credentials);

                    // Sets a custom request executor which logs the server responses
                    builder.setRequestExecutor(new HttpRequestExecutor()
                    {
                        @Override
                        public HttpResponse execute(HttpRequest request, HttpClientConnection conn, HttpContext context)
                            throws IOException, HttpException
                        {

                            // Only executed while connection is not refused!
                            try
                            {
                                // Initializes socket monitoring and data logging
                                WebdavContext.getActiveAction().initDataRecord();

                                // Call the execution of the request
                                HttpResponse response = super.execute(request, conn, context);

                                HttpEntity entity = response.getEntity();
                                if (entity != null)
                                {
                                    response.setEntity(new BufferedHttpEntity(entity));
                                }
                                // Logs response code and content type inside the active action
                                WebdavContext.getActiveAction().setHttpResponseCode(response.getStatusLine().getStatusCode());

                                Header contentTypeHeader = response.getFirstHeader("Content-Type");
                                if (contentTypeHeader != null)
                                {
                                    WebdavContext.getActiveAction().setHttpContentType((contentTypeHeader.getValue().split(";"))[0]);
                                }

                                return response;
                            }
                            catch (IOException | HttpException ex)
                            {
                                // Logs exception messages inside the active action
                                WebdavContext.getActiveAction().setExMessage(ex.getMessage());
                                throw ex;
                            }
                            finally
                            {
                                // Finalizes data logging and sends the result to DataManager
                                WebdavContext.getActiveAction().doDataRecord();
                            }
                        }
                    });

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
            };
        }

        // Initialisation of response values
        this.exMessage = ""; // no Exception
        this.httpResponseCode = 0; // No Response
        this.httpContentType = ""; // No Response
    }

    /**
     * @return Previously performed action
     */
    public AbstractWebdavAction getPreviousAction()
    {
        return this.previousWebdavAction;
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
     * Sets user credentials to this actions and sardine client Will also be used from all following actions by taking
     * use of WebdavContext and the constructor of the following action
     *
     * @param userName
     *            Credential user name
     * @param userPassword
     *            Credential user password
     */
    public void setCredentials(String userName, String userPassword)
    {
        this.userName = userName;
        this.userPassword = userPassword;
        this.getSardine().setCredentials(userName, userPassword);
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
    public void setHostName(String hostName)
    {
        this.hostName = hostName;
        this.path = this.hostName + this.webdavDir + this.relativePath;
    }

    /**
     * @return Relative path of wendav's home directory at the server
     */
    public String getWebdavDir()
    {
        return this.webdavDir;
    }

    /**
     * Sets your webdav home directory path if used, otherwise use "" you can also set your favorite path, to shorten
     * your input relativePath's
     *
     * @param webdavDir
     *            Relative webdav home directory related to hostname, for example: "webdav/"
     */
    public void setWebdavDir(String webdavDir)
    {
        this.webdavDir = webdavDir;
        this.path = this.hostName + this.webdavDir + this.relativePath;
    }

    /**
     * Gets the relative path of this action
     *
     * @return Relative path of performed action to reuse it in following actions
     */
    public String getUsedRelativePath()
    {
        return this.relativePath;
    }

    public String getUsedPath()
    {
        return this.path;
    }

    /**
     * Initializes the socket monitor to get analytic data
     */
    protected void initDataRecord()
    {
        this.requestData = new RequestData(WebdavContext.getActiveAction().getClass().getSimpleName());
        XltSockets.getSocketMonitor().reset();
    }

    /**
     * Logs all request related data for analytic purpose
     */
    protected void doDataRecord()
    {
        // Transfers analytic data from socket monitor to request data
        this.requestData.setRunTime();

        // set path
        this.requestData.setUrl(this.path);

        // Getting the socket monitor
        SocketStatistics socketStatistics = XltSockets.getSocketMonitor().getSocketStatistics();

        // setConnectTime
        this.requestData.setConnectTime(socketStatistics.getConnectTime());

        // setReceiveTime
        this.requestData.setReceiveTime(socketStatistics.getReceiveTime());

        // setBytesReceived
        this.requestData.setBytesReceived(socketStatistics.getBytesReceived());

        // setSendTime
        this.requestData.setSendTime(socketStatistics.getSendTime());

        // setBytesSent
        this.requestData.setBytesSent(socketStatistics.getBytesSent());

        // setServerBusyTime
        this.requestData.setServerBusyTime(socketStatistics.getServerBusyTime());

        // setTimeToFirstBytes
        this.requestData.setTimeToFirstBytes(socketStatistics.getTimeToFirstBytes());

        // setTimeToLastBytes
        this.requestData.setTimeToLastBytes(socketStatistics.getTimeToLastBytes());

        // setHttpResponseCode
        this.requestData.setResponseCode(this.httpResponseCode);

        // setHttpContentType
        this.requestData.setContentType(this.httpContentType);

        // setFailed
        if (!this.exMessage.equals("") || this.httpResponseCode == 0 || this.httpResponseCode >= 500)
        {
            // set failed
            this.requestData.setFailed(true);
            // log event
            if (!exMessage.equals(""))
                Session.getCurrent().getDataManager().logEvent("Exception", this.exMessage);
        }

        // Log data object
        Session.getCurrent().getDataManager().logDataRecord(this.requestData);
    }

    /**
     * Sardine client shutdown and release (implicit given by WebdavContext's clean method) which must to be used at the
     * end of your testcase
     */
    public void releaseClient()
    {
        // Clean shutdown of sardine client
        try
        {
            this.sardine.shutdown();
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
    public void freeResourceSRC()
    {
        this.resourceSRC = null;
    }

    /**
     * Gets a flag of DavResource usage inside the action Set this true if you build your own action using DavResources
     * as source and the WebDavActionValidator
     */
    public boolean getDavResourceUsage()
    {
        return this.davResourceUsage;
    }

    /**
     * Returns the DavResource source object
     *
     * @return DavResource source object
     */
    public DavResource getResourceSRC()
    {
        return this.resourceSRC;
    }
}