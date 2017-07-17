package com.xceptance.xlt.webdav.actions;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.util.WebDavValidationUtils;

/**
 * Deletes a file or directory on a WebDAV server using the DELETE request method.
 * <p>
 * The resource in question can be specified either as path (relative to the WebDAV base directory as configured in
 * {@link WebDavConnect}) or as a {@link DavResource} object, which can be obtained from the results of a
 * {@link WebDavList} action.
 * <p>
 * The default action name in the test results will be "{@literal WebDavDelete}". Use {@link #timerName(String)} to
 * specify a different name.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavDelete extends AbstractWebDavAction<WebDavDelete>
{
    /**
     * The URL of the resource to be deleted.
     */
    private final String url;

    /**
     * Creates a new action with the passed resource path.
     *
     * @param relativePath
     *            the resource path relative to your WebDAV base directory
     */
    public WebDavDelete(final String relativePath)
    {
        super();

        url = getUrl(relativePath);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param davResource
     *            the {@link DavResource} object to delete
     */
    public WebDavDelete(final DavResource davResource)
    {
        super();

        url = getUrl(davResource);
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
        getSardine().delete(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate()
    {
        // check status code -> 204
        WebDavValidationUtils.validateStatusCode(getStatusCode(), 204);
    }
}
