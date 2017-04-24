/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.service;

import de.elt.dispotool.dao.BewegungDao;
import de.elt.dispotool.dao.BwaDao;
import de.elt.dispotool.dao.BestandDao;
import de.elt.dispotool.entities.Bewegung;
import de.elt.dispotool.entities.Bwa;
import de.elt.dispotool.util.BewegungUtils;
import de.elt.dispotool.util.ChartUtils;
import de.elt.dispotool.util.Constants;
import de.elt.dispotool.view.BewegungView;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateful;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.model.chart.BarChartModel;

/**
 *
 * @author Jonas
 */
@Stateful
@Getter
@Setter
@Log
public class BewegungService {

    @Inject
    BewegungDao bewegungDao;

    @Inject
    BwaDao bwaDao;

    @Inject
    BestandDao bestandDao;

    List<Bewegung> bewegungen;
    List<Bewegung> zugaenge;
    List<Bewegung> abgaenge;
    int abgangsInterval = 30;
    Date first, last;
    List<Bwa> bwas;
    Map<String, Integer> vorzeichen;
    SortedMap<String, SortedMap<String, Integer>> bewegungsMap;
    SortedMap<String, SortedMap<String, Integer>> abgangsMap;
    SortedMap<String, Integer> simZugangsMap;
    SortedMap<String, Integer> bestandsMap;
    SortedMap<String, Integer> simBestandsMap;
    SortedMap<String, SortedMap<String, Integer>> chartMap;
    SortedMap<String, SortedMap<String, Integer>> simChartMap;
    Map<String, Integer> bwaMengen;
    Integer bestellmenge;

    Integer minBestand, maxBestand, minSimBestand, maxSimBestand;

    String materialNummer;
    BarChartModel barChartModel;
    List<String> types = Constants.TYPES;

    public void initBewegungsMap() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy,MM,dd");
        bewegungsMap = BewegungUtils.makeEmptyMap(first, last);

//        for (Bwa bwa : bwas) {
//            String bwaString = bwa.getBwa();
//            int vorz = getVorzeichen().get(bwaString);
//            //log.fine("bwaString: "+bwaString+", vorz: "+vorz);
//            if (getVorzeichen().get(bwaString) == 0) {
//                continue;
//            }
//            Map<String, Integer> series = getBewegungenAsMap(bwaString);
//            BewegungUtils.addSeries(bewegungsMap, series, bwaString);
////          log.fine("bwaString: "+bwaString+", vorz: "+vorz);
//        }
        log.info("Bewegungen.size: " + bewegungen.size());
        for(Bewegung b : bewegungen) {
            String bwa = b.getBewegungsart();
            String date = format.format(b.getBuchungsdatum());
            SortedMap<String, Integer> subMap = bewegungsMap.get(date);
            if (subMap == null) {
                subMap = new TreeMap();
                bewegungsMap.put(date, subMap);
            }
            Integer menge = subMap.get(bwa);
            if (menge == null){
                menge = 0;
            }
            subMap.put(bwa, menge + getValue(b));
        }
    }

    public void initAbgangsMap() {
        abgangsMap = BewegungUtils.makeEmptyMap(first, last);

        for (Bwa bwa : bwas) {
            String bwaString = bwa.getBwa();
            if (getVorzeichen().get(bwaString) != -1) {
                continue;
            }
            Map<String, Integer> series = getBewegungenAsMap(bwaString);
            BewegungUtils.addSeries(abgangsMap, series, bwaString);
        }
    }

    public void initChartMap() {
        chartMap = new TreeMap();
        for (Map.Entry<String, SortedMap<String, Integer>> entry : bewegungsMap.entrySet()) {
            chartMap.put(entry.getKey(), new TreeMap(entry.getValue()));
        }
        BewegungUtils.addSeries(chartMap, new TreeMap(getBestandsMap()), " Bestand");
//        log.log(Level.INFO, "ChartMap: {0}", chartMap.toString());
    }

    public SortedMap<String, Integer> getBestandsMap() {
        if (bestandsMap == null) {
            initBestandsMap();
        }
        return bestandsMap;
    }

    public SortedMap<String, Integer> getSimBestandsMap() {
        if (simBestandsMap == null) {
            initSimBestandsMap();
        }
        return simBestandsMap;
    }

