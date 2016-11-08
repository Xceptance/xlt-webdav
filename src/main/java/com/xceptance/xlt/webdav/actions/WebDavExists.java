package com.xceptance.xlt.webdav.actions;

import org.junit.Assert;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.validators.StatusCodeValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Checks if a resources path exists by using WebDAV <code>HEAD</code> by sardine.exists. Can be used by relative path
 * or by a resource object provided by previously performed ListResources actions to verify an expectation.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavExists extends AbstractWebDavAction<WebDavExists>
{
    /**
     * Expectation of resources existence
     */
    private final boolean exists;

    /**
     * Verification of path validity
     */
    private boolean doesExist = false;

    /**
     * the path to check
     */
    private final String url;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            Resources relative source path related to your webdav directory
     * @param exists
     *            Expected state of resources existence
     */
    public WebDavExists(final String relativePath, final boolean exists)
    {
        super();

        this.exists = exists;
        url = getUrl(relativePath);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param src
     *            Source DavResource object to perform this action
     * @param exists
     *            Expected state of resources existence
     */
    public WebDavExists(final DavResource src, final boolean exists)
    {
        super();

        this.exists = exists;
        url = getUrl(src);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preValidate() throws Exception
    {
        WebDavActionValidator.validate(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws Exception
    {
        // Responds http 404 in case of a non existing resource without SardineException
        doesExist = getSardine().exists(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Verify: check operation succeeded -> 200, 404
        StatusCodeValidator.validate(getStatusCode(), exists ? 200 : 404);

        // Verify: Resource expectations
        if (exists)
        {
            Assert.assertTrue("The resource does not exist", doesExist);
        }
        else
        {
            Assert.assertFalse("The resource does exist", doesExist);
        }
    }
}
