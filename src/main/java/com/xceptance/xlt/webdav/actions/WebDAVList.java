package com.xceptance.xlt.webdav.actions;

import java.util.List;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDAVAction;
import com.xceptance.xlt.webdav.validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Lists a resource at a specific path by using WebDAV <code>PROPFIND</code> by sardine.list. Can be used by relative
 * path or by a resource object provided by previously performed ListResources actions. Listing of directories will give
 * you a list of all resources inside (at depth > 0) included this directory itself at index 0. A depth of 0 will give
 * you a specific resource as a list with one member. Depths out of 0 and 1 may not be supported by a server. Use this
 * action to get resource objects for following actions and use .freeResources() if you dont need them anymore.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDAVList extends AbstractWebDAVAction<WebDAVList>
{
    // Search depth at the destination path
    private final int depth;

    // Resource result set
    private List<DavResource> resources;

    // the path to get
    private final String url;
    
    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param path
     *            Resources relative source path related to your webdav directory
     */
    public WebDAVList(final String path)
    {
        super();

        this.depth = 0;
        this.url = getURL(path);
    }
    
    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param path
     *            Resources relative source path related to your webdav directory
     * @param depth
     *            Depth of search (>= -1: -1 = infinity, 0 = single resource) maybe not supported by a server
     */
    public WebDAVList(final String path, final int depth)
    {
        super();

        this.depth = depth;
        this.url = getURL(path);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param src
     *            Source DavResource object to perform this action
     */
    public WebDAVList(final DavResource src)
    {
        super();

        this.depth = 0;
        this.url = getURL(src);
    }
    
    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param src
     *            Source DavResource object to perform this action
     * @param depth
     *            Depth of search (>= -1: -1 = infinity, 0 = single resource) maybe not supported by a server
     */
    public WebDAVList (final DavResource src, final int depth)
    {
        super();

        this.depth = depth;
        this.url = getURL(src);
    }

    @Override
    public void preValidate() throws Exception
    {
        WebDavActionValidator.validate(this);
    }

    @Override
    protected void execute() throws Exception
    {
        this.resources = this.getSardine().list(url, depth, false);
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Verify: List operation succeeded -> 207
        ResponseCodeValidator.validate(getHttpResponseCode(), 207);
    }

    /**
     * Returns the result set of the list operation
     *
     * @return Result set
     */
    public List<DavResource> getResources()
    {
        return this.resources;
    }
}