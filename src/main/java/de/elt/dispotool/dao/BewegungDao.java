package de.elt.dispotool.dao;

import de.elt.dispotool.entities.Bewegung;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class BewegungDao extends AbstractBaseDao<Bewegung> {

    @PersistenceContext(unitName = "mysql")
    EntityManager em;

    public BewegungDao() {
        super(Bewegung.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Bewegung getById(String id) {
        String jpql = "select b from Bewegung b where b.id = :id";
        Bewegung b = em.createQuery(jpql, Bewegung.class).setParameter("id", id).getSingleResult();
        return b;
    }

    public List<Bewegung> getByMaterialnummer(String materialnummer) {
        String jpql = "select b from Bewegung b where b.materialnummer = :materialnummer";
        List<Bewegung> b = em.createQuery(jpql, Bewegung.class).setParameter("materialnummer", materialnummer).getResultList();
        return b;
    }

    public List<String> getBewegungsartenOfMaterial(String materialnummer) {
        String jpql = "select distinct b.bewegungsart from Bewegung b where b.materialnummer = :materialnummer";
        List<String> b = em.createQuery(jpql, String.class).setParameter("materialnummer", materialnummer).getResultList();
        return b;
    }

    public List<Bewegung> getBewegungen(String materialnummer, String bewegungsart) {
        String jpql = "select b from Bewegung b where b.materialnummer = :materialnummer and b.bewegungsart = :bewegungsart";
        List<Bewegung> b = em.createQuery(jpql, Bewegung.class)
                .setParameter("materialnummer", materialnummer).setParameter("bewegungsart", bewegungsart)
                .getResultList();
        return b;
    }

    public List<String> getMaterialNummern() {
        String jpql = "select distinct b.materialnummer from Bewegung b"; // where b.materialnummer like '10010%'";
        List matnums = em.createQuery(jpql, String.class).getResultList();
        //log.log(Level.FINE, "matnums:{0}", matnums);
        return matnums;
    }

    public Map<String, Integer> getBwaMengen(String matNr) {
        String sql = "select bewegungsart, sum(bewegungsdaten2.menge) from dispotool.bewegungsdaten2 where materialnummer='" + matNr + "' group by bewegungsart";
        List<Object[]> results = em.createNativeQuery(sql).getResultList();

        Map<String, Integer> map = new HashMap();

        results.stream().forEach((record) -> {
            String bwa = (String) record[0];
            BigDecimal menge = (BigDecimal) record[1];
            map.put(bwa, menge.intValue());
        });
        return map;
    }
}
