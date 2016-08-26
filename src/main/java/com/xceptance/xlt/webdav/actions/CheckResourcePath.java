package com.xceptance.xlt.webdav.actions;

import org.junit.Assert;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.util.AbstractWebdavAction;
import com.xceptance.xlt.webdav.util.PathBuilder;
import com.xceptance.xlt.webdav.validators.post_validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.pre_validators.ResourceSRCValidator;
import com.xceptance.xlt.webdav.validators.pre_validators.WebdavActionValidator;

/**
 * Checks if a resources path exists by using WebDAV <code>HEAD</code> by sardine.exists. Can be used by relative path
 * or by a resource object provided by previously performed ListResources actions to verify an expectation.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class CheckResourcePath extends AbstractWebdavAction
{
    // Expectation of resources existence
    private boolean expectation;

    // Verification of path validity
    private boolean isValid;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            Resources relative source path related to your webdav directory
     * @param expectation
     *            Expected state of resources existence
     */
    public CheckResourcePath(String relativePath, boolean expectation)
    {
        super();

        this.expectation = expectation;
        this.davResourceUsage = false;

        // Redundant initialisation tasks
        this.initializePath(relativePath);
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
    public CheckResourcePath(String timerName, String relativePath, boolean expectation)
    {
        super(timerName);

        this.expectation = expectation;
        this.davResourceUsage = false;

        // Redundant initialisation tasks
        this.initializePath(relativePath);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param resourceSRC
     *            Source DavResource object to perform this action
     * @param expectation
     *            Expected state of resources existence
     */
    public CheckResourcePath(DavResource resourceSRC, boolean expectation)
    {
        super();

        this.expectation = expectation;
        this.davResourceUsage = true;

        // Redundant initialisation tasks
        this.initializeResource(resourceSRC);
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
    public CheckResourcePath(String timerName, DavResource resourceSRC, boolean expectation)
    {
        super(timerName);

        this.expectation = expectation;
        this.davResourceUsage = true;

        // Redundant initialisation tasks
        this.initializeResource(resourceSRC);
    }

    /**
     * Initializes paths by given strings
     *
     * @param relativePath
     *            Resources relative source path related to your webdav directory
     */
    private void initializePath(String relativePath)
    {
        // initialisation to avoid NullPointerException and mismatching
        this.relativePath = (relativePath == null) ? "" : relativePath;

        // redundant initialisation tasks
        this.initializeFullPath();
    }

    /**
     * Initializes paths by given resource and string
     *
     * @param resourceSRC
     *            Source DavResource object to perform this action
     */
    private void initializeResource(DavResource resourceSRC)
    {
        this.resourceSRC = resourceSRC;
        this.davResourceUsage = true;

        // Assign path from DavResource
        this.relativePath = (this.resourceSRC == null) ? "" : this.resourceSRC.getPath();
        if (!this.relativePath.equals(""))
        {
            // Extract relative path from resource path
            this.relativePath = this.relativePath.substring(this.webdavDir.length() + 1, this.relativePath.length());
        }

        // Redundant initialisation tasks
        this.initializeFullPath();
    }

    /**
     * Initializes full path
     */
    private void initializeFullPath()
    {
        // Initialisation
        this.isValid = false;

        // Build full path
        this.path = this.hostName + this.webdavDir + this.relativePath;
    }

    @Override
    public void preValidate() throws Exception
    {
        WebdavActionValidator.getInstance().validate(this);
        ResourceSRCValidator.getInstance().validate(this);
    }

    @Override
    protected void execute() throws Exception
    {
        // Responds http 404 in case of a non existing resource without SardineException
        this.isValid = this.sardine.exists(PathBuilder.substituteWhiteSpace(this.path));

        // Free local memory
        this.freeResourceSRC();
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Verify: Resource expectations
        Assert.assertTrue("The response of your check does not macht your expectation", this.expectation == this.isValid);

        // Verify: check operation succeeded -> 200, 404
        ResponseCodeValidator.getInstance().validate(this.httpResponseCode, (this.isValid) ? 200 : 404);
    }

    /**
     * Holds the information of validity of the used path after performing this action
     *
     * @return State of resources existence
     */
    public boolean isValid()
    {
        return this.isValid;
    }
}