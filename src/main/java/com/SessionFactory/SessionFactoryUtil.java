package com.SessionFactory;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryUtil {

	private static SessionFactory sessionFactory;

	// Private constructor to prevent instantiation
	private SessionFactoryUtil() {
	}

	// Static method to get the SessionFactory
	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			try {

//            	System.out.println("Initializing SessionFactory...");

				sessionFactory = new Configuration().configure().buildSessionFactory();

//                System.out.println("SessionFactory Initialized Successfully.");

			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("There was a problem building the SessionFactory.");
			}
		}
		return sessionFactory;
	}

	// Method to close the SessionFactory
	public static void shutdown() {
		if (sessionFactory != null) {
			sessionFactory.close();
		}
	}
}