//    private Integer getAnfangsbestand2() {
//        String matNum = getMaterialNummer();
//        Integer bestand = materialDao.getBestand2015(matNum);
//        if (bestand == null) {
//            bestand = 0;
//        }
//        return bestand;
//    }
    public Integer getAnfangsbestand() {
        Integer bestand = bestandDao.getBestand2016(materialNummer);
        if (bestand == null) {
            bestand = 0;
        }

        int count = 0, count2 = 0, zuSum = 0, abSum = 0;
        for (Bewegung zu : zugaenge) {
            bestand -= zu.getMenge();
            zuSum += zu.getMenge();
            count++;
        }
        for (Bewegung ab : abgaenge) {
            bestand += ab.getMenge();
            abSum += ab.getMenge();
            count2++;
        }
        log.log(Level.INFO, "zugaenge: {0}, count: {1}, ab: {2}, count: {3}, diff: {4}, bes2016: {5}, bes2015(calc): {6}", new Object[]{zuSum, count, abSum, count2, zuSum - abSum, bestandDao.getBestand2016(materialNummer), bestand});
        return bestand;
    }

    private void initZuAbgaenge() {
        zugaenge = new ArrayList<>();
        abgaenge = new ArrayList<>();
        for (Bewegung b : bewegungen) {
            int vz = getVorzeichen().get(b.getBewegungsart());
            switch (vz) {
                case 0:
//                    log.fine("case"+vz+": " +b.getBewegungsart());
                    break;
                case 1:
                    zugaenge.add(b);
//                    log.fine("case"+vz+": " +b.getBewegungsart());
                    break;
                case -1:
                    abgaenge.add(b);
//                    log.fine("case"+vz+": " +b.getBewegungsart());
                    break;
                default:
                    log.warning("Default Case in Vorzeichen switch! Value was: " + vz);
            }
        }
    }

    public void initBestandsMap() {

        if (bewegungsMap == null) {
            initBewegungsMap();
        }

        bestandsMap = new TreeMap<>();

//        String matnr = getMaterialNummer();
//        bewegungen = bewegungDao.getByMaterialnummer(matnr);
        Date lastPlusOne = DateUtils.addDays(last, 1);
        Integer currentBestand = getAnfangsbestand();
        int zuSum = 0, abSum = 0, count1 = 0, count2 = 0, count3=0, count4=0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy,MM,dd");
        for (Date day = first; day.before(lastPlusOne); day = DateUtils.addDays(day, 1)) {
            String dateString = format.format(day);
            SortedMap<String, Integer> dayBewegungen = bewegungsMap.get(dateString);
            count3++;
            if (dayBewegungen != null) {
                for (Map.Entry<String, Integer> entry : dayBewegungen.entrySet()) {
                    count4++;
                    Integer menge = entry.getValue();
                    if (menge > 0) {
                        zuSum += menge;
                        count1++;
                    } else {
                        abSum += menge;
                        count2++;
                    }
                    currentBestand += menge;
                    //log.log(Level.FINEST, "CurrentBestand changed. Date: {0}, Bestand: {1}", new Object[]{dateString, currentBestand});
                }
            }
            bestandsMap.put(dateString, currentBestand);
            adjustMinMax(currentBestand);
//            log.log(Level.FINER, "Date and Bestand: {0}: {1}", new Object[]{dateString, currentBestand});
        }
        log.log(Level.INFO, "zugaenge: {0}, count: {1}, ab: {2}, count: {3}, diff: {4}, bes2016: {5}, bes2015(calc): {6}, bewegungsMap.size: {7}, count3: {8}, count4: {9}", new Object[]{zuSum, count1, abSum, count2, zuSum + abSum, bestandDao.getBestand2016(materialNummer), currentBestand, bewegungsMap.size(),count3, count4});
        
    }

    private void adjustMinMax(Integer currentBestand) {
        if (minBestand == null || minBestand > currentBestand) {
            minBestand = currentBestand;
        }
        if (maxBestand == null || maxBestand < currentBestand) {
            maxBestand = currentBestand;
        }
    }

    private void adjustSimMinMax(Integer currentBestand) {
        if (minSimBestand == null || minSimBestand > currentBestand) {
            minSimBestand = currentBestand;
        }
        if (maxSimBestand == null || maxSimBestand < currentBestand) {
            maxSimBestand = currentBestand;
        }
    }

    private void resetMinMax() {
        minSimBestand = null;
        maxSimBestand = null;
        maxBestand = null;
        minBestand = null;
    }

    public void initSimBestandsMap() {
        Integer ordered = 0;
        if (abgangsMap == null) {
            initBewegungsMap();
        }
//        simZugangsMap = new TreeMap<>();
        simZugangsMap = BewegungUtils.makeEmpty1Map(first, last);
        simBestandsMap = new TreeMap<>();
        Integer abgangsInterval = getAbgangsInterval();
        Integer maxAbgangsmenge = getMaxAbgangsmenge(abgangsInterval);
        Date lastPlusOne = DateUtils.addDays(last, 1);
        Integer currentBestand = getAnfangsbestand();

        SimpleDateFormat format = new SimpleDateFormat("yyyy,MM,dd");
        for (Date day = first; day.before(lastPlusOne); day = DateUtils.addDays(day, 1)) {
            String dateString = format.format(day);
            SortedMap<String, Integer> dayBewegungen = abgangsMap.get(dateString);
            if (dayBewegungen != null) {
                for (Map.Entry<String, Integer> entry : dayBewegungen.entrySet()) {
                    Integer menge = entry.getValue();
                    currentBestand += menge;
                }
                Integer zugang = simZugangsMap.get(dateString);
                if (zugang != null) {
                    currentBestand += zugang;
                    ordered -= zugang;
                }
            }
            simBestandsMap.put(dateString, currentBestand);
            adjustSimMinMax(currentBestand);
            if (currentBestand + ordered <= maxAbgangsmenge) {
                Date nowPlusInterval = DateUtils.addDays(day, abgangsInterval);
                String arrivalDate = format.format(nowPlusInterval);
                simZugangsMap.put(arrivalDate, bestellmenge);
                ordered += bestellmenge;
            }
//            log.log(Level.FINER, "Date and Bestand: {0}: {1}", new Object[]{dateString, currentBestand});
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
        //Map testSeries = getBewegungenAsMap(bwas.get(0).getBwa());
//        BewegungUtils.addSeries(tempMap, testSeries, "BestandTest");
        return ChartUtils.makeChartData(tempMap);
    }

    public String getSimBestandsArray() {
        SortedMap<String, SortedMap<String, Integer>> tempMap = BewegungUtils.makeEmptyMap(first, last);
        BewegungUtils.addSeries(tempMap, simBestandsMap, "2_Bestand(Simuliert)");
        Map minSim = BewegungUtils.makeEmpty1Map(first, last, minSimBestand);
        BewegungUtils.addSeries(tempMap, minSim, "2_Min(Simuliert)");
        Map maxSim = BewegungUtils.makeEmpty1Map(first, last, maxSimBestand);
        BewegungUtils.addSeries(tempMap, maxSim, "2_Max(Simuliert)");
        BewegungUtils.addSeries(tempMap, bestandsMap, "1_Bestand(Real)");
        Map min = BewegungUtils.makeEmpty1Map(first, last, minBestand);
        BewegungUtils.addSeries(tempMap, min, "1_Min(Real)");
        Map max = BewegungUtils.makeEmpty1Map(first, last, maxBestand);
        BewegungUtils.addSeries(tempMap, max, "1_Max(Real)");
        BewegungUtils.addSeries(tempMap, simZugangsMap, "Reorders(Simuliert)");
        return ChartUtils.makeChartData(tempMap);
    }

    private SortedMap<String, Integer> getBewegungenAsMap(String bwa) {
        List<Bewegung> bewegungenOfBwa = bewegungDao.getBewegungen(materialNummer, bwa);
        SortedMap<String, Integer> map = new TreeMap();
        for (Bewegung b : bewegungenOfBwa) {
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
        bwaMengen = bewegungDao.getBwaMengen(materialNummer);
        resetMinMax();
        initVorzeichen();
        initZuAbgaenge();
        initBewegungsMap();
        initAbgangsMap();
        initBestandsMap();
        bestellmenge = getMaxAbgangsmenge();
        initSimBestandsMap();
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

    public int getMaxAbgangsmenge() {
        return getMaxAbgangsmenge(abgangsInterval);
    }

    private int getMaxAbgangsmenge(int intervalInDays) {
        int maxAbgang = 0;
        Date from = getFirst();
        Date to = getLast();

//        log.info("from: " + from + ", to: " + to + " toMinusInterval: " + toMinusInterval);
        Date intervalEnd = DateUtils.addDays(from, intervalInDays);
//        log.log(Level.INFO, "from: {0}, intervalEnd: {1}", new Object[]{from, intervalEnd});
        int currentAbgang = 0;
        for (Bewegung ab : abgaenge) {
            Date abDate = ab.getBuchungsdatum();
            if (abDate.after(from) && abDate.before(intervalEnd)) {
//                log.log(Level.INFO, "abDate: {0}", abDate);
                currentAbgang += ab.getMenge();
            }
        }
        maxAbgang = currentAbgang;
//        log.log(Level.INFO, "maxAbgang: {0}", maxAbgang);

        Date toMinusInterval = DateUtils.addDays(to, -intervalInDays + 1);
        for (Date intervalStart = from; intervalStart.before(toMinusInterval); intervalStart = DateUtils.addDays(intervalStart, 1)) {
            intervalEnd = DateUtils.addDays(intervalStart, intervalInDays);
            for (Bewegung ab : abgaenge) {
                Date abDate = ab.getBuchungsdatum();
                if (abDate.equals(intervalStart)) {
                    currentAbgang -= ab.getMenge();
                } else if (abDate.equals(intervalEnd)) {
                    currentAbgang += ab.getMenge();
                }
            }

            if (currentAbgang > maxAbgang) {
                maxAbgang = currentAbgang;
//                log.log(Level.INFO, "maxAbgang: {0}", maxAbgang);
            }

        }

        return maxAbgang;
    }

    public Integer getMengeOfBwa(String bwa) {
        return getBwaMengen().get(bwa);
    }

    public void setAbgangsInterval(int ab) {
        abgangsInterval = ab;
        initSimBestandsMap();
    }

    public Integer getBestellmenge() {
        return bestellmenge;
    }

    public void setBestellmenge(Integer x) {
        bestellmenge = x;
        log.finest("setBestellmenge " + x);
    }
}
