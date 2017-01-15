package de.elt.dispotool.dao;

import de.elt.dispotool.entities.Bewegung;
import de.elt.dispotool.entities.Material;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import lombok.extern.java.Log;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.primefaces.model.SortOrder;

/**
 *
 * @author jonas.eicher
 */
@Stateless
@Log
public class MaterialDao extends AbstractBaseDao<Material> {

    @PersistenceContext(unitName = "mysql")
    EntityManager em;

    public MaterialDao() {
        super(Material.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Material> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

        String jpql = "select mat from Material mat";
        log.log(Level.FINE, "filters: {0}", filters.toString());

        jpql += " where mat.bestand2016 > 0";

        if (filters.containsKey("matNr")) {
            jpql += " and mat.matNr like :matNr";
        }

        TypedQuery<Material> query = em.createQuery(jpql, Material.class).setFirstResult(first).setMaxResults(pageSize);

        if (filters.containsKey("matNr")) {
            query.setParameter("matNr", "%" + filters.get("matNr").toString() + "%");
        }

        List<Material> list = query.getResultList();
        return list;
    }

    public int getRowCount() {
        Long i = (Long) em.createNativeQuery("select count(*) from stammdaten where bestand2016 > 0 ").getSingleResult();
        return i.intValue();
    }

    public Material getByMaterialnummer(String matNr) {
        String jpql = "select mat from Material mat where mat.matNr = :matNr";
        Material mat = em.createQuery(jpql, Material.class).setParameter("matNr", matNr).getSingleResult();
        return mat;
    }

    public Integer getBestand2015(String matNr) {
        return getBestand(matNr, "2015");
    }

    public Integer getBestand2016(String matNr) {
        return getBestand(matNr, "2016");
    }

    private Integer getBestand(String matNr, String year) {
        Integer bestand = 0;
        try {
            bestand = em.createQuery("select b.bestand" + year + " from Bestand b where b.matNr = :matNr", Integer.class).setParameter("matNr", matNr).getSingleResult();
        } catch (javax.persistence.NoResultException ex) {
            log.info("Kein Anfangsbestand fuer " + matNr + " gefunden.");
        }
        return bestand;
    }

}
