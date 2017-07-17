package com.xceptance.xlt.webdav.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;

/**
 * Common validation routines used in several WebDAV actions.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class WebDavValidationUtils
{
    /**
     * Validates the existence of a client, host name settings, well credential settings and paths of a WebdavAction
     *
     * @param action
     *            the WebDAV action to be validated
     * @throws AssertionError
     *             if validation fails
     */
    public static void validateAction(final AbstractWebDavAction<?> action) throws AssertionError
    {
        // Verify: Host name is not blank
        Assert.assertTrue("Host name must not be blank", StringUtils.isNotBlank(action.getHostName()));

        // Verify: Credentials are used in a common way
        Assert.assertTrue("Credentials are incomplete",
                          (action.getUserName() == null && action.getUserPassword() == null)
                                                        || (action.getUserName() != null && action.getUserPassword() != null));

        // Verify: Sardine client is not null
        Assert.assertNotNull("Sardine client must not be null", action.getSardine());
    }

    /**
     * Validates that the passed status code is one of the given expected status codes.
     *
     * @param actualStatusCode
     *            the status code to check
     * @param expectedStatusCodes
     *            one or more valid HTTP status codes
     * @throws AssertionError
     *             if validation fails
     */
    public static void validateStatusCode(final int actualStatusCode, final int... expectedStatusCodes) throws AssertionError
    {
        Assert.assertTrue("Unexpected status code: " + actualStatusCode + " is not one of " + ArrayUtils.toString(expectedStatusCodes),
                          ArrayUtils.contains(expectedStatusCodes, actualStatusCode));
    }
}
