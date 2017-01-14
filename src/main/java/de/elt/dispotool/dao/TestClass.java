/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.dao;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Jonas
 */
public class TestClass {
    
    public static void main (String[] args) {
        
        Integer i = 20;
        
        Map<String, Integer> m = new HashMap();
        m.put("asdf", i);
        i=10;
        System.out.println(m.get("asdf"));
        System.out.println(i);
    }
}
