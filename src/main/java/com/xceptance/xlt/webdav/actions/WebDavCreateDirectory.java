package com.xceptance.xlt.webdav.actions;

import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.validators.StatusCodeValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Creates a directory at a destination by using WebDAV <code>MKCOL</code> by sardine.createDirectory. Can be used by
 * relative path on an existing directory. Ensure the parent directory exists, otherwise this operation throws a
 * SardineException HTTP 409!
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavCreateDirectory extends AbstractWebDavAction<WebDavCreateDirectory>
{
    /**
     * the path to create
     */
    private final String url;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param path
     *            Directory's relative destination path relative to your webdav directory
     */
    public WebDavCreateDirectory(final String path)
    {
        super();

        url = getUrl(path);
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
        getSardine().createDirectory(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Verify: create operation succeeded -> 201
        StatusCodeValidator.validate(getStatusCode(), 201);
    }
}
