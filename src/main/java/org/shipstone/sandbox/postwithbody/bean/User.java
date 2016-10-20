package org.shipstone.sandbox.postwithbody.bean;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

/**
 * @author François Robert
 */
@Data
@XmlRootElement
@XmlAccessorType(FIELD)
public class User {

  @XmlAttribute
  private String id;

  private String username;

  /**
   * Date au format YYYY-MM-DD
   */
  private String creationDate;

  /**
   * Date au format YYYY-MM-DD
   */
  private String expirationDate;

}
