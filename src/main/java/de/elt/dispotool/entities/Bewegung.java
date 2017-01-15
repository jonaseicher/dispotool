/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Data;

/**
 *
 * @author jonas.eicher
 */
@Entity
@Table(name = "bewegungsdaten2")
@Data
public class Bewegung implements Serializable {

  @Id
  @Column(name = "id")
  int id;
  @Column(name = "materialnummer")
  String materialnummer;
  @Column(name = "lagerort")
  int lagerort;
  @Column(name = "bewegungsart")
  String bewegungsart;
  @Column
  Date buchungsdatum;
  @Column
  int menge;

  @Transient
  SimpleDateFormat format = new SimpleDateFormat("yyyy,MM,dd");
  
  public String getDateString() {
      return format.format(buchungsdatum);
  }
}
