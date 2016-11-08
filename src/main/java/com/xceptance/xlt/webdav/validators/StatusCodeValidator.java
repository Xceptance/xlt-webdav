package com.xceptance.xlt.webdav.validators;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;

/**
 * Validator to validate HTTP status codes with expectations.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class StatusCodeValidator
{
    /**
     * Validates that the passed actual status code is one of the given expected status codes.
     *
     * @param actualStatusCode
     *            the status code of your action
     * @param expectedStatusCodes
     *            one or more valid HTTP status codes
     * @throws AssertionError
     *             if validation fails
     */
    public static void validate(final int actualStatusCode, final int... expectedStatusCodes) throws AssertionError
    {
        Assert.assertTrue("Unexpected status code: " + actualStatusCode + " is not one of " + ArrayUtils.toString(expectedStatusCodes),
                          ArrayUtils.contains(expectedStatusCodes, actualStatusCode));
    }
}
