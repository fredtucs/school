package it.vige.school;

import static java.util.Calendar.getInstance;
import static javax.security.jacc.PolicyContext.getContext;
import static org.jboss.logging.Logger.getLogger;

import java.util.Calendar;
import java.util.Date;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContextException;

import org.jboss.logging.Logger;

public class Utils {

	private final static String SUBJECT_CONTAINER = "javax.security.auth.Subject.container";

	private static Logger log = getLogger(Utils.class);

	public static String getCurrentRole() {
		String role = null;
		try {
			Subject subject;
			subject = (Subject) getContext(SUBJECT_CONTAINER);
			role = subject.getPrincipals().toArray()[1].toString();
			role = role.substring(role.indexOf(":") + 1, role.indexOf(")"));
		} catch (PolicyContextException e) {
			log.error(e);
		}
		return role;
	}
	
	public static Calendar getCurrentDay() {
		Calendar day = getInstance();
		day.setTime(new Date());
		return day;
	}
}