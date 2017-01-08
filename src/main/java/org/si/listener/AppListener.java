package org.si.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Application Lifecycle Listener implementation class AppListener
 *
 */
public class AppListener implements ServletContextListener {
    private static JedisPool pool;
    //private static ThreadHelper helper;
	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce) {
    	pool = new JedisPool(new JedisPoolConfig(), "localhost");
    	/*helper = ThreadHelper.getInstance();
    	helper.setPool(pool);*/
        sce.getServletContext().setAttribute("pool", pool);
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce) {
    	/*if(helper != null) {
    		helper.setPool(null);
        	helper = null;
    	}*/
        if(pool != null) {
        	pool.destroy();
        }
    }
	
}
