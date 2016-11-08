package com.xceptance.xlt.webdav.actions;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.validators.StatusCodeValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Deletes a resource by using WebDAV <code>DELETE</code> by sardine.delete. Can be used by relative path or by a
 * resource object provided by previously performed ListResources actions.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavDelete extends AbstractWebDavAction<WebDavDelete>
{
    /**
     * our path to delete
     */
    private final String url;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            Resources relative source path related to your webdav directory
     */
    public WebDavDelete(final String path)
    {
        super();
        url = getUrl(path);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param src
     *            Source DavResource object to perform this action
     */
    public WebDavDelete(final DavResource src)
    {
        super();
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
        getSardine().delete(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Verify: Delete operation succeeded -> 204
        StatusCodeValidator.validate(getStatusCode(), 204);
    }
}
