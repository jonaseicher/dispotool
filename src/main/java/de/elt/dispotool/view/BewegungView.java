/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.view;

import de.elt.dispotool.dao.BewegungDao;
import de.elt.dispotool.dao.BwaDao;
import de.elt.dispotool.entities.Bewegung;
import de.elt.dispotool.entities.Bwa;
import de.elt.dispotool.util.Constants;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Setter;
import lombok.Getter;
import lombok.extern.java.Log;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.DateAxis;

/**
 *
 * @author jonas.eicher
 */
@Named
@ViewScoped
@Getter
@Setter
@Log
public class BewegungView implements Serializable {

  @Inject
  BewegungDao bewegungDao;

  @Inject
  BwaDao bwaDao;

//  List<Bewegung> bewegungen;
  List<Bwa> bwas;

  String materialNummer;
  BarChartModel barChartModel;

  List<String> types = Constants.TYPES;

  @PostConstruct
  public void init() {
    Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
    String matNr = params.get("matNr");
    if (matNr == null) {
      log.warning("materialnummer was null. setting default value.");
      matNr = "64365703";
    }
    setMaterialNummer(matNr);
  }

//  public List<String> completeMaterialNummer(String query) {
//    log.fine("query:" + query);
//    log.fine("matnr:" + materialNummer);
//    List<String> results = new ArrayList<>();
//    for (String mnr : materialNummern) {
//      if (mnr.contains(query)) {
//        results.add(mnr);
//      }
//    }
//    log.log(Level.FINE, "completeResults:{0}", results);
//    return results;
//  }

  public void setMaterialNummer(String matnr) {
    materialNummer = matnr;
    bwas = bwaDao.getBewegungsartenOfMaterial(materialNummer);    
//bewegungen = bewegungDao.getByMaterialnummer(matnr);
    initChart();
  }

  public void initChart() {
    barChartModel = new BarChartModel();
    
    for (Bwa bwa : bwas) {
      addSeries(bwa);
    }

    barChartModel.setLegendPosition("ne");
    DateAxis axis = new DateAxis();
    axis.setTickAngle(90);
    axis.setTickFormat("%#d.%b %Y");
    barChartModel.getAxes().put(AxisType.X, axis);
  }

  void addSeries(Bwa bwa) {

    SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");

    int vorzeichen = 1;
    switch (bwa.getTyp()) {
      case "NICHTS":
        return;
      case "ZUGANG":
        break;
      case "ABGANG":
        vorzeichen = -1;
        break;
      default:
        log.log(Level.WARNING, "Default case in switch. Value was: {0}", bwa.getTyp());
    }

    BarChartSeries s = new BarChartSeries();
    s.setLabel(bwa.getBwa());
    long stamp = System.currentTimeMillis();
    List<Bewegung> bewegungenOfBwa = bewegungDao.getBewegungen(materialNummer, bwa.getBwa());
    log.log(Level.INFO, "Query time: {0}", Long.toString(System.currentTimeMillis() - stamp));
    for (Bewegung b : bewegungenOfBwa) {
      //s.set(b.getBuchungsdatum(), vorzeichen * b.getMenge());
      log.log(Level.FINE, "setting: {0} = {1}", new Object[]{b.getBuchungsdatum(), vorzeichen * b.getMenge()});
      String date = format.format(b.getBuchungsdatum());
      log.log(Level.FINE, "{0} = {1}", new Object[]{date, vorzeichen * b.getMenge()});
      s.set(date, vorzeichen * b.getMenge());
    }
    barChartModel.addSeries(s);
  }

}
