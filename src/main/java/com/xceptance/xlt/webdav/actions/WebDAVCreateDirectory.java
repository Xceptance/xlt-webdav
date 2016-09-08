package com.xceptance.xlt.webdav.actions;

import com.xceptance.xlt.webdav.impl.AbstractWebDAVAction;
import com.xceptance.xlt.webdav.validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Creates a directory at a destination by using WebDAV <code>MKCOL</code> by sardine.createDirectory. Can be used by
 * relative path on an existing directory. Ensure the parent directory exists, otherwise this operation throws a
 * SardineException HTTP 409!
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDAVCreateDirectory extends AbstractWebDAVAction
{
	// the path to create
	private final String path;
	
    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param path
     *            Directory's relative destination path relative to your webdav directory
     */
    public WebDAVCreateDirectory(final String path)
    {
        super();
        this.path = getAbsoluteURL(path);
    }

    /**
     * Action with specific name listed in the results, based on a path
     *
     * @param timerName
     *            Is used for naming this action in results
     * @param path
     *            Directory's relative destination path relative to your webdav directory
     */
    public WebDAVCreateDirectory(final String timerName, final String path)
    {
        super(timerName);
        this.path = getAbsoluteURL(path);
    }

    @Override
    public void preValidate() throws Exception
    {
        WebDavActionValidator.validate(this);
    }

    @Override
    protected void execute() throws Exception
    {
        this.getSardine().createDirectory(path);
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Verify: create operation succeeded -> 201
        ResponseCodeValidator.validate(getHttpResponseCode(), 201);
    }
}