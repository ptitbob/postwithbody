package org.shipstone.sandbox.postwithbody.bean;

import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @author François Robert
 */
@Data
@XmlRootElement
public class User {

  @XmlAttribute
  private String id;

  private String username;

  private Date creationDate;

  private Date expirationDate;

}
