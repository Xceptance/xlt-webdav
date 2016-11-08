package com.xceptance.xlt.webdav.actions;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.validators.StatusCodeValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Gets a file by using WebDAV <code>GET</code> by sardine.get. Can be used by relative path or by a resource object
 * provided by previously performed ListResources actions. Set the storage flag if you need the result for following
 * actions and release them by .releaseFile() if you do not need it anymore. Set the storage flag to false if you just
 * want to do a performance test.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavGet extends AbstractWebDavAction<WebDavGet>
{
    /**
     * the path to fetch
     */
    private final String url;

    /**
     * File (available after performed action if flag is set)
     */
    private byte[] fileContent;

    /**
     * do we want to preserve the file content?
     */
    private final boolean store;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param path
     *            Files relative source path related to your webdav directory
     * @param store
     *            do we want to store the fetched content, true if yes, false otherwise
     */
    public WebDavGet(final String path, final boolean store)
    {
        super();

        url = getUrl(path);
        this.store = store;
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param src
     *            Source DavResource object to perform this action
     * @param store
     *            do we want to store the fetched content, true if yes, false otherwise
     */
    public WebDavGet(final DavResource src, final boolean store)
    {
        super();

        url = getUrl(src);
        this.store = store;
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
        try (final InputStream is = getSardine().get(url))
        {
            if (store)
            {
                fileContent = IOUtils.toByteArray(is);
            }
            else
            {
                // just read, don't keep
                final byte[] data = new byte[1024];

                while (is.read(data, 0, data.length) != -1)
                {
                    // nope
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Verify: Get operation succeeded -> 200
        StatusCodeValidator.validate(getStatusCode(), 200);
    }

    /**
     * Returns the result file as a byteArray
     *
     * @return the read file content
     */
    public byte[] getFileContent()
    {
        return fileContent;
    }
}
