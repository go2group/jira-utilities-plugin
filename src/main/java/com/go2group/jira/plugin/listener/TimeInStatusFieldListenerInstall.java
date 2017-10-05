package com.go2group.jira.plugin.listener;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.event.JiraListener;
import com.atlassian.jira.event.ListenerManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;

public class TimeInStatusFieldListenerInstall implements LifecycleAware, DisposableBean
{
    private static final Logger log = Logger.getLogger("TimeInStatusField Listener");

    public void onStart()
    {
        if (!isInstalled())
        {
            // Install listener
            
            try
            {
                ComponentManager.getComponent(ListenerManager.class).createListener("TimeInStatus Plugin: Calculate/set value for time-in-status fields", TimeInStatusFieldListener.class);
            }
            catch (Exception e)
            {
                log.error("Error adding listener: " + e.toString() + ".");
            }
        }
    }
    
    private boolean isInstalled()
    {
        // Check to see if the listener is installed
        
        Map<String,JiraListener> m = ComponentManager.getComponent(ListenerManager.class).getListeners();
        for (Map.Entry<String,JiraListener> entry : m.entrySet())
        {
            if (entry.getValue().getClass().getName().equals(TimeInStatusFieldListener.class.getName()))
                return true;
        }
        
        return false;
    }
    
    /**
     * Called when plugin is uninstalled and when JIRA is shutdown
     */
    public void destroy() throws Exception
    {
//        // Remove the listener (note that config settings will be lost)
//        ComponentManager.getComponent(ListenerManager.class).deleteListener(TimeInStatusFieldListener.class);
    }
}