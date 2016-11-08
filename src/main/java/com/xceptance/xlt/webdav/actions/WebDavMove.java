package com.xceptance.xlt.webdav.actions;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.validators.StatusCodeValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Moves a resource to a destination by using WebDAV <code>MOVE</code> by sardine.move. Can be used by relative path or
 * by a resource object provided by previously performed ListResources actions to perform a move operation to the
 * destination path.
 */
public class WebDavMove extends AbstractWebDavAction<WebDavMove>
{
    /**
     * Relative path of actions source
     */
    private final String srcUrl;

    /**
     * Relative path of actions destination
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
    public WebDavMove(final String src, final String dst)
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
    public WebDavMove(final DavResource src, final String dst)
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
        getSardine().move(srcUrl, dstUrl);
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
