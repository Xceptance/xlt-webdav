package com.xceptance.xlt.webdav.actions;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.util.AbstractWebDavAction;
import com.xceptance.xlt.webdav.util.PathBuilder;
import com.xceptance.xlt.webdav.validators.post_validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.pre_validators.SourceDavResourceValidator;
import com.xceptance.xlt.webdav.validators.pre_validators.WebDavActionValidator;

import org.junit.Assert;

/**
 * Moves a resource to a destination by using WebDAV <code>MOVE</code> by sardine.move. Can be used by relative path or
 * by a resource object provided by previously performed ListResources actions to perform a move operation to the
 * destination path.
 */
public class MoveResource extends AbstractWebDavAction
{
    // Paths to perform move operation

    // Relative path of actions source
    private String relativePathSRC;

    // Relative path of actions destination
    private String relativePathDST;

    // Resulting Path of actions source
    private String pathSRC;

    // Resulting path of actions destination
    private String pathDST;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePathSRC
     *            Resources relative source path related to your webdav directory
     * @param relativePathDST
     *            Resources relative destination path related to your webdav directory
     */
    public MoveResource(String relativePathSRC, String relativePathDST)
    {
        super();

        // Redundant initialisation tasks
        this.initializePath(relativePathSRC, relativePathDST);
    }

    /**
     * Action with specific name listed in the results, based on a path
     *
     * @param timerName
     *            Is used for naming this action in results
     * @param relativePathSRC
     *            Resources relative source path related to your webdav directory
     * @param relativePathDST
     *            Resources relative destination path related to your webdav directory
     */
    public MoveResource(String timerName, String relativePathSRC, String relativePathDST)
    {
        super(timerName);

        // Redundant initialisation tasks
        this.initializePath(relativePathSRC, relativePathDST);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param resourceSRC
     *            Source DavResource object to perform this action
     * @param relativePathDST
     *            Resources relative destination path related to your webdav directory
     */
    public MoveResource(DavResource resourceSRC, String relativePathDST)
    {
        super();

        // Redundant initialisation tasks
        this.initializeResource(resourceSRC, relativePathDST);
    }

    /**
     * Action with specific name listed in the results, based on a resource object
     *
     * @param timerName
     *            Is used for naming this action in results
     * @param resourceSRC
     *            Source DavResource object to perform this action
     * @param relativePathDST
     *            Resources relative destination path related to your webdav directory
     */
    public MoveResource(String timerName, DavResource resourceSRC, String relativePathDST)
    {
        super(timerName);

        // Redundant initialisation tasks
        this.initializeResource(resourceSRC, relativePathDST);
    }

    /**
     * Initializes paths by given strings
     *
     * @param relativePathSRC
     *            Resources relative source path related to your webdav directory
     * @param relativePathDST
     *            Resources relative destination path related to your webdav directory
     */
    private void initializePath(String relativePathSRC, String relativePathDST)
    {
        this.davResourceUsage = false;

        // Initialisation to avoid NullPointerException and mismatching
        this.relativePathSRC = (relativePathSRC == null) ? "" : relativePathSRC;
        this.relativePathDST = (relativePathDST == null) ? "" : relativePathDST;

        // Redundant initialisation tasks
        this.initializeFullPath_and_LogPath();
    }

    /**
     * Initializes paths by given resource and string
     *
     * @param resourceSRC
     *            Source DavResource object to perform this action
     * @param relativePathDST
     *            Resources relative destination path related to your webdav directory
     */
    private void initializeResource(DavResource resourceSRC, String relativePathDST)
    {
        this.resourceSRC = resourceSRC;
        this.davResourceUsage = true;

        // Assign path from DavResource
        this.relativePathSRC = (this.resourceSRC == null) ? "" : this.resourceSRC.getPath();
        if (!this.relativePathSRC.equals(""))
        {
            // Extract relative path from resource path
            this.relativePathSRC = this.relativePathSRC.substring(this.webdavDir.length() + 1, this.relativePathSRC.length());
        }
        // Initialisation to avoid NullPointerException and mismatching
        this.relativePathDST = (relativePathDST == null) ? "" : relativePathDST;

        // Redundant initialisation tasks
        this.initializeFullPath_and_LogPath();
    }

    /**
     * Initializes full paths and path for logging purpose
     */
    private void initializeFullPath_and_LogPath()
    {
        // Build full paths
        this.pathSRC = this.hostName + this.webdavDir + this.relativePathSRC;
        this.pathDST = this.hostName + this.webdavDir + this.relativePathDST;

        // Paths used to log request data
        this.relativePath = this.relativePathDST;
        this.path = this.pathDST;
    }

    @Override
    public void preValidate() throws Exception
    {
        WebDavActionValidator.getInstance().validate(this);
        SourceDavResourceValidator.getInstance().validate(this);

        // Verify: RelativePathSRC is not empty
        Assert.assertFalse("RelativePathSRC must not be empty", this.relativePathSRC.equals(""));
    }

    @Override
    protected void execute() throws Exception
    {
        this.sardine.move(PathBuilder.substituteWhiteSpace(this.pathSRC), PathBuilder.substituteWhiteSpace(this.pathDST));

        // Free local memory
        this.freeResourceSRC();
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Verify: Move operation succeeded ->
        // 201 done by creating a new resource
        // 204 done by overwriting an existing resource
        // 207
        ResponseCodeValidator.getInstance().validate(this.httpResponseCode, 201, 204, 207);
    }

    /**
     * Returns the used relative path of operations source
     *
     * @return Relative path of operations source
     */
    public String getUsedRelativePathSRC()
    {
        return this.relativePathSRC;
    }

    /**
     * Returns the used relative path of operations destination
     *
     * @return Relative path of operations destination
     */
    public String getUsedRelativePathDST()
    {
        return this.relativePathDST;
    }
}