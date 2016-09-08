package com.xceptance.xlt.webdav.actions;

import org.junit.Assert;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDAVAction;
import com.xceptance.xlt.webdav.validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.SourceDavResourceValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Checks if a resources path exists by using WebDAV <code>HEAD</code> by sardine.exists. Can be used by relative path
 * or by a resource object provided by previously performed ListResources actions to verify an expectation.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDAVConnect extends AbstractWebDAVAction
{
	private boolean doesExist = false;
	
    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param hostname the hostname and the protocol to use
     * @param webDAVPath the webDAVPath, this is mostly the root directory
     * @param username the user name if authorization is required, can be null
     * @param password the password if authorization is required, can be null
     */
    public WebDAVConnect(String hostname, String webDAVPath, String username, String password)
    {
        super(hostname, webDAVPath, username, password);
    }

    @Override
    public void preValidate() throws Exception
    {
    }

    @Override
    protected void execute() throws Exception
    {
        // Responds http 404 in case of a non existing resource without SardineException
        this.doesExist = getSardine().exists(getAbsoluteURL(""));
    }

    @Override
    protected void postValidate() throws Exception
    {
    	Assert.assertTrue("Initial connect and verification failed.", doesExist);
    }

}