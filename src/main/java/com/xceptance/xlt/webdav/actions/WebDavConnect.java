package com.xceptance.xlt.webdav.actions;

import org.junit.Assert;

import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;

/**
 * Checks if a resources path exists by using WebDAV <code>HEAD</code> by sardine.exists. Can be used by relative path
 * or by a resource object provided by previously performed ListResources actions to verify an expectation.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavConnect extends AbstractWebDavAction<WebDavConnect>
{
    /**
     * Whether the WebDAV base directory indeed exists.
     */
    private boolean doesExist;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param hostName
     *            the host name and the protocol to use
     * @param webDavPath
     *            the webDAVPath, this is mostly the root directory
     * @param userName
     *            the user name if authorization is required, can be null
     * @param password
     *            the password if authorization is required, can be null
     */
    public WebDavConnect(final String hostName, final String webDavPath, final String userName, final String password)
    {
        super(hostName, webDavPath, userName, password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preValidate()
    {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws Exception
    {
        doesExist = getSardine().exists(getUrl(""));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate()
    {
        Assert.assertTrue("Initial connect and verification failed.", doesExist);
    }
}
