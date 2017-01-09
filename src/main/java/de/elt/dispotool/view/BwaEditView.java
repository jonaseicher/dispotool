/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.view;

import de.elt.dispotool.dao.BwaDao;
import de.elt.dispotool.entities.Bwa;
import de.elt.dispotool.util.Constants;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Setter;
import lombok.Getter;
import lombok.extern.java.Log;
import org.primefaces.event.CellEditEvent;
import org.primefaces.component.datatable.DataTable;

/**
 *
 * @author jonas.eicher
 */
@Named
@ViewScoped
@Getter
@Setter
@Log
public class BwaEditView implements Serializable {

  @Inject
  BwaDao bwaDao;

  List<Bwa> bwas;

  List<String> types = Constants.TYPES;

  @PostConstruct
  public void init() {
//    bwas = bwaDao.getAll();
    log.log(Level.FINE, "bwas:{0}", bwas);
  }

  public List<Bwa> getBwas() {
    if (bwas == null) {
      bwas = bwaDao.getAll();
    }
    return bwas;
  }

  public List<Bwa> getBwas(String materialNummer) {
    return bwaDao.getBewegungsartenOfMaterial(materialNummer);
  }

  public void onCellEdit(CellEditEvent event) {
    Object oldValue = event.getOldValue();
    Object newValue = event.getNewValue();

    if (newValue != null && !newValue.equals(oldValue)) {
      Bwa bwa = (Bwa) ((DataTable) event.getComponent()).getRowData();
      bwaDao.merge(bwa);
      FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Bewegungsart: " + bwa.getBwa() + ", Old: " + oldValue + ", New:" + newValue);
      FacesContext.getCurrentInstance().addMessage(null, msg);
    }
  }
}
