package de.elt.dispotool.dao;

import de.elt.dispotool.entities.Bwa;
import java.util.List;
import lombok.extern.java.Log;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author jonas.eicher
 */
@Stateless
@Log
public class BwaDao extends AbstractBaseDao<Bwa> {

  @PersistenceContext(unitName = "mysql")
  EntityManager em;

  public BwaDao() {
    super(Bwa.class);
  }

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public Bwa getById(String bwa) {
    String jpql = "select b from Bwa b where b.bwa = :bwa";
    Bwa b = em.createQuery(jpql, Bwa.class).setParameter("bwa", bwa).getSingleResult();
    return b;
  }


  public List<Bwa> getBewegungsartenOfMaterial(String materialnummer) {
    String jpql = "select distinct b.bewegungsart from Bewegung b where b.materialnummer = :materialnummer";
    List<String> bwaList = em.createQuery(jpql, String.class).setParameter("materialnummer", materialnummer).getResultList();    
    List<Bwa> bwas = em.createQuery("select bwart from Bwa bwart where bwart.bwa in :bwaList", Bwa.class).setParameter("bwaList", bwaList).getResultList();    
    return bwas;
  }
  
}
