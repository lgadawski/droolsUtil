package com.gadawski.util.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * @author l.gadawski@gmail.com
 *
 */
public class HibernateUtil {

    /**
     * 
     */
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * @return
     */
    private static SessionFactory buildSessionFactory() {
        ServiceRegistry serviceRegistry = null;
        return new Configuration().configure().buildSessionFactory(
                serviceRegistry);
    }

    /**
     * @return session factory
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * close caches and connection pools
     */
    public static void shutdown() {
        getSessionFactory().close();
    }
}
