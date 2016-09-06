package com.xceptance.xlt.webdav.validators.post_validators;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;

/**
 * Validator to validate HTTP response codes with expectations
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class ResponseCodeValidator
{
    /**
     * Provides a singleton validator
     */
    private static final ResponseCodeValidator instance = new ResponseCodeValidator();

    /**
     * @return Instance of this validator
     */
    public static ResponseCodeValidator getInstance()
    {
        return instance;
    }

    /**
     * Validates that the passed actual response code is one of the given expected response codes.
     *
     * @param actualResponseCode
     *            the response code of your action
     * @param expectedResponseCodes
     *            one or more valid HTTP response codes
     * @throws AssertionError
     *             if validation fails
     */
    public void validate(int actualResponseCode, int... expectedResponseCodes) throws Exception
    {
        Assert.assertTrue("Unexpected response code: " + actualResponseCode + " is not one of "
                              + ArrayUtils.toString(expectedResponseCodes),
                          ArrayUtils.contains(expectedResponseCodes, actualResponseCode));
    }
}
