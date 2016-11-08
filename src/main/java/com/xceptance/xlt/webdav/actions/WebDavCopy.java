package com.xceptance.xlt.webdav.actions;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.validators.StatusCodeValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Copies a resource to a destination by using WebDAV <code>COPY</code> by sardine.copy. Can be used by relative path or
 * by a resource object provided by previously performed ListResources actions to perform a copy operation to the
 * destination path. Does does always overwrite at its destination.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavCopy extends AbstractWebDavAction<WebDavCopy>
{
    /**
     * source
     */
    private final String srcUrl;

    /**
     * destination
     */
    private final String dstUrl;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param src
     *            Resources relative source path related to your webdav directory
     * @param dst
     *            Resources relative destination path related to your webdav directory
     */
    public WebDavCopy(final String src, final String dst)
    {
        super();

        srcUrl = getUrl(src);
        dstUrl = getUrl(dst);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param src
     *            Source DavResource object to perform this action
     * @param dst
     *            Resources relative destination path related to your webdav directory
     */
    public WebDavCopy(final DavResource src, final String dst)
    {
        super();

        srcUrl = getUrl(src);
        dstUrl = getUrl(dst);
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
        getSardine().copy(srcUrl, dstUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Verify: Move operation succeeded ->
        // 201 done by creating a new resource
        // 204 done by overwriting an existing resource
        // 207
        StatusCodeValidator.validate(getStatusCode(), 201, 204, 207);
    }
}
