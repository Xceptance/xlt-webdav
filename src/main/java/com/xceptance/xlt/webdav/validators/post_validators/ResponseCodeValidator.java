package com.xceptance.xlt.webdav.validators.post_validators;

import org.junit.Assert;

/**
 * Validator to postvalidate http response codes with expectations
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
     * Validates responded code against single expectation
     *
     * @param httpResponseCode
     *            Responded code by your action
     * @param httpResponseCodeExpectation
     *            Expected code of your action
     * @throws Exception
     *             Assertion failure
     */
    public void validate(int httpResponseCode, Integer httpResponseCodeExpectation) throws Exception
    {
        Assert.assertTrue("Respond of your request in not as expected. Server responds HTTP(" + httpResponseCode + ").",
                          httpResponseCode == httpResponseCodeExpectation);
    }

    /**
     * Validates responded code against multiple expectations
     *
     * @param httpResponseCode
     *            Responded code of your action
     * @param httpResponseCodeExpectation
     *            Multiple int values of valid http response codes for validation purpose
     * @throws Exception
     *             Assertion failure
     */
    public void validate(int httpResponseCode, int... httpResponseCodeExpectation) throws Exception
    {
        boolean isValid = false;
        for (int singleExpectation : httpResponseCodeExpectation)
        {
            if (httpResponseCode == singleExpectation)
                isValid = true;
        }
        Assert.assertTrue("Respond of your request is not as expected. Server responds HTTP(" + httpResponseCode + ").", isValid);
    }
}
