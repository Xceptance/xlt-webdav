package com.xceptance.xlt.webdav.actions;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.util.WebDavValidationUtils;

/**
 * Moves a file or directory on a WebDAV server to another directory using the MOVE request method. If another resource
 * exists at the target location, it will be overwritten.
 * <p>
 * The resource in question can be specified either as path (relative to the WebDAV base directory as configured in
 * {@link WebDavConnect}) or as a {@link DavResource} object, which can be obtained from the results of a
 * {@link WebDavList} action. The target location has to be given as relative path in either case.
 * <p>
 * The default action name in the test results will be "{@literal WebDavMove}". Use {@link #timerName(String)} to
 * specify a different name.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavMove extends AbstractWebDavAction<WebDavMove>
{
    /**
     * Relative path of actions source
     */
    private final String sourceUrl;

    /**
     * Relative path of actions destination
     */
    private final String targetUrl;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativeSourcePath
     *            the source path relative to your WebDAV base directory
     * @param relativeTargetPath
     *            the target path relative to your WebDAV base directory
     */
    public WebDavMove(final String relativeSourcePath, final String relativeTargetPath)
    {
        super();

        sourceUrl = getUrl(relativeSourcePath);
        targetUrl = getUrl(relativeTargetPath);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param davResource
     *            Source DavResource object to perform this action
     * @param relativeTargetPath
     *            Resources relative destination path related to your webdav directory
     */
    public WebDavMove(final DavResource davResource, final String relativeTargetPath)
    {
        super();

        sourceUrl = getUrl(davResource);
        targetUrl = getUrl(relativeTargetPath);
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
        getSardine().move(sourceUrl, targetUrl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate()
    {
        // check status code
        // - 201: done by creating a new resource
        // - 204: done by overwriting an existing resource
        // - 207: ???
        WebDavValidationUtils.validateStatusCode(getStatusCode(), 201, 204, 207);
    }
}
