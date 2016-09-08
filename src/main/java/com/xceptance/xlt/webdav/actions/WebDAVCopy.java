package com.xceptance.xlt.webdav.actions;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDAVAction;
import com.xceptance.xlt.webdav.validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.SourceDavResourceValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Copies a resource to a destination by using WebDAV <code>COPY</code> by sardine.copy. Can be used by relative path or
 * by a resource object provided by previously performed ListResources actions to perform a copy operation to the
 * destination path. Does does always overwrite at its destination.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDAVCopy extends AbstractWebDAVAction
{
    // source
    private String src;

    // destination
    private String dst;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param src
     *            Resources relative source path related to your webdav directory
     * @param dst
     *            Resources relative destination path related to your webdav directory
     */
    public WebDAVCopy(String src, String dst)
    {
        super();

        this.src = getAbsoluteURL(src);
        this.dst = getAbsoluteURL(dst);
    }

    /**
     * Action with specific name listed in the results, based on a path
     *
     * @param timerName
     *            Is used for naming this action in results
     * @param src
     *            Resources relative source path related to your webdav directory
     * @param dst
     *            Resources relative destination path related to your webdav directory
     */
    public WebDAVCopy(String timerName, String src, String dst)
    {
        super(timerName);

        this.src = getAbsoluteURL(src);
        this.dst = getAbsoluteURL(dst);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param src
     *            Source DavResource object to perform this action
     * @param dst
     *            Resources relative destination path related to your webdav directory
     */
    public WebDAVCopy(DavResource src, String dst)
    {
        super();

        this.src = src.getHref().toString();
        this.dst = getAbsoluteURL(dst);
    }

    /**
     * Action with specific name listed in the results, based on a resource object
     *
     * @param timerName
     *            Is used for naming this action in results
     * @param src
     *            Source DavResource object to perform this action
     * @param dst
     *            Resources relative destination path related to your webdav directory
     */
    public WebDAVCopy(String timerName, DavResource src, String dst)
    {
        super(timerName);

        this.src = src.getHref().toString();
        this.dst = getAbsoluteURL(dst);
    }

    @Override
    public void preValidate() throws Exception
    {
        WebDavActionValidator.validate(this);
        SourceDavResourceValidator.validate(this);
    }

    @Override
    protected void execute() throws Exception
    {
        this.getSardine().copy(src, dst);
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