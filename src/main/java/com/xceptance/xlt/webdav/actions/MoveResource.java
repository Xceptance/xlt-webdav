package com.xceptance.xlt.webdav.actions;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDAVAction;
import com.xceptance.xlt.webdav.validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.SourceDavResourceValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Moves a resource to a destination by using WebDAV <code>MOVE</code> by sardine.move. Can be used by relative path or
 * by a resource object provided by previously performed ListResources actions to perform a move operation to the
 * destination path.
 */
public class MoveResource extends AbstractWebDAVAction
{
    // Relative path of actions source
    private final String src;

    // Relative path of actions destination
    private final String dst;
    
    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param src
     *            Resources relative source path related to your webdav directory
     * @param dst
     *            Resources relative destination path related to your webdav directory
     */
    public MoveResource(final String src, final String dst)
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
    public MoveResource(String timerName, String src, String dst)
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
    public MoveResource(final DavResource src, final String dst)
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
     * @param resourceSRC
     *            Source DavResource object to perform this action
     * @param relativePathDST
     *            Resources relative destination path related to your webdav directory
     */
    public MoveResource(final String timerName, final DavResource src, final String dst)
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
        this.getSardine().move(src, dst);
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