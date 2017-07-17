package com.xceptance.xlt.webdav.actions;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.github.sardine.DavResource;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.util.WebDavValidationUtils;

/**
 * Downloads a file from a WebDAV server using the GET request method. The downloaded content can optionally be stored
 * in memory for further inspection.
 * <p>
 * The resource in question can be specified either as path (relative to the WebDAV base directory as configured in
 * {@link WebDavConnect}) or as a {@link DavResource} object, which can be obtained from the results of a
 * {@link WebDavList} action.
 * <p>
 * The default action name in the test results will be "{@literal WebDavGet}". Use {@link #timerName(String)} to specify
 * a different name.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavGet extends AbstractWebDavAction<WebDavGet>
{
    /**
     * The URL of the file to fetch.
     */
    private final String url;

    /**
     * Whether to store the content of the file to an internal buffer.
     */
    private final boolean storeContent;

    /**
     * The content of the file.
     */
    private byte[] fileContent;

    /**
     * Action with standard action name listed in the results, based on a path
     *
     * @param relativePath
     *            the resource path relative to your WebDAV base directory
     * @param storeContent
     *            do we want to store the fetched content, true if yes, false otherwise
     */
    public WebDavGet(final String relativePath, final boolean storeContent)
    {
        super();

        url = getUrl(relativePath);
        this.storeContent = storeContent;
    }

    /**
     * Action with standard action name listed in the results, based on a resource object
     *
     * @param davResource
     *            Source DavResource object to perform this action
     * @param storeContent
     *            whether to store the file content to an internal buffer
     */
    public WebDavGet(final DavResource davResource, final boolean storeContent)
    {
        super();

        url = getUrl(davResource);
        this.storeContent = storeContent;
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
        try (final InputStream is = getSardine().get(url))
        {
            if (storeContent)
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
    protected void postValidate()
    {
        // check status code -> 200
        WebDavValidationUtils.validateStatusCode(getStatusCode(), 200);
    }

    /**
     * Returns the content of the requested file.
     *
     * @return the file content
     */
    public byte[] getFileContent()
    {
        return fileContent;
    }

    /**
     * Clears the stored file content. Call this method when you are done with inspecting the file content.
     */
    public void clearFileContent()
    {
        fileContent = null;
    }
}
