package com.xceptance.xlt.webdav.validators;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

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
     * Validates the existence of a client, host name settings, well credential settings and paths of a WebdavAction
     *
     * @param activeAction
     *            WebdavAction which is going to be performed
     * @throws AssertionError
     *             Assertion failure
     */
    public static void validate(final AbstractWebDavAction<?> activeAction) throws AssertionError
    {
        // Verify: Host name is not blank
        Assert.assertTrue("Host name must not be blank", StringUtils.isNotBlank(activeAction.getHostName()));

        // Verify: Credentials are used in a common way
        Assert.assertTrue("Credentials are incomplete", (activeAction.getUserName() == null && activeAction.getUserPassword() == null)
                                                        || (activeAction.getUserName() != null && activeAction.getUserPassword() != null));

        // Verify: Sardine client is not null
        Assert.assertNotNull("Sardine client must not be null", activeAction.getSardine());
    }
}
