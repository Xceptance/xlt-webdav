package com.xceptance.xlt.webdav.actions;

import org.junit.Assert;

import com.xceptance.xlt.webdav.util.AbstractWebDavAction;
import com.xceptance.xlt.webdav.util.PathBuilder;
import com.xceptance.xlt.webdav.validators.post_validators.ResponseCodeValidator;
import com.xceptance.xlt.webdav.validators.pre_validators.WebDavActionValidator;

import java.io.InputStream;

/**
 * Puts a given file to a destination by using WebDAV <code>PUT</code> by sardine.put. Can be used by relative path
 * which describes the destination an a byteArray or InputStream as source file.
 */
public class PutFile extends AbstractWebDavAction
{
    // File data to perform upload
    private byte[] fileByBytes;

    private InputStream fileByInputStream;

    /**
     * Action with standard action name listed in the results, based on a path and a byte array as source
     * 
     * @param relativePath
     *            Files relative destination path related to your webdav directory
     * @param file
     *            Byte array to perform upload
     */
    public PutFile(String relativePath, byte[] file)
    {
        super();

        this.fileByBytes = file;

        // Redundant initialisation tasks
        this.initialize(relativePath);
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
    public PutFile(String timerName, String relativePath, byte[] file)
    {
        super(timerName);

        this.fileByBytes = file;

        // Redundant initialisation tasks
        this.initialize(relativePath);
    }

    /**
     * Action with standard action name listed in the results, based on a path and a input stream as source
     * 
     * @param relativePath
     *            Files relative destination path related to your webdav directory
     * @param file
     *            InputStream to perform upload
     */
    public PutFile(String relativePath, InputStream file)
    {
        super();

        this.fileByInputStream = file;

        // Redundant initialisation tasks
        this.initialize(relativePath);
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
    public PutFile(String timerName, String relativePath, InputStream file)
    {
        super(timerName);

        this.fileByInputStream = file;

        // Redundant initialisation tasks
        this.initialize(relativePath);
    }

    /**
     * Initializes path by given string
     *
     * @param relativePath
     *            Files relative destination path related to your webdav directory
     */
    private void initialize(String relativePath)
    {
        // initialisation to avoid NullPointerException and mismatching
        this.relativePath = (relativePath == null) ? "" : relativePath;
        this.path = this.hostName + this.webdavDir + this.relativePath;
    }

    @Override
    public void preValidate() throws Exception
    {
        WebDavActionValidator.getInstance().validate(this);

        // Verify: Data to perform upload is given
        Assert.assertFalse("No content to perform upload", this.fileByBytes == null && this.fileByInputStream == null);
    }

    @Override
    protected void execute() throws Exception
    {
        // Upload by InputStream
        if (this.fileByInputStream != null)
            this.sardine.put(PathBuilder.substituteWhiteSpace(this.path), this.fileByInputStream);
        // Upload by byteArray
        else
            this.sardine.put(PathBuilder.substituteWhiteSpace(this.path), this.fileByBytes);

        // Free local memory
        this.fileByBytes = null;
        this.fileByInputStream = null;
    }

    @Override
    protected void postValidate() throws Exception
    {
        // Verify: Put operation succeeded ->
        // 201 done by creating a new file
        // 204 done by overwriting an existing file
        ResponseCodeValidator.getInstance().validate(this.httpResponseCode, 201, 204);
    }
}