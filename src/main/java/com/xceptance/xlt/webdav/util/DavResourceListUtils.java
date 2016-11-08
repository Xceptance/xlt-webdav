package com.xceptance.xlt.webdav.util;

import java.util.ArrayList;
import java.util.List;

import com.github.sardine.DavResource;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Utility to get specific results out of a List of DavResources.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public abstract class DavResourceListUtils
{
    /**
     * @param davResources
     *            List of DavResources with results of a performed ListAction or selection
     * @return List of all files contained inside the source list
     */
    public static List<DavResource> getAllFiles(final List<DavResource> davResources)
    {
        final List<DavResource> results = new ArrayList<DavResource>();

        if (davResources != null)
        {
            for (final DavResource resource : davResources)
            {
                if (!resource.isDirectory())
                {
                    results.add(resource);
                }
            }
        }

        return results;
    }

    /**
     * @param davResources
     *            List of DavResources with results of a performed ListAction or selection
     * @param endingPhrase
     *            Phrase of a name ending to perform the selection
     * @return List of all files matching the phrase at its end
     */
    public static List<DavResource> getFilesByEnding(final List<DavResource> davResources, final String endingPhrase)
    {
        final List<DavResource> results = new ArrayList<DavResource>();

        if (davResources != null)
        {
            for (final DavResource resource : davResources)
            {
                if (resource.getName().toLowerCase().endsWith(endingPhrase.toLowerCase()) && !resource.isDirectory())
                {
                    results.add(resource);
                }
            }
        }

        return results;
    }

    /**
     * @param davResources
     *            List of DavResources with results of a performed ListAction or selection
     * @return List of all directories contained inside the list
     */
    public static List<DavResource> getAllDirectories(final List<DavResource> davResources)
    {
        final List<DavResource> results = new ArrayList<DavResource>();

        if (davResources != null)
        {
            for (final DavResource resource : davResources)
            {
                if (resource.isDirectory())
                {
                    results.add(resource);
                }
            }
        }

        return results;
    }

    /**
     * @param davResources
     *            List of DavResources with results of a performed ListAction or selection
     * @param contentType
     *            HTTP content type to perform a selection
     * @return List of all files matching with the content type
     */
    public static List<DavResource> getResourcesByContentType(final List<DavResource> davResources, final String contentType)
    {
        final List<DavResource> results = new ArrayList<DavResource>();

        if (davResources != null)
        {
            for (final DavResource resource : davResources)
            {
                if (resource.getContentType().toLowerCase().contains(contentType.toLowerCase()))
                {
                    results.add(resource);
                }
            }
        }

        return results;
    }

    /**
     * @param davResources
     *            List of DavResources with results of a performed ListAction or selection
     * @param matchPhrase
     *            Phrase which must be included in the results names
     * @return List of all resources matching the phrase
     */
    public static List<DavResource> getMatchingResources(final List<DavResource> davResources, final String matchPhrase)
    {
        final List<DavResource> results = new ArrayList<DavResource>();

        if (davResources != null)
        {
            for (final DavResource resource : davResources)
            {
                if (resource.getName().toLowerCase().contains(matchPhrase.toLowerCase()))
                {
                    results.add(resource);
                }
            }
        }

        return results;
    }

    /**
     * @param davResources
     *            List of DavResources with results of a performed ListAction or selection
     * @param matchPhrase
     *            Phrase of which must not be included in the results names
     * @return List of all resources not matching the phrase
     */
    public static List<DavResource> getNotMatchingResources(final List<DavResource> davResources, final String matchPhrase)
    {
        final List<DavResource> results = new ArrayList<DavResource>();

        if (davResources != null)
        {
            for (final DavResource resource : davResources)
            {
                if (!resource.getName().toLowerCase().contains(matchPhrase.toLowerCase()))
                {
                    results.add(resource);
                }
            }
        }

        return results;
    }

    /**
     * @param davResources
     *            List of DavResources with results of a performed ListAction or selection
     * @return Randomized result of the lists content
     */
    public static DavResource getRandom(final List<DavResource> davResources)
    {
        if (davResources == null)
        {
            return null;
        }
        else
        {
            return davResources.get(XltRandom.nextInt(davResources.size()));
        }
    }
}
