package com.xceptance.xlt.webdav.validators.pre_validators;

import org.junit.Assert;

import com.xceptance.xlt.webdav.actions.CheckResourcePath;
import com.xceptance.xlt.webdav.actions.ListResources;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;

/**
 * Basic prevalidator for all webdav based actions Validates the existence of a client, host name settings, well
 * credential settings and paths for these WebdavActions
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavActionValidator
{
    /**
     * Provides a singleton validator
     */
    private static final WebDavActionValidator instance = new WebDavActionValidator();

    /**
     * @return Instance of this validator
     */
    public static WebDavActionValidator getInstance()
    {
        return instance;
    }

    /**
     * Validates the existence of a client, host name settings, well credential settings and paths of a WebdavAction
     * 
     * @param activeAction
     *            WebdavAction which is going to be performed
     * @throws Exception
     *             Assertion failure
     */
    public void validate(AbstractWebDavAction activeAction) throws Exception
    {
        // Verify: Host name
        Assert.assertFalse("Host name must not be NULL", activeAction.getHostName() == null);
        Assert.assertFalse("Host name must not be empty", activeAction.getHostName().equals(""));

        // Verify: Credentials are used in a common way
        Assert.assertTrue("Credentials are incomplete",
                          ((activeAction.getUserName() == null && activeAction.getUserPassword() == null) || (activeAction.getUserName() != null && activeAction.getUserPassword() != null)));

        // Verify: Sardine client is not null to perform any kind of action
        Assert.assertNotNull("Unable to run action case \"" + activeAction.getTimerName() + "\". Sardine client must not be NULL",
                             activeAction.getSardine());

        // Verify: RelativePath is not empty
        // this is used for logging purpose
        // Exclude ListResources and CheckResources which are able to be performed with an empty path
        if (activeAction.getClass() != ListResources.class && activeAction.getClass() != CheckResourcePath.class)
            Assert.assertFalse("RelativePath of your webdav action must not be empty" + activeAction.getClass().getSimpleName(),
                               activeAction.getUsedRelativePath().equals(""));

        // Verify: Path is not empty
        // this is used for logging purpose
        Assert.assertFalse("Path of your webdav action must not be empty", activeAction.getUsedPath().equals(""));
    }
}
