package com.xceptance.xlt.webdav.actions;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.util.PathBuilder;
import com.xceptance.xlt.webdav.validators.post_validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.pre_validators.SourceDavResourceValidator;
import com.xceptance.xlt.webdav.validators.pre_validators.WebDavActionValidator;

/**
 * Deletes a resource by using WebDAV <code>DELETE</code> by sardine.delete. Can be used by relative path or by a
 * resource object provided by previously performed ListResources actions.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class DeleteResource extends AbstractWebDavAction
{
    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            Resources relative source path related to your webdav directory
     */
    public DeleteResource(String relativePath)
    {
        super();

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
     */
    public DeleteResource(String timerName, String relativePath)
    {
        super(timerName);

        // Redundant initialisation tasks
        this.initializePath(relativePath);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param resourceSRC
     *            Source DavResource object to perform this action
     */
    public DeleteResource(DavResource resourceSRC)
    {
        super();

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
     */
    public DeleteResource(String timerName, DavResource resourceSRC)
    {
        super(timerName);

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

        // Initialisation to avoid NullPointerException and mismatching
        this.relativePath = (relativePath == null) ? "" : relativePath;

        // Redundant initialisation tasks
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
     * Initializes full path and path for logging purpose
     */
    private void initializeFullPath()
    {
        // Build full path
        this.path = this.hostName + this.webdavDir + this.relativePath;
    }

    @Override
    public void preValidate() throws Exception
    {
        WebDavActionValidator.getInstance().validate(this);
        SourceDavResourceValidator.getInstance().validate(this);
    }

    @Override
    protected void execute() throws Exception
    {
        this.sardine.delete(PathBuilder.substituteWhiteSpace(this.path));

        // Free local memory
        this.freeResourceSRC();
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Verify: Delete operation succeeded -> 204
        ResponseCodeValidator.getInstance().validate(this.httpResponseCode, 204);
    }
}