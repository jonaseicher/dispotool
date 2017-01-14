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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
    Map<String, Map<String, Integer>> bewegungsMap;
    Map<String, Integer> bestandsMap;

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

    public void initBewegungsMap() {
        String matNr = getMaterialNummer();
        List<String> bwas = bewegungDao.getBewegungsartenOfMaterial(matNr);
//  bwas.size()
        List<Bewegung> bewegungen = bewegungDao.getByMaterialnummer(matNr);
        SimpleDateFormat format = new SimpleDateFormat("yyyy,MM,dd");
        bewegungsMap = new HashMap<>();
        for (Bewegung b : bewegungen) {
            if (b.getBewegungsart().equals("NICHTS")) {
                continue;
            }
            //Date date = b.getBuchungsdatum();
//            String date = String.valueOf(b.getBuchungsdatum().getTime());
            String date = format.format(b.getBuchungsdatum());
            String bwa = b.getBewegungsart();
            Map<String, Integer> series = bewegungsMap.get(date);
            if (series == null) {
                series = new HashMap<>();
                bewegungsMap.put(date, series);
            };
            Integer menge = series.get(bwa);
            if (menge == null) {
                menge = 0;
            } else {
                log.warning("WARNING!! Mehr als eine Bewegung an einem Datum und einer Bewegunsart! Siehe ColumnChartView. Matnr:" + matNr + ", Bwa:" + bwa + ", datum:" + date);
            }
            menge += getValue(b);
            series.put(bwa, menge);
        }
    }

    private Date getFirstDate(List<Bewegung> bewegungen) {
        Date first = new Date();
        for (Bewegung b : bewegungen) {
            Date x = b.getBuchungsdatum();
            if (x.before(first)) {
                first = x;
            }
        }
        return first;
    }

    private Date getLastDate(List<Bewegung> bewegungen) {
        Date last = new Date(0l);
        for (Bewegung b : bewegungen) {
            Date x = b.getBuchungsdatum();
            if (x.after(last)) {
                last = x;
            }
        }
        return last;
    }

    private Integer getAnfangsbestand() {
        return 0;
    }

    public void initBestandsMap() {

        if (bewegungsMap == null) {
            initBewegungsMap();
        }

        bestandsMap = new HashMap<>();

        String matNr = getMaterialNummer();
        List<Bewegung> bewegungen = bewegungDao.getByMaterialnummer(matNr);

        Date first = getFirstDate(bewegungen);
        Date last = getLastDate(bewegungen);
        Date lastPlusOne = DateUtils.addDays(last, 1);
        Integer currentBestand = getAnfangsbestand();

        SimpleDateFormat format = new SimpleDateFormat("yyyy,MM,dd");
        for (Date day = first; day.before(lastPlusOne); day = DateUtils.addDays(day, 1)) {
            String dateString = format.format(day);
            Map<String, Integer> dayBewegungen = bewegungsMap.get(dateString);
            if (dayBewegungen != null) {
                for (Entry<String, Integer> entry : dayBewegungen.entrySet()) {
                    String bwaString = entry.getKey();
                    Integer menge = entry.getValue();
                    Integer value = getVorzeichenOfBwa(bwaString) * menge;
                    currentBestand += value;
                    log.info("CurrentBestand changed. Now: " + currentBestand);
                }
            }
            bestandsMap.put(dateString, currentBestand);
        }

    }

    public String getBewegungsArray() {
        return makeChartData(bewegungsMap);
    }

    public String getBestandsArray() {
        Map<String, Map<String, Integer>> tempMap = new HashMap();
        for (Entry<String, Integer> entry : bestandsMap.entrySet()) {
            Map tempMap2 = new HashMap();
            tempMap2.put("Bestand", entry.getValue());
            tempMap.put(entry.getKey(), tempMap2);
        }
        return makeChartData(tempMap);
    }

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
        initBewegungsMap();
        initBestandsMap();
    }

    void initVorzeichen() {
        vorzeichen = new HashMap();
        for (Bwa bwa : bwas) {
            vorzeichen.put(bwa.getBwa(), getVorzeichenOfType(bwa.getTyp()));
        }
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

    /**
     * Method that creates the chart data format that google charts wants:
     *
     * var dt = new google.visualization.DataTable({ cols: [{id: 'task', label:
     * 'Task', type: 'string'}, {id: 'hours', label: 'Hours per Day', type:
     * 'number'}], rows: [{c:[{v: 'Work'}, {v: 11}]}, {c:[{v: 'Eat'}, {v: 2}]},
     * {c:[{v: 'Commute'}, {v: 2}]}, {c:[{v: 'Watch TV'}, {v:2}]}, {c:[{v:
     * 'Sleep'}, {v:7, f:'7.000'}]}] });
     *
     * @return String to be passed to DataTable constructor.
     * @param data A map with Labels, each pointing to a map with dates (in
     * yyyy,MM,dd format) and values (Integers)
     */
    public String makeChartData(Map<String, Map<String, Integer>> data) {

        String cols = "{id:\"date\",label:\"Datum\",type:\"date\"}";

        Set set = new HashSet();
        for (Map subMap : data.values()) {
            set.addAll(subMap.keySet());
        }
        List<String> labels = new ArrayList(set);

        for (String label : labels) {
            cols += ",{id:\"" + label + "\",label:\"" + label + "\",type:\"number\"}";
        }

        String rows = "";
        boolean first = true;
        for (String dateKey : data.keySet()) {
            String row = "";
            if (!first) {
                row += ",";
            } else {
                first = false;
            }
            row += "{c:[{v:\"Date(" + dateKey + ")\"}";
            Map<String, Integer> rowData = data.get(dateKey);

            for (int i = 0; i < labels.size(); i++) {
                String label = labels.get(i);
                Integer value = rowData.get(label);
                if (value == null) {
                    value = 0;
                }
                row += ",{v:" + value + "}";
            }
            row += "]}";
            rows += row;
        }

        String all = "{ cols: [" + cols + "], rows: [" + rows + "] }";

        log.info(all);

        return all;
    }

}
