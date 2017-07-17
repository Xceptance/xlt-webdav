package com.xceptance.xlt.webdav.actions;

import org.junit.Assert;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.util.WebDavValidationUtils;

/**
 * Checks if a file or directory exists on a WebDAV server using the HEAD request method.
 * <p>
 * The resource in question can be specified either as path (relative to the WebDAV base directory as configured in
 * {@link WebDavConnect}) or as a {@link DavResource} object, which can be obtained from the results of a
 * {@link WebDavList} action.
 * <p>
 * The default action name in the test results will be "{@literal WebDavExists}". Use {@link #timerName(String)} to
 * specify a different name.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavExists extends AbstractWebDavAction<WebDavExists>
{
    /**
     * The URL of the resource to be checked.
     */
    private final String url;

    /**
     * Whether the resource is expected to exist.
     */
    private final boolean shouldExist;

    /**
     * Whether the resource indeed exists.
     */
    private boolean doesExist;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            the resource path relative to your WebDAV base directory
     * @param shouldExist
     *            whether the resource is expected to exist
     */
    public WebDavExists(final String relativePath, final boolean shouldExist)
    {
        super();

        url = getUrl(relativePath);
        this.shouldExist = shouldExist;
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param davResource
     *            Source DavResource object to perform this action
     * @param shouldExist
     *            whether the resource is expected to exist
     */
    public WebDavExists(final DavResource davResource, final boolean shouldExist)
    {
        super();

        url = getUrl(davResource);
        this.shouldExist = shouldExist;
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
        doesExist = getSardine().exists(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate()
    {
        // check status code
        // - 200: resource exists
        // - 404: resource does not exist
        WebDavValidationUtils.validateStatusCode(getStatusCode(), shouldExist ? 200 : 404);

        // check expectations
        if (shouldExist)
        {
            Assert.assertTrue("The resource does not exist", doesExist);
        }
        else
        {
            Assert.assertFalse("The resource does exist", doesExist);
        }
    }
}
