package com.xceptance.xlt.webdav.validators.pre_validators;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.xceptance.xlt.webdav.actions.CheckResourcePath;
import com.xceptance.xlt.webdav.actions.ListResources;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;

/**
 * Basic prevalidator for all webDAV actions. Validates the existence of a Sardine client, host name settings, proper
 * credentials and paths.
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
        // Verify: Host name is not blank
        Assert.assertTrue("Host name must not be blank", StringUtils.isNotBlank(activeAction.getHostName()));

        // Verify: Credentials are used in a common way
        Assert.assertTrue("Credentials are incomplete", (activeAction.getUserName() == null && activeAction.getUserPassword() == null)
                                                        || (activeAction.getUserName() != null && activeAction.getUserPassword() != null));

        // Verify: Sardine client is not null
        Assert.assertNotNull("Sardine client must not be null", activeAction.getSardine());

        // Verify: RelativePath is not blank
        // Exclude ListResources and CheckResources which are able to be performed with an empty path
        if (!(activeAction instanceof ListResources) && !(activeAction instanceof CheckResourcePath))
        {
            Assert.assertTrue("RelativePath must not be blank", StringUtils.isNotBlank(activeAction.getUsedRelativePath()));
        }

        // Verify: Path is not blank
        Assert.assertTrue("Absolute path must not be blank", StringUtils.isNotBlank(activeAction.getUsedPath()));
    }
}
