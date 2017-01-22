/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.view;

import de.elt.dispotool.dao.BewegungDao;
import de.elt.dispotool.dao.BwaDao;
import de.elt.dispotool.dao.MaterialDao;
import de.elt.dispotool.entities.Bewegung;
import de.elt.dispotool.entities.Bwa;
import de.elt.dispotool.util.BewegungUtils;
import de.elt.dispotool.util.ChartUtils;
import de.elt.dispotool.util.Constants;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    @Inject
    MaterialDao materialDao;

    List<Bewegung> bewegungen;
    List<Bewegung> zugaenge;
    List<Bewegung> abgaenge;
    Date first, last;
    List<Bwa> bwas;
    Map<String, Integer> vorzeichen;
    SortedMap<String, SortedMap<String, Integer>> bewegungsMap;
    SortedMap<String, Integer> bestandsMap;
    SortedMap<String, SortedMap<String, Integer>> chartMap;

    String materialNummer;
    BarChartModel barChartModel;
    List<String> types = Constants.TYPES;

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String matNr = params.get("matNr");

        setMaterialNummer(matNr);
    }

    public void initBewegungsMap() {
        bewegungsMap = BewegungUtils.makeEmptyMap(first, last);

        for (Bwa bwa : bwas) {
            String bwaString = bwa.getBwa();
            if (getVorzeichen().get(bwaString) == 0) {
                continue;
            }
            Map<String, Integer> series = getBewegungenAsMap(bwaString);
            BewegungUtils.addSeries(bewegungsMap, series, bwaString);
        }
    }

    public void initChartMap() {
        chartMap = new TreeMap();
        for (Entry<String, SortedMap<String, Integer>> entry : bewegungsMap.entrySet()) {
            chartMap.put(entry.getKey(), new TreeMap(entry.getValue()));
        }
        BewegungUtils.addSeries(chartMap, new TreeMap(getBestandsMap()), " _Bestand_");
        log.info("ChartMap: " + chartMap.toString());
    }

    public SortedMap<String, Integer> getBestandsMap() {
        if (bestandsMap == null) {
            initBestandsMap();
        }
        return bestandsMap;
    }

//    private Integer getAnfangsbestand2() {
//        String matNum = getMaterialNummer();
//        Integer bestand = materialDao.getBestand2015(matNum);
//        if (bestand == null) {
//            bestand = 0;
//        }
//        return bestand;
//    }

    private Integer getAnfangsbestand() {
        Integer bestand = materialDao.getBestand2016(materialNummer);
        if (bestand == null) {
            bestand = 0;
        }

        for (Bewegung zu : zugaenge) {
            bestand -= zu.getMenge();
        }
        for (Bewegung ab : abgaenge) {
            bestand += ab.getMenge();
        }
        return bestand;
    }

    private void initZuAbgaenge(String matNr) {
        zugaenge = new ArrayList<>();
        abgaenge = new ArrayList<>();
        for (Bewegung b : bewegungen) {
            int vorzeichen = getVorzeichen().get(b.getBewegungsart());
            switch (vorzeichen) {
                case 0:
                    break;
                case 1:
                    zugaenge.add(b);
                    break;
                case -1:
                    abgaenge.add(b);
                    break;
                default:
                    log.warning("Default Case in Vorzeichen switch! Value was: " + vorzeichen);
            }
        }
    }

    public void initBestandsMap() {

        if (bewegungsMap == null) {
            initBewegungsMap();
        }

        bestandsMap = new TreeMap<>();

//        String matNr = getMaterialNummer();
//        bewegungen = bewegungDao.getByMaterialnummer(matNr);
        Date lastPlusOne = DateUtils.addDays(last, 1);
        Integer currentBestand = getAnfangsbestand();

        SimpleDateFormat format = new SimpleDateFormat("yyyy,MM,dd");
        for (Date day = first; day.before(lastPlusOne); day = DateUtils.addDays(day, 1)) {
            String dateString = format.format(day);
            SortedMap<String, Integer> dayBewegungen = bewegungsMap.get(dateString);
            if (dayBewegungen != null) {
                for (Entry<String, Integer> entry : dayBewegungen.entrySet()) {
                    Integer menge = entry.getValue();
                    currentBestand += menge;
//                    log.log(Level.FINEST, "CurrentBestand changed. Date: {0}, Bestand: {1}", new Object[]{dateString, currentBestand});
                }
            }
            bestandsMap.put(dateString, currentBestand);
            log.log(Level.FINER, "Date and Bestand: {0}: {1}", new Object[]{dateString, currentBestand});
        }

    }

    public String getBewegungsArray() {
        return ChartUtils.makeChartData(bewegungsMap);
    }

    public String getChartArray() {
        return ChartUtils.makeChartData(chartMap);
    }

    public String getBestandsArray() {
        SortedMap<String, SortedMap<String, Integer>> tempMap = BewegungUtils.makeEmptyMap(first, last);
        BewegungUtils.addSeries(tempMap, bestandsMap, "Bestand");
        Map testSeries = getBewegungenAsMap(bwas.get(0).getBwa());
//        BewegungUtils.addSeries(tempMap, testSeries, "BestandTest");
        return ChartUtils.makeChartData(tempMap);
    }

    private SortedMap<String, Integer> getBewegungenAsMap(String bwa) {
        List<Bewegung> bewegungen = bewegungDao.getBewegungen(materialNummer, bwa);
        SortedMap<String, Integer> map = new TreeMap();
        for (Bewegung b : bewegungen) {
            map.put(b.getDateString(), getValue(b));
        }
        return map;
    }

    public List<Bwa> getBwasOfMaterial() {
        return getBwasOfMaterial(materialNummer);
    }

    public List<Bwa> getBwasOfMaterial(String materialNummer) {
        return bwaDao.getBewegungsartenOfMaterial(materialNummer);
    }

    public void setMaterialNummer(String matnr) {
        if (matnr == null) {
            log.warning("materialnummer was null. setting default value.");
            matnr = "64365703";
        }
        bewegungen = bewegungDao.getByMaterialnummer(matnr);
        if (bewegungen == null || bewegungen.isEmpty()) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("materialnummern.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(BewegungView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        materialNummer = matnr;
        first = BewegungUtils.getFirstDate(bewegungen);
        last = BewegungUtils.getLastDate(bewegungen);
        bwas = getBwasOfMaterial();
        initVorzeichen();
        initZuAbgaenge(materialNummer);
        initBewegungsMap();
        initBestandsMap();
        initChartMap();
    }

    void initVorzeichen() {
        vorzeichen = new HashMap();
        for (Bwa bwa : bwas) {
            vorzeichen.put(bwa.getBwa(), BewegungUtils.getVorzeichenOfType(bwa.getTyp()));
        }
    }

    int getVorzeichenOfBwa(String bwa) {
        return vorzeichen.get(bwa);
    }

    int getValue(Bewegung bewegung) {
        String bwa = bewegung.getBewegungsart();
        return getVorzeichenOfBwa(bwa) * bewegung.getMenge();
    }

}
