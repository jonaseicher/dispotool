/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.view;

import com.google.gson.Gson;
import de.elt.dispotool.dao.BewegungDao;
import de.elt.dispotool.entities.Bewegung;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Setter;
import lombok.Getter;
import lombok.extern.java.Log;
import org.primefaces.model.chart.AxisType;
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
public class TestView implements Serializable {

  BarChartModel model;
  List<Bewegung> bewegungen;
  List<Object> testData;
  
  SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");

  @Inject
  BewegungDao dao;

  @PostConstruct
  public void init() {
    String matnr = "64365703";
    bewegungen = dao.getByMaterialnummer(matnr);
   Object[][] o = { {4,3},{5,3,3} }; 
   testData = new ArrayList();
   for (Bewegung b :bewegungen) {
     testData.add (new Object[]{b.getBuchungsdatum().getTime(), b.getMenge()});
   }
  }
  
  public String getTestData() {
    String json = new Gson().toJson(testData);
    log.info(json);
    return json ;
  }

}
