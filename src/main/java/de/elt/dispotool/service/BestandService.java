/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.service;

import de.elt.dispotool.dao.BewegungDao;

/**
 *
 * @author Jonas
 */
public class BestandService {
    
    
    String matnr;
    BewegungDao bewegungDao;

    public BestandService(String matnr, BewegungDao dao) {
        this.matnr = matnr;
        this.bewegungDao = dao;
    }
    
}
