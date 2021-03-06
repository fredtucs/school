package it.vige.school.web;

import static org.jboss.logging.Logger.getLogger;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;

@WebFilter("/TokenFilter")
public class TokenFilter extends HttpFilter {

	private static final long serialVersionUID = 7773284531545037448L;

	@Inject
	private Users users;

	@Inject
	private Configuration configuration;

	private static Logger log = getLogger(TokenFilter.class);

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.debug("Started filter");
		RefreshableKeycloakSecurityContext keycloakSecurityContext = getSession(request);
		String accessToken = keycloakSecurityContext.getTokenString();
		configuration.setClient(keycloakSecurityContext.getIdToken().getAudience()[0]);
		configuration.setUser(request.getUserPrincipal().getName());
		configuration.setRequest(request);
		configuration.setAccessToken(accessToken);
		configuration.setRealm(keycloakSecurityContext.getRealm());
		configuration.setAuthServerUrl(keycloakSecurityContext.getDeployment().getAuthServerBaseUrl());
		users.init(false);
		chain.doFilter(request, response);
	}

	private RefreshableKeycloakSecurityContext getSession(ServletRequest req) {
		return (RefreshableKeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
	}
}
