package app.bpartners.geojobs.endpoint.rest.security.authentication.apikey;

import static app.bpartners.geojobs.endpoint.rest.security.model.Authority.Role.ROLE_ADMIN;

import app.bpartners.geojobs.endpoint.rest.security.model.Authority;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyAuthenticator implements Function<HttpServletRequest, Authentication> {
  public static final String APIKEY_HEADER_NAME = "x-api-key";
  private final String adminApiKey;

  public ApiKeyAuthenticator(@Value("${admin.api.key}") String adminApiKey) {
    this.adminApiKey = adminApiKey;
  }

  @Override
  public Authentication apply(HttpServletRequest request) {
    String candidateApiKey = request.getHeader(APIKEY_HEADER_NAME);
    if (!adminApiKey.equals(candidateApiKey)) {
      throw new BadCredentialsException("Invalid API Key");
    }

    return new ApiKeyAuthentication(candidateApiKey, Set.of(new Authority(ROLE_ADMIN)));
  }
}
