package com.xceptance.xlt.webdav.actions;

import java.io.InputStream;

import org.junit.Assert;

import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;
import com.xceptance.xlt.webdav.validators.StatusCodeValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Puts a given file to a destination by using WebDAV <code>PUT</code> by sardine.put. Can be used by relative path
 * which describes the destination an a byteArray or InputStream as source file.
 */
public class WebDavPut extends AbstractWebDavAction<WebDavPut>
{
    /**
     * File data to perform upload
     */
    private final byte[] fileContent;

    /**
     * alternative source
     */
    private final InputStream inputStream;

    /**
     * path to write to
     */
    private final String url;

    /**
     * Action with standard action name listed in the results, based on a path and a byte array as source
     *
     * @param path
     *            Files relative destination path related to your webdav directory
     * @param file
     *            Byte array to perform upload
     */
    public WebDavPut(final String path, final byte[] fileContent)
    {
        super();

        url = getUrl(path);
        this.fileContent = fileContent;
        inputStream = null;
    }

    /**
     * Action with standard action name listed in the results, based on a path and a input stream as source
     *
     * @param path
     *            Files relative destination path related to your webdav directory
     * @param inputStream
     *            InputStream to perform upload
     */
    public WebDavPut(final String path, final InputStream inputStream)
    {
        super();

        url = getUrl(path);
        fileContent = null;
        this.inputStream = inputStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preValidate() throws Exception
    {
        WebDavActionValidator.validate(this);

        // Verify: Data to perform upload is given
        Assert.assertTrue("No content to perform upload", fileContent != null || inputStream != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws Exception
    {
        if (inputStream != null)
        {
            getSardine().put(url, inputStream);
        }
        else
        {
            getSardine().put(url, fileContent);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postValidate() throws Exception
    {
        // Verify: Put operation succeeded ->
        // 201 done by creating a new file
        // 204 done by overwriting an existing file
        StatusCodeValidator.validate(getStatusCode(), 201, 204);
    }
}
