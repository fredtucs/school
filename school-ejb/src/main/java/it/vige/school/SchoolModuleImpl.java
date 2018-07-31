package it.vige.school;

import static org.jboss.logging.Logger.getLogger;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.jboss.logging.Logger;

import it.vige.school.model.Pupil;

@Stateless
public class SchoolModuleImpl implements SchoolModule {

	private static Logger log = getLogger(SchoolModuleImpl.class);

	@PersistenceContext(unitName = "school")
	private EntityManager em;

	@Override
	public List<Pupil> findAllPupils() throws ModuleException {
		try {
			TypedQuery<Pupil> query = em.createNamedQuery("findPupilsBySchool", Pupil.class);
			List<Pupil> pupilList = query.getResultList();
			if (pupilList == null) {
				throw new ModuleException("No pupil found");
			}
			log.debug("pupil found: " + pupilList);
			return pupilList;
		} catch (Exception e) {
			String message = "Cannot find pupil";
			throw new ModuleException(message, e);
		}
	}

	@Override
	public List<Pupil> findPupilsByRoom(String room) throws ModuleException {
		if (room != null) {
			try {
				TypedQuery<Pupil> query = em.createNamedQuery("findPupilsByRoom", Pupil.class);
				query.setParameter("room", room);
				List<Pupil> pupilList = query.getResultList();
				if (pupilList == null) {
					throw new ModuleException("No pupil found for " + room);
				}
				log.debug("pupil found: " + pupilList);
				return pupilList;
			} catch (Exception e) {
				String message = "Cannot find pupil by room " + room;
				throw new ModuleException(message, e);
			}
		} else {
			throw new IllegalArgumentException("room cannot be null");
		}
	}

	@Override
	public List<Pupil> findPupilsBySchool(String school) throws ModuleException {
		if (school != null) {
			try {
				TypedQuery<Pupil> query = em.createNamedQuery("findPupilsBySchool", Pupil.class);
				query.setParameter("school", school);
				List<Pupil> pupilList = query.getResultList();
				if (pupilList == null) {
					throw new ModuleException("No pupil found for " + school);
				}
				log.debug("pupil found: " + pupilList);
				return pupilList;
			} catch (Exception e) {
				String message = "Cannot find pupil by room " + school;
				throw new ModuleException(message, e);
			}
		} else {
			throw new IllegalArgumentException("room cannot be null");
		}
	}

	@Override
	public Pupil createPupil(String name, String surname, String room, String school) throws ModuleException {
		Pupil pupil = new Pupil();
		pupil.setName(name);
		pupil.setRoom(room);
		pupil.setSchool(school);
		pupil.setSurname(surname);
		em.persist(pupil);
		log.debug("pupil created: " + pupil);
		return pupil;
	}

	@Override
	public void removePupil(int id) throws ModuleException {
		Pupil pupil = em.find(Pupil.class, id);
		em.remove(pupil);
		log.debug("pupil removed: " + pupil);
	}

}