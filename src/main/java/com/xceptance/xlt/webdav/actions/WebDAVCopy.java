package com.xceptance.xlt.webdav.actions;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDAVAction;
import com.xceptance.xlt.webdav.validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Copies a resource to a destination by using WebDAV <code>COPY</code> by sardine.copy. Can be used by relative path or
 * by a resource object provided by previously performed ListResources actions to perform a copy operation to the
 * destination path. Does does always overwrite at its destination.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDAVCopy extends AbstractWebDAVAction<WebDAVCopy>
{
    // source
    private final String srcURL;

    // destination
    private final String dstURL;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param src
     *            Resources relative source path related to your webdav directory
     * @param dst
     *            Resources relative destination path related to your webdav directory
     */
    public WebDAVCopy(final String src, final String dst)
    {
        super();

        this.srcURL = getURL(src);
        this.dstURL = getURL(dst);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param src
     *            Source DavResource object to perform this action
     * @param dst
     *            Resources relative destination path related to your webdav directory
     */
    public WebDAVCopy(final DavResource src, final String dst)
    {
        super();

        this.srcURL = getURL(src);
        this.dstURL = getURL(dst);
    }
 
    @Override
    public void preValidate() throws Exception
    {
        WebDavActionValidator.validate(this);
    }

    @Override
    protected void execute() throws Exception
    {
        this.getSardine().copy(srcURL, dstURL);
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Verify: Move operation succeeded ->
        // 201 done by creating a new resource
        // 204 done by overwriting an existing resource
        // 207
        ResponseCodeValidator.validate(getHttpResponseCode(), 201, 204, 207);
    }
}