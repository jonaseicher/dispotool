/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.util;

import de.elt.dispotool.entities.Bewegung;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.apache.commons.lang3.time.DateUtils;

/**
 *
 * @author Jonas
 */
@Log
public class BewegungUtils {

    public static int getVorzeichenOfType(String bwaTyp) {
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
    
    
    
       public static SortedMap<String, SortedMap<String, Integer>> makeEmptyMap(Date first, Date last) {
        
        SortedMap<String, SortedMap<String, Integer>>  map = new TreeMap();
        Date lastPlusOne = DateUtils.addDays(last, 1);
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy,MM,dd");
        for (Date day = first; day.before(lastPlusOne); day = DateUtils.addDays(day, 1)) {
            String dateString = format.format(day);
            map.put(dateString, new TreeMap<>());           
        }
        return map;
    }
    
    public static void addSeries(SortedMap<String, SortedMap<String,Integer>> toMap, Map<String, Integer> series, String label) {
        
        for (Map.Entry<String, SortedMap<String,Integer>> entry : toMap.entrySet()) {
            String dateString = entry.getKey();
            if (series.get(dateString) == null) { continue; }
            Map<String,Integer> subMap = entry.getValue();
            subMap.put(label, series.get(dateString));
        }
    }
    
        // TODO: make combined method for initializing date fields first last
    public static Date getFirstDate(List<Bewegung> bewegungen) {
        Date first = new Date();
        for (Bewegung b : bewegungen) {
            Date x = b.getBuchungsdatum();
            if (x.before(first)) {
                first = x;
            }
        }
        return first;
    }

    public static Date getLastDate(List<Bewegung> bewegungen) {
        Date last = new Date(0l);
        for (Bewegung b : bewegungen) {
            Date x = b.getBuchungsdatum();
            if (x.after(last)) {
                last = x;
            }
        }
        return last;
    }
    
    
}
