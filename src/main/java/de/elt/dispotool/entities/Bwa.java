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
@Table(name = "bwa2")
@Data
public class Bwa implements Serializable {

  @Id
  @Column(name = "BWA")
  String bwa;
  @Column(name = "Bewegung")
  String bewegung;
  @Column(name = "Bezeichnung")
  String bezeichnung;
  @Column(name = "Typ")
  String typ;

}
