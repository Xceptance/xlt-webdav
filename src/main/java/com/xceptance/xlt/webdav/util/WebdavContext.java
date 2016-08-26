package com.xceptance.xlt.webdav.util;

import com.xceptance.xlt.api.engine.Session;

import java.util.HashMap;
import java.util.Map;

/**
 * Context class to perform basic action flow and logging. Internally every created WebdavAction is stored in a map
 * related to user id (Session.getCurrent().getUserID()). This will build an action chain of your "activeAction" and all
 * "previousAction"'s. You can access your actions by using this context and its .getActiveAction() method.
 *
 * @author @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public abstract class WebdavContext
{
    // UserID related storage for your action to be performed <String UserID, AbstractWebdavAction activeAction>
    private static Map<String, AbstractWebdavAction> activeActions = new HashMap<String, AbstractWebdavAction>();

    /**
     * Returns last created action related to session userID Called implicit by AbstractWebdavAction's constructor to
     * build an action chain
     *
     * @return Current WebdavAction related to userID
     */
    public static AbstractWebdavAction getActiveAction()
    {
        return WebdavContext.activeActions.get(Session.getCurrent().getUserID());
    }

    /**
     * Sets action as active action related to session userID Called implicit by AbstractWebdavAction's constructor to
     * build an action chain
     *
     * @param activeAction
     *            current WebdavAction which is getting to be performed
     */
    public static void setActiveAction(AbstractWebdavAction activeAction)
    {
        WebdavContext.activeActions.put(Session.getCurrent().getUserID(), activeAction);
    }

    /**
     * Shutdowns users sardine client and releases users "activeAction" (IMPORTANT after test case completion to avoid
     * endless chaining of actions and resulting memory leaks)
     */
    public static void clean()
    {
        WebdavContext.activeActions.get(Session.getCurrent().getUserID()).releaseClient();
        WebdavContext.activeActions.put(Session.getCurrent().getUserID(), null);
    }

    /**
     * Sets a host name to the last created webdav action and all following ones
     *
     * @param hostName
     *            Servers host name for expample "http://localhost/"
     */
    public static void setHostName(String hostName)
    {
        WebdavContext.activeActions.get(Session.getCurrent().getUserID()).setHostName(hostName);
    }

    /**
     * Sets a webdav home directory path to the last created webdav action and all following ones, otherwise the action
     * uses "" You can also set your favorite path, to shorten your input relative path's
     *
     * @param webdavDir
     *            Relative webdav home directory related to hostName, for example: "webdav/"
     */
    public static void setWebdavDir(String webdavDir)
    {
        WebdavContext.activeActions.get(Session.getCurrent().getUserID()).setWebdavDir(webdavDir);
    }

    /**
     * Sets user credentials to the last created action and all following and on this way also to the sardine client
     *
     * @param userName
     *            credential user name
     * @param userPassword
     *            credential user password
     */
    public static void setCredentials(String userName, String userPassword)
    {
        WebdavContext.activeActions.get(Session.getCurrent().getUserID()).setCredentials(userName, userPassword);
    }
}
