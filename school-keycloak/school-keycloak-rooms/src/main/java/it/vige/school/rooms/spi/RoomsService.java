package it.vige.school.rooms.spi;

import java.util.List;

import org.keycloak.provider.Provider;

import it.vige.school.rooms.Room;
import it.vige.school.rooms.School;

public interface RoomsService extends Provider {

	List<School> findAllSchools();

	List<Room> findAllRooms();

	List<School> findSchools(String search, Integer firstResult, Integer maxResults, Boolean briefRepresentation);

	School findSchoolById(String school);

	List<Room> findRoomsBySchool(String school);

	Room createRoom(Room room);

	School createSchool(School school);

	School updateSchool(School school);

	void removeRoom(Room room);

	void removeSchool(School school);
}
