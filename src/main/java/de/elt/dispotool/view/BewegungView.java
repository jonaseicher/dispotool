/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.view;

import de.elt.dispotool.dao.BewegungDao;
import de.elt.dispotool.dao.BwaDao;
import de.elt.dispotool.dao.BestandDao;
import de.elt.dispotool.entities.Bewegung;
import de.elt.dispotool.entities.Bwa;
import de.elt.dispotool.service.BewegungService;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import lombok.extern.java.Log;
import org.primefaces.model.chart.BarChartModel;

/**
 *
 * @author jonas.eicher
 */
@Named
@ViewScoped
@Log
public class BewegungView implements Serializable {

    @Inject
    BewegungService bewegungService;

    Integer kostenProBestellung = 100;
    Double bestandsZins = 0.05;

    public Integer getKostenProBestellung() {
        return kostenProBestellung;
    }

    public void setKostenProBestellung(Integer kostenProBestellung) {
        this.kostenProBestellung = kostenProBestellung;
    }

    public Double getBestandsZins() {
        return bestandsZins;
    }

    public void setBestandsZins(Double bestandsZins) {
        this.bestandsZins = bestandsZins;
    }
    
    @PostConstruct
    public void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String matnr = params.get("matnr");

        bewegungService.setMaterialNummer(matnr);
    }
    
    public Integer getBestellmenge() {
        return bewegungService.getBestellmenge();
    }
    
    public void setBestellmenge(Integer x) {
        bewegungService.setBestellmenge(x);
    }
    public void initBewegungsMap() {
        bewegungService.initBewegungsMap();
    }

    public void initChartMap() {
        bewegungService.initChartMap();
    }

    public SortedMap<String, Integer> getBestandsMap() {
        return bewegungService.getBestandsMap();
    }

    public Integer getAnfangsbestand() {
        return bewegungService.getAnfangsbestand();
    }

    public void initBestandsMap() {
        bewegungService.initBestandsMap();
    }

    public String getBewegungsArray() {
        return bewegungService.getBewegungsArray();
    }

    public String getChartArray() {
        return bewegungService.getChartArray();
    }

    public String getBestandsArray() {
        return bewegungService.getBestandsArray();
    }

    public List<Bwa> getBwasOfMaterial() {
        return bewegungService.getBwasOfMaterial();
    }

    public List<Bwa> getBwasOfMaterial(String materialNummer) {
        return bewegungService.getBwasOfMaterial(materialNummer);
    }

    public void setMaterialNummer(String matnr) {
        bewegungService.setMaterialNummer(matnr);
    }

    public int getMaxAbgangsmenge() {
        return bewegungService.getMaxAbgangsmenge();
    }

    public Integer getMengeOfBwa(String bwa) {
        return bewegungService.getMengeOfBwa(bwa);
    }

    public BewegungDao getBewegungDao() {
        return bewegungService.getBewegungDao();
    }

    public BwaDao getBwaDao() {
        return bewegungService.getBwaDao();
    }

    public BestandDao getBestandDao() {
        return bewegungService.getBestandDao();
    }

    public List<Bewegung> getBewegungen() {
        return bewegungService.getBewegungen();
    }

    public List<Bewegung> getZugaenge() {
        return bewegungService.getZugaenge();
    }

    public List<Bewegung> getAbgaenge() {
        return bewegungService.getAbgaenge();
    }

    public int getAbgangsInterval() {
        return bewegungService.getAbgangsInterval();
    }

    public Date getFirst() {
        return bewegungService.getFirst();
    }

    public Date getLast() {
        return bewegungService.getLast();
    }

    public List<Bwa> getBwas() {
        return bewegungService.getBwas();
    }

    public Map<String, Integer> getVorzeichen() {
        return bewegungService.getVorzeichen();
    }

    public SortedMap<String, SortedMap<String, Integer>> getBewegungsMap() {
        return bewegungService.getBewegungsMap();
    }

    public SortedMap<String, SortedMap<String, Integer>> getChartMap() {
        return bewegungService.getChartMap();
    }

    public Map<String, Integer> getBwaMengen() {
        return bewegungService.getBwaMengen();
    }

    public String getMaterialNummer() {
        return bewegungService.getMaterialNummer();
    }

    public BarChartModel getBarChartModel() {
        return bewegungService.getBarChartModel();
    }

    public List<String> getTypes() {
        return bewegungService.getTypes();
    }

    public void setBewegungDao(BewegungDao bewegungDao) {
        bewegungService.setBewegungDao(bewegungDao);
    }

    public void setBwaDao(BwaDao bwaDao) {
        bewegungService.setBwaDao(bwaDao);
    }

    public void setBestandDao(BestandDao materialDao) {
        bewegungService.setBestandDao(materialDao);
    }

    public void setBewegungen(List<Bewegung> bewegungen) {
        bewegungService.setBewegungen(bewegungen);
    }

    public void setZugaenge(List<Bewegung> zugaenge) {
        bewegungService.setZugaenge(zugaenge);
    }

    public void setAbgaenge(List<Bewegung> abgaenge) {
        bewegungService.setAbgaenge(abgaenge);
    }

    public void setAbgangsInterval(int abgangsInterval) {
        bewegungService.setAbgangsInterval(abgangsInterval);
    }

    public void setFirst(Date first) {
        bewegungService.setFirst(first);
    }

    public void setLast(Date last) {
        bewegungService.setLast(last);
    }

    public void setBwas(List<Bwa> bwas) {
        bewegungService.setBwas(bwas);
    }

    public void setVorzeichen(Map<String, Integer> vorzeichen) {
        bewegungService.setVorzeichen(vorzeichen);
    }

    public void setBewegungsMap(SortedMap<String, SortedMap<String, Integer>> bewegungsMap) {
        bewegungService.setBewegungsMap(bewegungsMap);
    }

    public void setBestandsMap(SortedMap<String, Integer> bestandsMap) {
        bewegungService.setBestandsMap(bestandsMap);
    }

    public void setChartMap(SortedMap<String, SortedMap<String, Integer>> chartMap) {
        bewegungService.setChartMap(chartMap);
    }

    public void setBwaMengen(Map<String, Integer> bwaMengen) {
        bewegungService.setBwaMengen(bwaMengen);
    }

    public void setBarChartModel(BarChartModel barChartModel) {
        bewegungService.setBarChartModel(barChartModel);
    }

    public void setTypes(List<String> types) {
        bewegungService.setTypes(types);
    }
    
    public String getSimBestandsArray() {
        return bewegungService.getSimBestandsArray();
    }
    
}
