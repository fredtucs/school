package it.vige.school.rooms.rest;

import org.jboss.logging.Logger;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RoleModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import java.util.Objects;

public class AuthCheck {
    private static final Logger log = Logger.getLogger(AuthCheck.class);

    public static void whoAmI(KeycloakSession session) {
        final AuthenticationManager.AuthResult authResult = abortIfNotAuthenticated(session);
        if (authResult == null) {
            log.infof("Anonymous user entering realm %s", session.getContext().getRealm().getName());
        } else {
            ClientModel client = session.getContext().getRealm().getClientByClientId(authResult.getToken().getIssuedFor());
            log.infof("%s, realm: %s, client: %s", authResult.getUser().getUsername(), session.getContext().getRealm().getName(), client.getClientId());
        }
    }

    public static void hasRole(KeycloakSession session, String role) {
        final AuthenticationManager.AuthResult authResult = abortIfNotAuthenticated(session);

        RoleModel roleModel = session.realms().getRealmRole(session.getContext().getRealm(), role);

        if (Objects.isNull(roleModel)) {
            ClientModel client = session.getContext().getRealm().getClientByClientId(authResult.getToken().getIssuedFor());
            roleModel = session.realms().getClientRole(session.getContext().getRealm(), client, role);
        }

        if (!authResult.getUser().hasRole(roleModel)) {
            throw new ForbiddenException("You do not have the required credentials for this action");
        }
    }

    public static AuthenticationManager.AuthResult abortIfNotAuthenticated(KeycloakSession session) {
        final AuthenticationManager.AuthResult authResult = new AppAuthManager().authenticateBearerToken(session);
        abortIfNotAuthenticated(authResult);
        return authResult;
    }

    public static void abortIfNotAuthenticated(AuthenticationManager.AuthResult authResult) {
        if (authResult == null) {
            throw new NotAuthorizedException("Bearer token required");
        }
    }
}