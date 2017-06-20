package org.shipstone.sandbox.postwithbody.rs;

import org.shipstone.sandbox.postwithbody.bean.User;
import org.shipstone.sandbox.postwithbody.rs.bean.model.UserModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.MONTHS;

/**
 * @author François Robert
 */
@RestController
@RequestMapping("api/user")
public class UserEndPoint {

  public static final String DATE_FORMAT = "yyyy-MM-dd";

  @GetMapping(
      path = "{userId:[0-9]*}",
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
  )
  public User getUser(@PathVariable("userId") String userId) {
    User user = new User();
    user.setUsername("toto");
    user.setId(userId);
    LocalDate localDate = LocalDate.now(ZoneId.systemDefault());
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    user.setCreationDate(localDate.format(dateTimeFormatter));
    user.setExpirationDate(localDate.plus(2, MONTHS).format(dateTimeFormatter));
    return user;
  }

  @PostMapping
  public ResponseEntity<User> createUser(
      UriComponentsBuilder uriComponentsBuilder,
      @RequestHeader HttpHeaders requestHttpHeaders,
      @ModelAttribute("userModel") UserModel userModel
  ) {
    User user = new User();
    user.setUsername(userModel.getUsername());
    LocalDate localDate = LocalDate.now(ZoneId.systemDefault());
    user.setId(localDate.format(DateTimeFormatter.ofPattern("yyqwd")));
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    user.setCreationDate(localDate.format(dateTimeFormatter));
    user.setExpirationDate(localDate.plus(2, MONTHS).format(dateTimeFormatter));
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(
        uriComponentsBuilder.path("/api/user/{id}")
            .buildAndExpand(user.getId()).toUri()
    );
    return new ResponseEntity<>(
        (
            requestHttpHeaders.getAccept().size() == 0 || requestHttpHeaders.getAccept().contains(MediaType.ALL)
                ? null : user
        ),
        httpHeaders,
        HttpStatus.CREATED
    );
  }

}
