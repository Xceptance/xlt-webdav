package com.xceptance.xlt.webdav.actions;

import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.util.WebDavValidationUtils;

/**
 * Creates a directory on a WebDAV server using the MKCOL request method. Ensure that any parent directory already
 * exists, otherwise this operation will throw an exception.
 * <p>
 * The target location has to be given as path (relative to the WebDAV base directory as configured in
 * {@link WebDavConnect}).
 * <p>
 * The default action name in the test results will be "{@literal WebDavCreateDirectory}". Use
 * {@link #timerName(String)} to specify a different name.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavCreateDirectory extends AbstractWebDavAction<WebDavCreateDirectory>
{
    /**
     * The URL of the directory to be created.
     */
    private final String url;

    /**
     * Creates a new action with the passed target path.
     *
     * @param relativePath
     *            the resource path relative to your WebDAV base directory
     */
    public WebDavCreateDirectory(final String relativePath)
    {
        super();

        url = getUrl(relativePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preValidate()
    {
        WebDavValidationUtils.validateAction(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws Exception
    {
        getSardine().createDirectory(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate()
    {
        // check status code -> 201
        WebDavValidationUtils.validateStatusCode(getStatusCode(), 201);
    }
}
