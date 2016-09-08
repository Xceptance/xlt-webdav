package com.xceptance.xlt.webdav.actions;

import java.io.InputStream;

import org.junit.Assert;

import com.xceptance.xlt.webdav.impl.AbstractWebDAVAction;
import com.xceptance.xlt.webdav.validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.WebDavActionValidator;

/**
 * Puts a given file to a destination by using WebDAV <code>PUT</code> by sardine.put. Can be used by relative path
 * which describes the destination an a byteArray or InputStream as source file.
 */
public class WebDAVPut extends AbstractWebDAVAction
{
    // File data to perform upload
    private final byte[] fileContent;

    // alternative source
    private final InputStream inputStream;

    // path to write to
    private final String path;
    
    /**
     * Action with standard action name listed in the results, based on a path and a byte array as source
     * 
     * @param path
     *            Files relative destination path related to your webdav directory
     * @param file
     *            Byte array to perform upload
     */
    public WebDAVPut(final String path, final byte[] fileContent)
    {
        super();
        this.path = getAbsoluteURL(path);
        this.fileContent = fileContent;
        this.inputStream = null;
    }

    /**
     * Action with specific name listed in the results, based on a path and a byte array as source
     * 
     * @param timerName
     *            Is used for naming this action in results
     * @param relativePath
     *            Files relative destination path related to your webdav directory
     * @param file
     *            byte array to perform upload
     */
    public WebDAVPut(final String timerName, final String path, byte[] fileContent)
    {
        super(timerName);

        this.fileContent = fileContent;
        this.path = getAbsoluteURL(path);
        this.inputStream = null;
    }

    /**
     * Action with standard action name listed in the results, based on a path and a input stream as source
     * 
     * @param path
     *            Files relative destination path related to your webdav directory
     * @param inputStream
     *            InputStream to perform upload
     */
    public WebDAVPut(final String path, final InputStream inputStream)
    {
        super();
        this.fileContent = null;
        this.path = getAbsoluteURL(path);
        this.inputStream = inputStream;
    }

    /**
     * Action with specific name listed in the results, based on a path and a input stream as source
     * 
     * @param timerName
     *            Is used for naming this action in results
     * @param relativePath
     *            Files relative destination path related to your webdav directory
     * @param file
     *            InputStream to perform upload
     */
    public WebDAVPut(final String timerName, final String path, final InputStream inputStream)
    {
        super(timerName);
        this.fileContent = null;
        this.path = getAbsoluteURL(path);
        this.inputStream = inputStream;
    }

    @Override
    public void preValidate() throws Exception
    {
        WebDavActionValidator.validate(this);

        // Verify: Data to perform upload is given
        Assert.assertFalse("No content to perform upload", this.fileContent == null && this.inputStream == null);
    }

    @Override
    protected void execute() throws Exception
    {
        // Upload by InputStream
        if (this.inputStream != null)
        {
            this.getSardine().put(path, this.inputStream);
        }
        else
        {
            this.getSardine().put(path, this.fileContent);
        }
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Verify: Put operation succeeded ->
        // 201 done by creating a new file
        // 204 done by overwriting an existing file
        ResponseCodeValidator.validate(getHttpResponseCode(), 201, 204);
    }
}