package com.xceptance.xlt.webdav.util;

/**
 * Utility to translate paths into paths with "%20" translated spaces. Use this to avoid IllegalStatementExceptions
 * caused by spaces inside paths.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public abstract class PathBuilder
{
    /**
     * Translates " " inside paths to ASCII conform characters without changing the source
     *
     * @param path
     *            Path which should be translated
     * @return Path with "%20" substituted space characters
     */
    public static String substituteWhiteSpace(String path)
    {
        return path.replaceAll(" ", "%20");
    }
}
