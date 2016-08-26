package com.xceptance.xlt.webdav.util;

import com.github.sardine.DavResource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility to get specific results out of a List of DavResources.
 *
 * @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public abstract class ListSelector
{

    /**
     * @param davResources
     *            List of DavResources with results of a performed ListAction or selection
     * @return List of all files contained inside the source list
     */
    public static List<DavResource> getAllFiles(List<DavResource> davResources)
    {

        List<DavResource> results = new ArrayList<DavResource>();

        davResources = (davResources == null) ? new ArrayList<DavResource>() : davResources;
        for (DavResource resource : davResources)
        {
            if (!resource.isDirectory())
                results.add(resource);
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
    public static List<DavResource> getFilesByEnding(List<DavResource> davResources, String endingPhrase)
    {

        List<DavResource> results = new ArrayList<DavResource>();

        davResources = (davResources == null) ? new ArrayList<DavResource>() : davResources;
        for (DavResource resource : davResources)
        {
            if (resource.getName().toLowerCase().endsWith(endingPhrase.toLowerCase()) && !resource.isDirectory())
                results.add(resource);
        }

        return results;
    }

    /**
     * @param davResources
     *            List of DavResources with results of a performed ListAction or selection
     * @return List of all directories contained inside the list
     */
    public static List<DavResource> getAllDirectories(List<DavResource> davResources)
    {

        List<DavResource> results = new ArrayList<DavResource>();

        davResources = (davResources == null) ? new ArrayList<DavResource>() : davResources;
        for (DavResource resource : davResources)
        {
            if (resource.isDirectory())
                results.add(resource);
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
    public static List<DavResource> getResourcesByContentType(List<DavResource> davResources, String contentType)
    {

        List<DavResource> results = new ArrayList<DavResource>();

        davResources = (davResources == null) ? new ArrayList<DavResource>() : davResources;
        for (DavResource resource : davResources)
        {
            if (resource.getContentType().toLowerCase().contains(contentType.toLowerCase()))
                results.add(resource);
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
    public static List<DavResource> getMatchingResources(List<DavResource> davResources, String matchPhrase)
    {

        List<DavResource> results = new ArrayList<DavResource>();

        davResources = (davResources == null) ? new ArrayList<DavResource>() : davResources;
        for (DavResource resource : davResources)
        {
            if (resource.getName().toLowerCase().contains(matchPhrase.toLowerCase()))
                results.add(resource);
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
    public static List<DavResource> getNotMatchingResources(List<DavResource> davResources, String matchPhrase)
    {

        List<DavResource> results = new ArrayList<DavResource>();

        davResources = (davResources == null) ? new ArrayList<DavResource>() : davResources;
        for (DavResource resource : davResources)
        {
            if (!resource.getName().toLowerCase().contains(matchPhrase.toLowerCase()))
                results.add(resource);
        }

        return results;
    }

    /**
     * @param davResources
     *            List of DavResources with results of a performed ListAction or selection
     * @return Randomized result of the lists content
     */
    public static DavResource getRandom(List<DavResource> davResources)
    {

        Random random = new Random();

        davResources = (davResources == null) ? new ArrayList<DavResource>() : davResources;
        return (davResources.size() == 0) ? null : davResources.get(random.nextInt(davResources.size()));
    }
}
