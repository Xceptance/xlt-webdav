package com.xceptance.xlt.webdav.actions;

import com.amazonaws.util.IOUtils;
import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.util.AbstractWebDavAction;
import com.xceptance.xlt.webdav.util.PathBuilder;
import com.xceptance.xlt.webdav.validators.post_validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.pre_validators.SourceDavResourceValidator;
import com.xceptance.xlt.webdav.validators.pre_validators.WebDavActionValidator;

import org.junit.Assert;

import java.io.InputStream;

/**
 * Gets a file by using WebDAV <code>GET</code> by sardine.get. Can be used by relative path or by a resource object
 * provided by previously performed ListResources actions. Set the storage flag if you need the result for following
 * actions and release them by .releaseFile() if you do not need it anymore. Set the storage flag to false if you just
 * want to do a performance test.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class GetFile extends AbstractWebDavAction
{
    // File (available after performed action if flag is set)
    private byte[] file;

    private boolean storageFlag;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            Files relative source path related to your webdav directory
     * @param storageFlag
     *            Flag to hold result file in local memory (you have to release it manually)
     */
    public GetFile(String relativePath, boolean storageFlag)
    {
        super();

        this.storageFlag = storageFlag;

        // Redundant initialisation tasks
        this.initializePath(relativePath);
    }

    /**
     * Action with specific name listed in the results, based on a path
     *
     * @param timerName
     *            Is used for naming this action in results
     * @param relativePath
     *            Files relative source path related to your webdav directory
     * @param storageFlag
     *            Flag to hold result file in local memory (you have to release it manually)
     */
    public GetFile(String timerName, String relativePath, boolean storageFlag)
    {
        super(timerName);

        this.storageFlag = storageFlag;

        // Redundant initialisation tasks
        this.initializePath(relativePath);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param resourceSRC
     *            Source DavResource object to perform this action
     * @param storageFlag
     *            Flag to hold result file in local memory (you have to release it manually)
     */
    public GetFile(DavResource resourceSRC, boolean storageFlag)
    {
        super();

        this.storageFlag = storageFlag;

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
     * @param storageFlag
     *            Flag to hold result file in local memory (you have to release it manually)
     */
    public GetFile(String timerName, DavResource resourceSRC, boolean storageFlag)
    {
        super(timerName);

        this.storageFlag = storageFlag;

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
        WebDavActionValidator.getInstance().validate(this);
        SourceDavResourceValidator.getInstance().validate(this);

        // Verify: Resource is a file
        if (this.davResourceUsage)
            Assert.assertFalse("Selected resource must be a file", this.resourceSRC.isDirectory());
    }

    @Override
    protected void execute() throws Exception
    {
        InputStream is = sardine.get(PathBuilder.substituteWhiteSpace(this.path));
        this.file = IOUtils.toByteArray(is);
        is.close();

        // Free local memory
        this.freeResourceSRC();
        if (!storageFlag)
        {
            this.file = null;
        }
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Verify: Get operation succeeded -> 200
        ResponseCodeValidator.getInstance().validate(this.httpResponseCode, 200);

    }

    /**
     * Returns the result file as a byteArray
     *
     * @return Result file
     */
    public byte[] getFile()
    {
        return file;
    }

    /**
     * Releases the result Call this if you do not need the result anymore
     */
    public void releaseFile()
    {
        this.file = null;
    }
}