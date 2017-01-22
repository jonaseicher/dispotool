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
    
    
    String matNr;
    BewegungDao bewegungDao;

    public BestandService(String matNr, BewegungDao dao) {
        this.matNr = matNr;
        this.bewegungDao = dao;
    }
    
}
