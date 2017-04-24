/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.service;

import de.elt.dispotool.entities.Bewegung;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import lombok.Data;
import lombok.extern.java.Log;

/**
 *
 * @author Jonas
 */
@Data
@Log
public class InventoryMap {

    SortedMap<String, Integer> data;
    String matnr;
    Integer min, max;
    Date first, last;
    SortedMap<String, SortedMap<String, Integer>> bewegungsMap;
    
    public InventoryMap(List<Bewegung> bewegungen) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy,MM,dd");
//        Collections.sort(bewegungen, new BwaComparator());
        bewegungsMap = new TreeMap();
        for(Bewegung b : bewegungen) {
            String bwa = b.getBewegungsart();
            SortedMap<String, Integer> subMap = bewegungsMap.get(bwa);
            if (subMap == null) {
                subMap = new TreeMap();
                bewegungsMap.put(bwa, subMap);
            }
            subMap.put(format.format(b.getBuchungsdatum()), b.getMenge());
        }
    }

}


 class BwaComparator implements Comparator<Bewegung> {

    @Override
    public int compare(Bewegung o1, Bewegung o2) {
        return o1.getBewegungsart().compareToIgnoreCase(o2.getBewegungsart());
    }
    
}

 class DateComparator implements Comparator<Bewegung> {

    @Override
    public int compare(Bewegung o1, Bewegung o2) {
        return o1.getBuchungsdatum().compareTo(o2.getBuchungsdatum());
    }
    
}