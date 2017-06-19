package org.shipstone.sandbox.postwithbody.rs;

import org.shipstone.sandbox.postwithbody.bean.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.MONTHS;

/**
 * @author François Robert
 */
@Path("user")
public class UserEndPoint {

  public static final String DATE_FORMAT = "yyyy-MM-dd";

  @GET()
  @Path("{userId:[0-9]*}")
  @Produces({
      "application/json", "application/xml"
  })
  public User getUser(@PathParam("userId") String userId) {
    User user = new User();
    user.setUsername("toto");
    user.setId(userId);
    LocalDate localDate = LocalDate.now(ZoneId.systemDefault());
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    user.setCreationDate(localDate.format(dateTimeFormatter));
    user.setExpirationDate(localDate.plus(2, MONTHS).format(dateTimeFormatter));
    return user;
  }

  @POST
  @Consumes("application/x-www-form-urlencoded")
  public Response createUser(@Context HttpHeaders httpHeaders, @FormParam("username") String username) {
    User user = new User();
    user.setUsername(username);
    LocalDate localDate = LocalDate.now(ZoneId.systemDefault());
    user.setId(localDate.format(DateTimeFormatter.ofPattern("yyqwd")));
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    user.setCreationDate(localDate.format(dateTimeFormatter));
    user.setExpirationDate(localDate.plus(2, MONTHS).format(dateTimeFormatter));
    return Response
        .created(UriBuilder.fromResource(UserEndPoint.class).path(user.getId()).build())
        .entity(httpHeaders.getAcceptableMediaTypes().contains(MediaType.WILDCARD_TYPE) ? null : user)
        .build();
  }

}
