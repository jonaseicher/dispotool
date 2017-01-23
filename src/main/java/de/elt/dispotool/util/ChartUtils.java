/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.util;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.extern.java.Log;

/**
 *
 * @author Jonas
 */
@Log
public class ChartUtils {
    
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
    public static String makeChartData(SortedMap<String, SortedMap<String, Integer>> data) {

        String cols = "{id:\"date\",label:\"Datum\",type:\"date\"}";

        SortedSet set = new TreeSet();
        for (SortedMap subMap : data.values()) {
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
            String[] dateArr = dateKey.split(",");
            Integer month = Integer.valueOf(dateArr[1]);
            month--;
            
            String fixedDate = dateArr[0] + ","+month + "," + dateArr[2];
            
            row += "{c:[{v:\"Date(" + fixedDate + ")\"}";
            SortedMap<String, Integer> rowData = data.get(dateKey);

            for (int i = 0; i < labels.size(); i++) {
                String label = labels.get(i);
                Integer value = rowData.get(label);
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
