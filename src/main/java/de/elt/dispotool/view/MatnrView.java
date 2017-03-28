/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.view;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.primefaces.model.chart.BarChartSeries;
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
public class MatnrView implements Serializable {

    @Inject
    BewegungDao bewegungDao;

    List<String> materialNummerList;
    
    @PostConstruct
    public void init() {
        materialNummerList = bewegungDao.getMaterialNummern();
    }
}