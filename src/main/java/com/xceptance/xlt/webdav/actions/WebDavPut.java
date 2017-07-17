package com.xceptance.xlt.webdav.actions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Assert;

import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.util.WebDavValidationUtils;

/**
 * Uploads a file to a WebDAV server using the PUT request method. If another resource exists at the target location, it
 * will be overwritten. The content of the file can be read from various sources.
 * <p>
 * The target location has to be given as path (relative to the WebDAV base directory as configured in
 * {@link WebDavConnect}).
 * <p>
 * The default action name in the test results will be "{@literal WebDavPut}". Use {@link #timerName(String)} to specify
 * a different name.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavPut extends AbstractWebDavAction<WebDavPut>
{
    /**
     * The URL of the resource to be created/replaced.
     */
    private final String url;

    /**
     * The content of the file.
     */
    private final InputStream fileContent;

    /**
     * Action with standard action name listed in the results, based on a path and a byte array as source
     *
     * @param relativePath
     *            the resource path relative to your WebDAV base directory
     * @param fileContent
     *            the file content to upload
     */
    public WebDavPut(final String relativePath, final byte[] fileContent)
    {
        this(relativePath, new ByteArrayInputStream(fileContent));
    }

    /**
     * Action with standard action name listed in the results, based on a path and a input stream as source
     *
     * @param relativePath
     *            the resource path relative to your WebDAV base directory
     * @param file
     *            the file to upload
     * @throws FileNotFoundException
     *             if the file could not be found
     */
    public WebDavPut(final String relativePath, final File file) throws FileNotFoundException
    {
        this(relativePath, new FileInputStream(file));
    }

    /**
     * Action with standard action name listed in the results, based on a path and a input stream as source
     *
     * @param relativePath
     *            the resource path relative to your WebDAV base directory
     * @param inputStream
     *            the stream to upload
     */
    public WebDavPut(final String relativePath, final InputStream inputStream)
    {
        super();

        url = getUrl(relativePath);
        fileContent = inputStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preValidate()
    {
        WebDavValidationUtils.validateAction(this);

        // verify that data to perform upload is given
        Assert.assertTrue("No content to perform upload", fileContent != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws Exception
    {
        getSardine().put(url, fileContent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate()
    {
        // check status code
        // - 201: done by creating a new file
        // - 204: done by overwriting an existing file
        WebDavValidationUtils.validateStatusCode(getStatusCode(), 201, 204);
    }
}
