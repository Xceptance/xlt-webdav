package com.xceptance.xlt.webdav.actions;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.util.WebDavValidationUtils;

/**
 * Copies a file or directory on a WebDAV server to another directory using the COPY request method. If another resource
 * exists at the target location, it will be overwritten.
 * <p>
 * The resource in question can be specified either as path (relative to the WebDAV base directory as configured in
 * {@link WebDavConnect}) or as a {@link DavResource} object, which can be obtained from the results of a
 * {@link WebDavList} action. The target location has to be given as relative path in either case.
 * <p>
 * The default action name in the test results will be "{@literal WebDavCopy}". Use {@link #timerName(String)} to
 * specify a different name.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavCopy extends AbstractWebDavAction<WebDavCopy>
{
    /**
     * The source URL.
     */
    private final String sourceUrl;

    /**
     * The target URL.
     */
    private final String targetUrl;

    /**
     * Creates a new action with a source and target path.
     *
     * @param relativeSourcePath
     *            the source path relative to your WebDAV base directory
     * @param relativeTargetPath
     *            the target path relative to your WebDAV base directory
     */
    public WebDavCopy(final String relativeSourcePath, final String relativeTargetPath)
    {
        super();

        sourceUrl = getUrl(relativeSourcePath);
        targetUrl = getUrl(relativeTargetPath);
    }

    /**
     * Creates a new action with a resource and a target path.
     *
     * @param davResource
     *            the {@link DavResource} object to copy
     * @param relativeTargetPath
     *            the target path relative to your WebDAV base directory
     */
    public WebDavCopy(final DavResource davResource, final String relativeTargetPath)
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
        getSardine().copy(sourceUrl, targetUrl);
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
