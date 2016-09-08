package com.xceptance.xlt.webdav.actions;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDAVAction;
import com.xceptance.xlt.webdav.validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.SourceDavResourceValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Deletes a resource by using WebDAV <code>DELETE</code> by sardine.delete. Can be used by relative path or by a
 * resource object provided by previously performed ListResources actions.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDAVDelete extends AbstractWebDAVAction
{
	// our path to delete
	private final String path;
	
    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            Resources relative source path related to your webdav directory
     */
    public WebDAVDelete(final String path)
    {
        super();
        this.path = getAbsoluteURL(path);
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param src
     *            Source DavResource object to perform this action
     */
    public WebDAVDelete(final DavResource src)
    {
        super();
        path = src.getHref().toString();
    }

    /**
     * Action with specific name listed in the results, based on a resource object
     *
     * @param timerName
     *            Is used for naming this action in results
     * @param src
     *            Source DavResource object to perform this action
     */
    public WebDAVDelete(final String timerName, final DavResource src)
    {
        super(timerName);
        path = src.getHref().toString();
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
        this.getSardine().delete(path);
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Verify: Delete operation succeeded -> 204
        ResponseCodeValidator.validate(getHttpResponseCode(), 204);
    }
}