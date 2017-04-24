/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elt.dispotool.dao;

import de.elt.dispotool.entities.Bestand;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.java.Log;

@Stateless
@Log
public class BestandDao extends AbstractBaseDao<Bestand> {

    @PersistenceContext(unitName = "mysql")
    EntityManager em;

    public BestandDao() {
        super(Bestand.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Integer getBestand2016(String matnr) {
        return getBestand(matnr, "2016");
    }

    private Integer getBestand(String matnr, String year) {
        Integer bestand = 0;
        try {
            bestand = em.createQuery("select b.bestand" + year + " from Bestand b where b.matnr = :matnr", Integer.class).setParameter("matnr", matnr).getSingleResult();
        } catch (javax.persistence.NoResultException ex) {
            log.info("Kein Anfangsbestand fuer " + matnr + " gefunden.");
        }
        return bestand;
    }
}
