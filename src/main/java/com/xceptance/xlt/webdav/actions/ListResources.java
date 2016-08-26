package com.xceptance.xlt.webdav.actions;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.util.AbstractWebdavAction;
import com.xceptance.xlt.webdav.util.PathBuilder;
import com.xceptance.xlt.webdav.validators.post_validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.pre_validators.ResourceSRCValidator;
import com.xceptance.xlt.webdav.validators.pre_validators.WebdavActionValidator;

import org.junit.Assert;

import java.util.List;

/**
 * Lists a resource at a specific path by using WebDAV <code>PROPFIND</code> by sardine.list. Can be used by relative
 * path or by a resource object provided by previously performed ListResources actions. Listing of directories will give
 * you a list of all resources inside (at depth > 0) included this directory itself at index 0. A depth of 0 will give
 * you a specific resource as a list with one member. Depths out of 0 and 1 may not be supported by a server. Use this
 * action to get resource objects for following actions and use .freeResources() if you dont need them anymore.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class ListResources extends AbstractWebdavAction
{
    // Search depth at the destination path
    private int depth;

    // Resource result set
    private List<DavResource> resources;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            Resources relative source path related to your webdav directory
     * @param depth
     *            Depth of search (>= -1: -1 = infinity, 0 = single resource) maybe not supported by a server
     */
    public ListResources(String relativePath, int depth)
    {
        super();

        this.depth = depth;

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
     * @param depth
     *            Depth of search (>= -1: -1 = infinity, 0 = single resource) maybe not supported by a server
     */
    public ListResources(String timerName, String relativePath, int depth)
    {
        super(timerName);

        this.depth = depth;

        // Redundant initialisation tasks
        this.initializePath(relativePath);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param resourceSRC
     *            Source DavResource object to perform this action
     * @param depth
     *            Depth of search (>= -1: -1 = infinity, 0 = single resource) maybe not supported by a server
     */
    public ListResources(DavResource resourceSRC, int depth)
    {
        super();

        this.depth = depth;

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
     * @param depth
     *            Depth of search (>= -1: -1 = infinity, 0 = single resource) maybe not supported by a server
     */
    public ListResources(String timerName, DavResource resourceSRC, int depth)
    {
        super(timerName);

        this.depth = depth;

        // Redundant initialisation tasks
        this.initializeResource(resourceSRC);
    }

    /**
     * Initializes path by given string
     *
     * @param relativePath
     *            Resources relative source path related to your webdav directory
     */
    private void initializePath(String relativePath)
    {
        this.davResourceUsage = false;

        // initialisation to avoid NullPointerException and mismatching
        this.relativePath = (relativePath == null) ? "" : relativePath;

        // redundant initialisation tasks
        this.initializeFullPath();

    }

    /**
     * Initializes path by given resource
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
        // Build full path
        this.path = this.hostName + this.webdavDir + this.relativePath;
    }

    @Override
    public void preValidate() throws Exception
    {
        WebdavActionValidator.getInstance().validate(this);
        ResourceSRCValidator.getInstance().validate(this);

        // Verify: Valid depth
        Assert.assertTrue("You are going to use an invalid depth of " + this.depth + ". It must be >= -1", this.depth >= -1);
    }

    @Override
    protected void execute() throws Exception
    {
        this.resources = this.sardine.list(PathBuilder.substituteWhiteSpace(this.path), depth, false);

        // Free local memory
        this.freeResourceSRC();
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Verify: List operation succeeded -> 207
        ResponseCodeValidator.getInstance().validate(this.httpResponseCode, 207);
    }

    /**
     * Returns the result set of the list operation
     *
     * @return Result set
     */
    public List<DavResource> getRessources()
    {
        return this.resources;
    }

    /**
     * Releases the results Call this if you do not need the results anymore
     */
    public void releaseResources()
    {
        this.resources = null;
    }
}