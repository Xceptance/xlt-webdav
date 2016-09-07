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
public class CheckResourcePath extends AbstractWebDAVAction
{
    // Expectation of resources existence
    private boolean exists;

    // Verification of path validity
    private boolean doesExist = false;

    // the path to check
    private final String path;
    
    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            Resources relative source path related to your webdav directory
     * @param exists
     *            Expected state of resources existence
     */
    public CheckResourcePath(String relativePath, boolean exists)
    {
        super();
        
        this.exists = exists;
        path = getAbsoluteURL(relativePath);
    }

    /**
     * Action with specific name listed in the results, based on a path
     *
     * @param timerName
     *            Is used for naming this action in results
     * @param relativePath
     *            Resources relative source path related to your webdav directory
     * @param expectation
     *            Expected state of resources existence
     */
    public CheckResourcePath(String timerName, String relativePath, boolean exists)
    {
        super(timerName);

        this.exists = exists;
        path = getAbsoluteURL(relativePath);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param resourceSRC
     *            Source DavResource object to perform this action
     * @param expectation
     *            Expected state of resources existence
     */
    public CheckResourcePath(DavResource davResource, boolean exists)
    {
        super();

        this.exists = exists;
        path = davResource.getHref().toString(); 
    }

    /**
     * Action with specific name listed in the results, based on a resource object
     *
     * @param timerName
     *            Is used for naming this action in results
     * @param resourceSRC
     *            Source DavResource object to perform this action
     * @param expectation
     *            Expected state of resources existence
     */
    public CheckResourcePath(String timerName, DavResource davResource, boolean exists)
    {
        super(timerName);
        
        this.exists = exists;
        path = davResource.getHref().toString();
    }

    @Override
    public void preValidate() throws Exception
    {
        WebDavActionValidator.validate(this);
        SourceDavResourceValidator.validate(this);
    }

    @Override
    protected void execute() throws Exception
    {
        // Responds http 404 in case of a non existing resource without SardineException
        this.doesExist = getSardine().exists(path);
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