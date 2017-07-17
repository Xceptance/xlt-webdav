package com.xceptance.xlt.webdav.actions;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.util.WebDavValidationUtils;

/**
 * Lists the properties of a resource (and optionally, in case of a directory, of its children) using the PROPFIND
 * request method. Use this action to get {@link DavResource} objects for querying the properties or to use them as
 * input for subsequent actions.
 * <p>
 * The resource in question can be specified either as path (relative to the WebDAV base directory as configured in
 * {@link WebDavConnect}) or as a {@link DavResource} object, which can be obtained from the results of a
 * {@link WebDavList} action.
 * <p>
 * In case the target resource is a directory Listing of directories will give you a list of all resources inside (at
 * depth greater than 0) included this directory itself at index 0. A depth of 0 will give you a specific resource as a
 * list with one member.
 * <ul>
 * <li>0: just the resource</li>
 * <li>1: the resource and</li>
 * <li>-1: the resource and all of its children unlimited depth (infinity)</li>
 * </ul>
 * Note that a WebDAV server might forbid to use infinity depth.
 * <p>
 * The default action name in the test results will be "{@literal WebDavList}". Use {@link #timerName(String)} to
 * specify a different name.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavList extends AbstractWebDavAction<WebDavList>
{
    /**
     * The valid values for depth.
     */
    private static final int[] VALID_DEPTH_VALUES =
        {
          -1, 0, 1
        };

    /**
     * The URL of the resource to list.
     */
    private final String url;

    /**
     * Search depth at the destination path
     */
    private final int depth;

    /**
     * The details of the resources found.
     */
    private List<DavResource> resources;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            the resource path relative to your WebDAV base directory
     */
    public WebDavList(final String relativePath)
    {
        this(relativePath, 0);
    }

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            the resource path relative to your WebDAV base directory
     * @param depth
     *            the listing depth (one of -1/0/1)
     */
    public WebDavList(final String relativePath, final int depth)
    {
        super();

        url = getUrl(relativePath);
        this.depth = depth;
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param davResource
     *            Source DavResource object to perform this action
     */
    public WebDavList(final DavResource davResource)
    {
        this(davResource, 0);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param davResource
     *            Source DavResource object to perform this action
     * @param depth
     *            the listing depth (one of -1/0/1)
     */
    public WebDavList(final DavResource davResource, final int depth)
    {
        super();

        url = getUrl(davResource);
        this.depth = depth;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preValidate()
    {
        WebDavValidationUtils.validateAction(this);

        // check depth
        Assert.assertTrue("Invalid depth value given: " + depth + " is not one of " + ArrayUtils.toString(VALID_DEPTH_VALUES),
                          ArrayUtils.contains(VALID_DEPTH_VALUES, depth));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws Exception
    {
        resources = getSardine().list(url, depth, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate()
    {
        // check status code -> 207
        WebDavValidationUtils.validateStatusCode(getStatusCode(), 207);
    }

    /**
     * Returns the result set of the list operation
     *
     * @return a resource list
     */
    public List<DavResource> getResources()
    {
        return resources;
    }
}
