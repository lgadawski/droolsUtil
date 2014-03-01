package com.gadawski.util.common;

/**
 * Config class that holds global configuration.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public final class MyAppConfig {
    /**
     * Indicates if rule engine should use database.
     */
    public static boolean USE_DB = true;
    
    /**
     * Prevent from creating objects.
     */
    private MyAppConfig() {
    }
}
