package com.xceptance.xlt.webdav.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.webdav.impl.AbstractWebDavAction;

/**
 * Context class to perform basic action flow and logging. Internally every created WebdavAction is stored in a map
 * related to user id (Session.getCurrent().getUserID()). This will build an action chain of your "activeAction" and all
 * "previousAction"'s. You can access your actions by using this context and its .getActiveAction() method.
 *
 * @author @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public abstract class WebDavContext
{
    /**
     * UserID related storage for your action to be performed <String UserID, AbstractWebdavAction activeAction>
     */
    private static final Map<String, AbstractWebDavAction<?>> activeActions = new ConcurrentHashMap<>();

    /**
     * Returns last created action related to session userID Called implicit by AbstractWebdavAction's constructor to
     * build an action chain
     *
     * @return Current WebdavAction related to userID
     */
    public static AbstractWebDavAction<?> getActiveAction()
    {
        return activeActions.get(Session.getCurrent().getUserID());
    }

    /**
     * Sets action as active action related to session userID Called implicit by AbstractWebdavAction's constructor to
     * build an action chain
     *
     * @param activeAction
     *            current WebdavAction which is getting to be performed
     */
    public static void setActiveAction(final AbstractWebDavAction<?> activeAction)
    {
        activeActions.put(Session.getCurrent().getUserID(), activeAction);
    }

    /**
     * Shutdowns users sardine client and releases users "activeAction" (IMPORTANT after test case completion to avoid
     * endless chaining of actions and resulting memory leaks)
     *
     * @throws IOException
     */
    public static void cleanUp() throws IOException
    {
        final AbstractWebDavAction<?> action = activeActions.remove(Session.getCurrent().getUserID());
        if (action != null)
        {
            action.releaseClient();
        }
    }
}
