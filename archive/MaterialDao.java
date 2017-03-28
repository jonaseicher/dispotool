//package de.elt.dispotool.dao;
//
//import de.elt.dispotool.entities.Bewegung;
//import de.elt.dispotool.entities.Material;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Level;
//import lombok.extern.java.Log;
//import javax.ejb.Stateless;
//import javax.persistence.EntityManager;
//import javax.persistence.PersistenceContext;
//import javax.persistence.TypedQuery;
//import org.primefaces.model.SortOrder;
//
///**
// *
// * @author jonas.eicher
// */
//@Stateless
//@Log
//public class MaterialDao extends AbstractBaseDao<Material> {
//
//    @PersistenceContext(unitName = "mysql")
//    EntityManager em;
//
//    public MaterialDao() {
//        super(Material.class);
//    }
//
//    @Override
//    protected EntityManager getEntityManager() {
//        return em;
//    }
//
//    public List<Material> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
//
//        String jpql = "select mat from Material mat";
//        log.log(Level.FINE, "filters: {0}", filters.toString());
//
//        jpql += " where mat.bestand2016 > 0";
//
//        if (filters.containsKey("matnr")) {
//            jpql += " and mat.matnr like :matnr";
//        }
//
//        TypedQuery<Material> query = em.createQuery(jpql, Material.class).setFirstResult(first).setMaxResults(pageSize);
//
//        if (filters.containsKey("matnr")) {
//            query.setParameter("matnr", "%" + filters.get("matnr").toString() + "%");
//        }
//
//        List<Material> list = query.getResultList();
//        return list;
//    }
//
//    public int getRowCount() {
//        Long i = (Long) em.createNativeQuery("select count(*) from stammdaten where bestand2016 > 0 ").getSingleResult();
//        return i.intValue();
//    }
//
//    public Material getByMaterialnummer(String matnr) {
//        String jpql = "select mat from Material mat where mat.matnr = :matnr";
//        Material mat = em.createQuery(jpql, Material.class).setParameter("matnr", matnr).getSingleResult();
//        return mat;
//    }
//
//    public Integer getBestand2015(String matnr) {
//        return getBestand(matnr, "2015");
//    }
//
//    public Integer getBestand2016(String matnr) {
//        return getBestand(matnr, "2016");
//    }
//
//    private Integer getBestand(String matnr, String year) {
//        Integer bestand = 0;
//        try {
//            bestand = em.createQuery("select b.bestand" + year + " from Bestand b where b.matnr = :matnr", Integer.class).setParameter("matnr", matnr).getSingleResult();
//        } catch (javax.persistence.NoResultException ex) {
//            log.info("Kein Anfangsbestand fuer " + matnr + " gefunden.");
//        }
//        return bestand;
//    }
//
//}
