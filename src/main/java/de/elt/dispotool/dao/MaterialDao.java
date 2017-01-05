package de.elt.dispotool.dao;

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
    log.log (Level.FINE, "filters: {0}", filters.toString());
    
    if (filters.containsKey("matNr")) {      
      jpql += " where mat.matNr like :matNr";
    }
    
    TypedQuery<Material> query = em.createQuery(jpql, Material.class).setFirstResult(first).setMaxResults(pageSize);
    
    if (filters.containsKey("matNr")) {      
      query.setParameter("matNr", "%" + filters.get("matNr").toString() + "%");
    }
    
    List<Material> list = query.getResultList();
    return list;
  }

  public int getRowCount() {
    Long i = (Long) em.createNativeQuery("select count(*) from stammdaten").getSingleResult();
    return i.intValue();
  }

}
