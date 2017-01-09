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
import java.util.Date;
import java.util.HashMap;
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
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.BarChartModel;

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

  List<Bewegung> bewegungen;
  List<Bwa> bwas;
  Map<String, Integer> vorzeichen;

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
    bewegungen = bewegungDao.getByMaterialnummer(matNr);
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
  public List<Bwa> getBwasOfMaterial() {
    return getBwasOfMaterial(materialNummer);
  }

  public List<Bwa> getBwasOfMaterial(String materialNummer) {
    return bwaDao.getBewegungsartenOfMaterial(materialNummer);
  }

  public void setMaterialNummer(String matnr) {
    materialNummer = matnr;
    bwas = getBwasOfMaterial();
    initVorzeichen();
    initChart();
  }

  void initVorzeichen() {
    vorzeichen = new HashMap();
    for (Bwa bwa : bwas) {
      vorzeichen.put(bwa.getBwa(), getVorzeichenOfType(bwa.getTyp()));
    }
  }

  public void initChart() {
    barChartModel = new BarChartModel();
    barChartModel.setLegendPosition("ne");
//    DateAxis axis = new DateAxis();
//    axis.setTickInterval();
//    axis.setTickAngle(90);
//    axis.setTickFormat("%#d.%b %Y");
//    barChartModel.getAxes().put(AxisType.X, axis);
    for (Bwa bwa : bwas) {
      if (getVorzeichenOfType(bwa.getTyp()) != 0) {
        addSeries(bwa);
      }
    }

    //barChartModel.setStacked(true);
  }

  Map getAccumulatedSeries(List<Bewegung> bewegungen) {
    Date first = new Date();
    Date last = new Date(0l);
    for (Bewegung bewegung : bewegungen) {
      Date datum = bewegung.getBuchungsdatum();
      if (datum.before(first)) {
        first = datum;
      }
      if (datum.after(last)) {
        last = datum;
      }
      log.log(Level.FINE, "First Date: {0}, Last Date: {1}, Bewegung: {2}", new Object[]{first, last, bewegung.toString()});
    }
    log.log(Level.FINE, "First Date: {0}, Last Date: {1}", new Object[]{first, last});

    List<Date> dates = new ArrayList();

    Date iterator = first;
    while (iterator.before(last)) {
      dates.add(iterator);
      iterator = DateUtils.addDays(iterator, 1);
    }
    dates.add(iterator);

    log.fine(dates.toString());
    Map<Date, Integer> seriesMap = new HashMap();

    for (Date d : dates) {
      seriesMap.put(d, 0);
    }

    for (Bewegung bewegung : bewegungen) {
      int value = getValue(bewegung);
      Date d = bewegung.getBuchungsdatum();
      int oldValue = seriesMap.get(d);
      seriesMap.put(d, oldValue + value);
      log.log(Level.FINE, "We just added this to the map: {0}: {1}+{2}", new Object[]{d, oldValue, value});
    }

    return seriesMap;
  }

  int getVorzeichenOfBwa(String bwa) {
    return vorzeichen.get(bwa);
  }

  int getVorzeichenOfType(String bwaTyp) {
    switch (bwaTyp) {
      case "NICHTS":
        return 0;
      case "ZUGANG":
        return 1;
      case "ABGANG":
        return -1;
      default:
        log.log(Level.WARNING, "Default case in switch. Value was: {0}", bwaTyp);
        return 0;
    }
  }

  int getValue(Bewegung bewegung) {
    String bwa = bewegung.getBewegungsart();
    return getVorzeichenOfBwa(bwa) * bewegung.getMenge();
  }

  void addSeries(Bwa bwa) {

    SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");

    BarChartSeries s = new BarChartSeries();
    s.setLabel(bwa.getBwa());
    long stamp = System.currentTimeMillis();
    List<Bewegung> bewegungenOfBwa = bewegungDao.getBewegungen(materialNummer, bwa.getBwa());
    log.log(Level.INFO, "Query time: {0}", Long.toString(System.currentTimeMillis() - stamp));

    stamp = System.currentTimeMillis();
    s.setData(getAccumulatedSeries(bewegungenOfBwa));
    log.log(Level.INFO, "getAccumulatedSeriesTime: {0}", Long.toString(System.currentTimeMillis() - stamp));
//    for (Bewegung b : bewegungenOfBwa) {
//      //s.set(b.getBuchungsdatum(), vorzeichen * b.getMenge());
//      log.log(Level.FINE, "setting: {0} = {1}", new Object[]{b.getBuchungsdatum(), vorzeichen * b.getMenge()});
//      
//      log.log(Level.FINE, "Datum: {0}, Ceiling: {1}", new Object[]{b.getBuchungsdatum(), DateUtils.ceiling(b.getBuchungsdatum(), Calendar.DAY_OF_MONTH)});
//      
//      String date = format.format(b.getBuchungsdatum());
//      log.log(Level.FINE, "{0} = {1}", new Object[]{date, vorzeichen * b.getMenge()});
//      s.set(date, vorzeichen * b.getMenge());
//    }
    barChartModel.addSeries(s);
  }

}
