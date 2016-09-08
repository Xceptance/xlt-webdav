package com.xceptance.xlt.webdav.actions;

import org.junit.Assert;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDAVAction;
import com.xceptance.xlt.webdav.validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Checks if a resources path exists by using WebDAV <code>HEAD</code> by sardine.exists. Can be used by relative path
 * or by a resource object provided by previously performed ListResources actions to verify an expectation.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDAVExists extends AbstractWebDAVAction<WebDAVExists>
{
    // Expectation of resources existence
    private boolean exists;

    // Verification of path validity
    private boolean doesExist = false;

    // the path to check
    private final String url;
    
    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            Resources relative source path related to your webdav directory
     * @param exists
     *            Expected state of resources existence
     */
    public WebDAVExists(final String relativePath, final boolean exists)
    {
        super();
        
        this.exists = exists;
        this.url = getURL(relativePath);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param src
     *            Source DavResource object to perform this action
     * @param exists
     *            Expected state of resources existence
     */
    public WebDAVExists(final DavResource src, final boolean exists)
    {
        super();

        this.exists = exists;
        this.url = getURL(src); 
    }

    @Override
    public void preValidate() throws Exception
    {
        WebDavActionValidator.validate(this);
    }

    @Override
    protected void execute() throws Exception
    {
        // Responds http 404 in case of a non existing resource without SardineException
        this.doesExist = getSardine().exists(url);
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Verify: Resource expectations
    	if (this.exists)
    	{
    		Assert.assertTrue("The resource does not exist", this.doesExist);
    	}
    	else
    	{
    		Assert.assertFalse("The resource does exist", this.doesExist);
    	}    	

    	// Verify: check operation succeeded -> 200, 404
        ResponseCodeValidator.validate(getHttpResponseCode(), this.exists ? 200 : 404);
    }

}