package it.vige.school.rooms.rest;

import org.keycloak.models.KeycloakSession;

import javax.inject.Inject;
import javax.ws.rs.Path;

public class RoomsRestResource {

    private final KeycloakSession session;

    @Inject
    public RoomsRestResource(KeycloakSession session) {
        this.session = session;
    }

    @Path("rooms")
    public RoomResource getRoomResource() {
        return new RoomResource(session);
    }

    @Path("schools")
    public SchoolResource getSchoolResource() {
        return new SchoolResource(session);
    }

}
