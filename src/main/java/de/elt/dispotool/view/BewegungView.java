/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.view;

import de.elt.dispotool.service.BewegungService;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.extern.java.Log;

/**
 *
 * @author jonas.eicher
 */
@Named
@ViewScoped
@Log
public class BewegungView implements Serializable {

    @Inject
    @Delegate
    BewegungService bewegungService;

    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String matNr = params.get("matNr");

        bewegungService.setMaterialNummer(matNr);
    }
    
    @Getter
    int bestellmenge = 1000;
    public void setBestellmenge(int newValue) {
        bestellmenge = newValue;
        //bewegungService.setMaterialNummer("10010844");
    }
}
