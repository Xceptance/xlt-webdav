package com.xceptance.xlt.webdav.util;

import java.util.ArrayList;
import java.util.List;

import com.github.sardine.DavResource;
import com.xceptance.xlt.api.util.XltRandom;

/**
 * Utility class to filter a list of {@link DavResource} objects according various criteria.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public abstract class DavResourceListUtils
{
    /**
     * Filters the given list of {@link DavResource} objects and returns only those resources that denote files.
     *
     * @param davResources
     *            the list of resources to filter
     * @return a list of all file resources in the input list
     */
    public static List<DavResource> getAllFileResources(final List<DavResource> davResources)
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
     * Filters the given list of {@link DavResource} objects and returns only those resources that denote files and end
     * with a certain suffix.
     *
     * @param davResources
     *            the list of resources to filter
     * @param suffix
     *            Phrase of a name ending to perform the selection
     * @return List of all files matching the phrase at its end
     */
    public static List<DavResource> getFilesByEnding(final List<DavResource> davResources, final String suffix)
    {
        final List<DavResource> results = new ArrayList<DavResource>();

        if (davResources != null)
        {
            for (final DavResource resource : davResources)
            {
                if (!resource.isDirectory() && resource.getName().toLowerCase().endsWith(suffix.toLowerCase()))
                {
                    results.add(resource);
                }
            }
        }

        return results;
    }

    /**
     * Filters the given list of {@link DavResource} objects and returns only those resources that denote directories.
     *
     * @param davResources
     *            the list of resources to filter
     * @return a list of all directory resources in the input list
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
     * Filters the given list of {@link DavResource} objects and returns only those resources that denote files and end
     * with a certain suffix.
     *
     * @param davResources
     *            the list of resources to filter
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
     *            the list of resources to filter
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
     *            the list of resources to filter
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
     *            the list of resources to filter
     * @return Randomized result of the lists content
     */
    public static DavResource getRandom(final List<DavResource> davResources)
    {
        if (davResources == null || davResources.isEmpty())
        {
            return null;
        }
        else
        {
            return davResources.get(XltRandom.nextInt(davResources.size()));
        }
    }
}
