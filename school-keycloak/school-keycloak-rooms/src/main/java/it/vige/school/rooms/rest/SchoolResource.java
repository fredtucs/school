package it.vige.school.rooms.rest;

import it.vige.school.Constants;
import it.vige.school.rooms.School;
import it.vige.school.rooms.spi.RoomsService;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.created;
import static javax.ws.rs.core.Response.noContent;
import static org.keycloak.services.ErrorResponse.exists;

public class SchoolResource {

    private static final Logger logger = Logger.getLogger(SchoolResource.class);
    private final KeycloakSession session;

    @Inject
    public SchoolResource(KeycloakSession session) {
        this.session = session;
    }

    @GET
    @Path("")
    @NoCache
    @Produces(APPLICATION_JSON)
    public List<School> findSchools(@QueryParam("search") String search, @QueryParam("first") Integer firstResult,
                                    @QueryParam("max") Integer maxResults, @QueryParam("briefRepresentation") Boolean briefRepresentation) {
        AuthCheck.whoAmI(session);
        return session.getProvider(RoomsService.class).findSchools(search, firstResult, maxResults, briefRepresentation);
    }

    @GET
    @Path("list")
    @NoCache
    @Produces(APPLICATION_JSON)
    public List<School> listSchools() {
        AuthCheck.whoAmI(session);
        return session.getProvider(RoomsService.class).findAllSchools();
    }

    @POST
    @Path("")
    @NoCache
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response createSchool(final School school) {
        try {
            AuthCheck.hasRole(session, Constants.ADMIN_ROLE);
            if (school.getId() == null)
                school.setId(generateId(school.getDescription()));
            School createdSchool = session.getProvider(RoomsService.class).createSchool(school);

            if (session.getTransactionManager().isActive()) {
                session.getTransactionManager().commit();
            }
            return created(session.getContext().getUri().getAbsolutePathBuilder().path(createdSchool.getId()).build())
                    .build();
        } catch (Exception ex) {
            if (session.getTransactionManager().isActive()) {
                session.getTransactionManager().setRollbackOnly();
            }
            logger.error("ERROR! ", ex);
            return exists("Could not create school!");
        }
    }

    /**
     * Update the school
     *
     * @param school the school to update
     * @return the rest response
     */
    @PUT
    @Path("{school.id}")
    @NoCache
    @Consumes(APPLICATION_JSON)
    public Response updateSchool(final School school) {

        try {
            AuthCheck.hasRole(session, Constants.ADMIN_ROLE);
            session.getProvider(RoomsService.class).updateSchool(school);

            if (session.getTransactionManager().isActive()) {
                session.getTransactionManager().commit();
            }
            return noContent().build();
        } catch (Exception me) { // JPA
            return exists("Could not update school!");
        }
    }

    @DELETE
    @Path("{school}")
    @NoCache
    public Response removeSchool(@PathParam("school") final String schoolId) {
        AuthCheck.hasRole(session, Constants.ADMIN_ROLE);
        School school = new School();
        school.setId(schoolId);
        session.getProvider(RoomsService.class).removeSchool(school);
        return created(session.getContext().getUri().getAbsolutePathBuilder().path(school.getId()).build()).build();
    }

    @GET
    @NoCache
    @Path("{school}")
    @Produces(APPLICATION_JSON)
    public School findSchoolById(@PathParam("school") final String school) {
        AuthCheck.whoAmI(session);
        return session.getProvider(RoomsService.class).findSchoolById(school);
    }

    private String generateId(String description) {
        return description.replaceAll("[-+.^ :,']", "").toLowerCase();
    }

}