/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author jonas.eicher
 */
@Entity
@Table(name = "stammdaten3")
@Data
public class Bestand implements Serializable {

  @Id
  @Column(name = "matnr")
  String matnr;
  @Column(name = "bestand2016")
  int bestand2016;
//  @Column(name = "bestand2015")
//  int bestand2015;
}
