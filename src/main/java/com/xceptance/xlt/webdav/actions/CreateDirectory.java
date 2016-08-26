package com.xceptance.xlt.webdav.actions;

import com.xceptance.xlt.webdav.util.AbstractWebdavAction;
import com.xceptance.xlt.webdav.util.PathBuilder;
import com.xceptance.xlt.webdav.validators.post_validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.pre_validators.WebdavActionValidator;

/**
 * Creates a directory at a destination by using WebDAV <code>MKCOL</code> by sardine.createDirectory. Can be used by
 * relative path on an existing directory. Ensure the parent directory exists, otherwise this operatiob throws
 * SardineException with HTTP 409!
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class CreateDirectory extends AbstractWebdavAction
{
    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            Directory's relative destination path related to your webdav directory
     */
    public CreateDirectory(String relativePath)
    {
        super();

        // Redundant initialisation tasks
        this.initialize(relativePath);
    }

    /**
     * Action with specific name listed in the results, based on a path
     *
     * @param timerName
     *            Is used for naming this action in results
     * @param relativePath
     *            Directory's relative destination path related to your webdav directory
     */
    public CreateDirectory(String timerName, String relativePath)
    {
        super(timerName);

        // Redundant initialisation tasks
        this.initialize(relativePath);
    }

    /**
     * Initializes path by given string
     *
     * @param relativePath
     *            Directory's relative destination path related to your webdav directory
     */
    private void initialize(String relativePath)
    {
        // Initialisation to avoid NullPointerException and mismatching
        this.relativePath = (relativePath == null) ? "" : relativePath;

        // Build full path
        this.path = this.hostName + this.webdavDir + this.relativePath;
    }

    @Override
    public void preValidate() throws Exception
    {
        WebdavActionValidator.getInstance().validate(this);
    }

    @Override
    protected void execute() throws Exception
    {
        this.sardine.createDirectory(PathBuilder.substituteWhiteSpace(this.path));
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Verify: create operation succeeded -> 201
        ResponseCodeValidator.getInstance().validate(this.httpResponseCode, 201);
    }
}