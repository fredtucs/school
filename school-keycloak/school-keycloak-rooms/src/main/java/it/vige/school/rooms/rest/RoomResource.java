package it.vige.school.rooms.rest;

import it.vige.school.Constants;
import it.vige.school.rooms.Room;
import it.vige.school.rooms.spi.RoomsService;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class RoomResource {

	private final KeycloakSession session;


	@Inject
	public RoomResource(KeycloakSession session) {
		this.session = session;
	}

	@GET
	@Path("")
	@NoCache
	@Produces(APPLICATION_JSON)
	public List<Room> findAllRooms() {
		AuthCheck.whoAmI(session);
		return session.getProvider(RoomsService.class).findAllRooms();
	}

	@GET
	@NoCache
	@Path("{school}")
	@Produces(APPLICATION_JSON)
	public List<Room> findRoomsBySchool(@PathParam("school") final String school) {
		AuthCheck.whoAmI(session);
		return session.getProvider(RoomsService.class).findRoomsBySchool(school);
	}

	@POST
	@Path("")
	@NoCache
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public Room createRoom(Room room) {
		AuthCheck.hasRole(session, Constants.ADMIN_ROLE);
		return session.getProvider(RoomsService.class).createRoom(room);
	}

	@DELETE
	@Path("")
	@NoCache
	@Consumes(APPLICATION_JSON)
	public Response removeRoom(Room room) {
		AuthCheck.hasRole(session, Constants.ADMIN_ROLE);
		session.getProvider(RoomsService.class).removeRoom(room);
		return Response.created(session.getContext().getUri().getAbsolutePathBuilder()
				.path(room.getClazz() + "-" + room.getSection() + "-" + room.getSchool()).build()).build();
	}

}