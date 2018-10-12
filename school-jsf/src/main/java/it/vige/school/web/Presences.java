package it.vige.school.web;

import static it.vige.school.Utils.getCalendarByDate;
import static org.jboss.logging.Logger.getLogger;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import it.vige.school.ModuleException;
import it.vige.school.SchoolModule;
import it.vige.school.dto.Presence;
import it.vige.school.dto.Pupil;

@SessionScoped
@Named
public class Presences implements Serializable {

	private static final long serialVersionUID = -2260430424205388307L;

	private static Logger log = getLogger(Presences.class);

	@Inject
	private SchoolModule schoolModule;

	@Inject
	private Configuration configuration;

	public void addPresence(Pupil pupil) throws ModuleException {
		Presence presence = new Presence();
		presence.setPupil(pupil);
		presence.setDay(getCalendarByDate(configuration.getCurrentDay()));
		if (pupil.isPresent())
			schoolModule.createPresence(presence);
		else
			schoolModule.removePresence(presence);
		log.debug("pupil: " + pupil);
	}
}