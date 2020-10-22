package it.vige.school.web;

import it.vige.school.ModuleException;
import it.vige.school.RestCaller;
import it.vige.school.SchoolModule;
import it.vige.school.dto.Presence;
import it.vige.school.dto.User;
import org.jboss.logging.Logger;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.vige.school.Constants.ERROR;
import static it.vige.school.Constants.MAX_USERS;
import static it.vige.school.Utils.getCalendarByDate;
import static java.util.stream.Collectors.toList;
import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import static javax.faces.context.FacesContext.getCurrentInstance;
import static org.jboss.logging.Logger.getLogger;

@SessionScoped
@Named
public class Users extends RestCaller implements Serializable, Converters {

	private static final long serialVersionUID = -2260430424205388307L;

	private static Logger log = getLogger(Users.class);

	@EJB
	private SchoolModule schoolModule;

	@Inject
	private Configuration configuration;

	@Inject
	private Report report;

	private List<User> users;

	private List<User> filteredUsers;

	private List<String> rooms;

	private List<String> schools;

	private List<School> allSchools;

	private boolean initialized;

	public void init(boolean force) {
		try {
			if (!initialized || force) {

				String url = configuration.getAuthServerUrl() + "/realms/" + configuration.getRealm()
						+ "/rooms/schools";
				Response response = get(configuration.getAccessToken(), url, null);
				allSchools = response.readEntity(new GenericType<List<School>>() {
				});
				response.close();
				log.debug("school found: " + allSchools);

				boolean isAdmin = configuration.isAdmin();
				if (isAdmin) {
					users = findAllUsers();
				} else {
					User currentUser = findUserById(configuration.getUser());
					if (configuration.isSchoolOperator())
						users = findUsersBySchool(currentUser.getSchool());
					else {
						users = findUsersBySchoolAndRoom(currentUser.getSchool(), currentUser.getRoom());
					}
				}
				Calendar currentDay = getCalendarByDate(configuration.getCurrentDay());
				List<Presence> presencesOfDay = schoolModule.findPresencesByDay(currentDay);
				users.forEach(x -> {
					for (Presence presence : presencesOfDay)
						if (presence.getUser().equals(x) && presence.getDay().equals(currentDay))
							x.setPresent(true);
				});
				if (filteredUsers != null)
					filteredUsers.forEach(x -> {
						for (User user : users)
							if (user.getId() == x.getId())
								x.setPresent(user.isPresent());
					});
				rooms = users.stream().filter(x -> x.getRoom() != null && !x.getRoom().isEmpty()).map(x -> x.getRoom())
						.distinct().sorted().collect(toList());
				schools = users.stream().filter(x -> x.getSchool() != null && !x.getSchool().isEmpty())
						.map(x -> x.getSchool()).distinct().sorted().collect(toList());

				initialized = true;
			}
		} catch (ModuleException ex) {
			FacesContext currentInstance = getCurrentInstance();
			if (currentInstance != null) {
				FacesMessage message = new FacesMessage(SEVERITY_INFO, // severity
						ERROR, ERROR);
				currentInstance.addMessage(ERROR, message);
			}
		}
	}

	public List<User> findAllUsers() throws ModuleException {
		try {
			String url = configuration.getAuthServerUrl() + "/admin/realms/" + configuration.getRealm() + "/users";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("max", MAX_USERS);
			Response response = get(configuration.getAccessToken(), url, params);
			List<UserRepresentation> userRepresentationList = response
					.readEntity(new GenericType<List<UserRepresentation>>() {
					});
			response.close();
			log.debug("user found: " + userRepresentationList);

			List<User> userList = userRepresentationList.stream().map(t -> UserRepresentationToUser.apply(t))
					.filter(z -> z.getRoom() != null && !z.getRoom().isBlank()).collect(toList());
			userList.forEach(x -> {
				addSchoolDescription(x);
			});

			return userList;
		} catch (Exception e) {
			String message = "Cannot find user";
			throw new ModuleException(message, e);
		}
	}

	public List<User> findUsersByRoom(String room) throws ModuleException {
		if (room != null) {
			try {
				List<User> userList = findAllUsers();
				log.debug("user found: " + userList);
				return userList.stream().filter(t -> room.equals(t.getRoom())).collect(toList());
			} catch (Exception e) {
				String message = "Cannot find user by room " + room;
				throw new ModuleException(message, e);
			}
		} else {
			throw new IllegalArgumentException("room cannot be null");
		}
	}

	public List<User> findUsersBySchool(String school) throws ModuleException {
		if (school != null) {
			try {
				List<User> userList = findAllUsers();
				log.debug("user found: " + userList);
				return userList.stream().filter(t -> school.equals(t.getSchool())).collect(toList());
			} catch (Exception e) {
				String message = "Cannot find user by room " + school;
				throw new ModuleException(message, e);
			}
		} else {
			throw new IllegalArgumentException("room cannot be null");
		}
	}

	public List<User> findUsersBySchoolAndRoom(String school, String room) throws ModuleException {
		if (school != null) {
			try {
				List<User> userList = findAllUsers();
				log.debug("user found: " + userList);
				return userList.stream().filter(t -> school.equals(t.getSchool()) && room.equals(t.getRoom()))
						.collect(toList());
			} catch (Exception e) {
				String message = "Cannot find user by school " + school;
				throw new ModuleException(message, e);
			}
		} else {
			throw new IllegalArgumentException("room cannot be null");
		}
	}

	public User findUserById(String id) throws ModuleException {
		if (id != null) {
			UserRepresentation user = null;
			try {
				String url = configuration.getAuthServerUrl() + "/admin/realms/" + configuration.getRealm() + "/users"
						+ "?username=" + id;
				Response response = get(configuration.getAccessToken(), url, null);
				user = response.readEntity(new GenericType<List<UserRepresentation>>() {
				}).get(0);
				response.close();
			} catch (Exception e) {
				try {
					String url = configuration.getAuthServerUrl() + "/admin/realms/" + configuration.getRealm()
							+ "/users" + "/" + id;
					Response response = get(configuration.getAccessToken(), url, null);
					user = response.readEntity(UserRepresentation.class);
					response.close();
				} catch (Exception ex) {
					String message = "Cannot find user by id " + id;
					throw new ModuleException(message, ex);
				}
			}
			log.debug("user found: " + user);
			User result = UserRepresentationToUser.apply(user);

			addSchoolDescription(result);
			return result;
		} else {
			throw new IllegalArgumentException("room cannot be null");
		}
	}

	public List<User> getUsers() {
		return users;
	}

	public List<User> getFilteredUsers() {
		return filteredUsers;
	}

	public void setFilteredUsers(List<User> filteredUsers) {
		this.filteredUsers = filteredUsers;
	}

	public List<String> getRooms() {
		log.debug("get rooms: " + rooms);
		return rooms;
	}

	public List<String> getSchools() {
		log.debug("schools: " + schools);
		return schools;
	}

	public void refresh() {
		init(true);
	}

	public void report() throws IOException {
		log.debug("report");
		report.init();
		report.setFilteredUsers(null);
		configuration.redirect("/views/report.xhtml");
	}

	private void addSchoolDescription(User user) {
		if (allSchools != null)
			for (School school : allSchools)
				if (school.getId().equals(user.getSchool()))
					user.setSchool(school.getDescription());
	}
}
