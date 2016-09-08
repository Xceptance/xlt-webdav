package com.xceptance.xlt.webdav.util;

import com.xceptance.xlt.api.engine.Session;
import com.xceptance.xlt.webdav.impl.AbstractWebDAVAction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Context class to perform basic action flow and logging. Internally every created WebdavAction is stored in a map
 * related to user id (Session.getCurrent().getUserID()). This will build an action chain of your "activeAction" and all
 * "previousAction"'s. You can access your actions by using this context and its .getActiveAction() method.
 *
 * @author @author Karsten Sommer (Xceptance Software Technologies GmbH)
 */
public abstract class WebDAVContext
{
    // UserID related storage for your action to be performed <String UserID, AbstractWebdavAction activeAction>
    private static Map<String, AbstractWebDAVAction> activeActions = new HashMap<String, AbstractWebDAVAction>();

    /**
     * Returns last created action related to session userID Called implicit by AbstractWebdavAction's constructor to
     * build an action chain
     *
     * @return Current WebdavAction related to userID
     */
    public static AbstractWebDAVAction getActiveAction()
    {
        return WebDAVContext.activeActions.get(Session.getCurrent().getUserID());
    }

    /**
     * Sets action as active action related to session userID Called implicit by AbstractWebdavAction's constructor to
     * build an action chain
     *
     * @param activeAction
     *            current WebdavAction which is getting to be performed
     */
    public static void setActiveAction(AbstractWebDAVAction activeAction)
    {
        WebDAVContext.activeActions.put(Session.getCurrent().getUserID(), activeAction);
    }

    /**
     * Shutdowns users sardine client and releases users "activeAction" (IMPORTANT after test case completion to avoid
     * endless chaining of actions and resulting memory leaks)
     */
    public static void clean()
    {
        try 
        {
			WebDAVContext.activeActions.get(Session.getCurrent().getUserID()).releaseClient();
		} 
        catch (IOException e) 
        {
		}
        WebDAVContext.activeActions.put(Session.getCurrent().getUserID(), null);
    }
}
