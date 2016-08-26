package com.xceptance.xlt.webdav.validators.pre_validators;

import org.junit.Assert;

import com.xceptance.xlt.webdav.util.AbstractWebdavAction;

/**
 * Basic prevalidator for all DavResource based actions Prevents usage of a null reference as a resource parameter and
 * enforces the resourceUsage flag of your action is set
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public class ResourceSRCValidator
{
    /**
     * Provides a singleton validator
     */
    private static final ResourceSRCValidator instance = new ResourceSRCValidator();

    /**
     * @return Instance of this validator
     */
    public static ResourceSRCValidator getInstance()
    {
        return instance;
    }

    /**
     * Verifies your resource is not null referenced and the resourceUsage flag is set
     *
     * @param activeAction
     *            WebdavAction which is going to be performed
     * @throws Exception
     *             Assertion failure
     */
    public void validate(AbstractWebdavAction activeAction) throws Exception
    {
        // Verify: DavResource is NOT NULL if it is used
        Assert.assertFalse("You are going to copy a resource by a NULL referenced DavResource", activeAction.getDavResourceUsage()
                                                                                                && activeAction.getResourceSRC() == null);
    }
}