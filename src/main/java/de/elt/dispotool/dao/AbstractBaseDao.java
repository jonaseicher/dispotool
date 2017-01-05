package de.elt.dispotool.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.QueryByExamplePolicy;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * Abstract base class for DAO services.
 *
 * @author asen
 *
 * @param <T>
 */
public abstract class AbstractBaseDao<T> {

  public static int FETCH_SIZE = 300;
  public static int SQL_IN_LIMIT = 1000;

  protected Class<T> entityClass;

  public AbstractBaseDao(Class<T> entityClass) {
    this.entityClass = entityClass;
  }

  /**
   * Returns an instance of entityManager.
   *
   * @return
   */
  protected abstract EntityManager getEntityManager();

  /**
   * Creates an entity.
   *
   * @param entity
   */
  public void persist(T entity) {
    try {
      getEntityManager().persist(entity);
    } catch (javax.validation.ConstraintViolationException cve) {
      for (ConstraintViolation<? extends Object> v : cve.getConstraintViolations()) {
        System.err.println(v);
        System.err.println("==>> " + v.getMessage());
        throw cve;
      }
    }
  }

  /**
   * Updates an entity.
   *
   * @param entity
   */
  public T merge(T entity) {
    return getEntityManager().merge(entity);
  }

  /**
   * Deletes an entity.
   *
   * @param entity
   */
  public void remove(T entity) {
    entity = getEntityManager().merge(entity);
    getEntityManager().remove(entity);
  }

  /**
   *
   * @param criteria
   * @param osRolename must not be null
   * @param attributeToInclude
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<T> queryByExample(T criteria, String osRolename, String... attributeToInclude) {
    QueryByExamplePolicy policy = new QueryByExamplePolicy();
    policy.excludeDefaultPrimitiveValues();
    policy.addSpecialOperation(String.class, "likeIgnoreCase");
    if (attributeToInclude != null) {
      for (int i = 0; i < attributeToInclude.length; i++) {
        policy.alwaysIncludeAttribute(entityClass, attributeToInclude[i]);
      }
    }
    ReadAllQuery q = new ReadAllQuery(criteria, policy);

    // Wrap the native query in a standard JPA Query and execute it
    Query query = JpaHelper.createQuery(q, getEntityManager());
    return (List<T>) query.getResultList();
  }

  /**
   * Queries the number of results for the given criteria.
   *
   * @param criteria the example object
   * @param osRolename must not be null
   * @param attributeToInclude attributes to include although they are null or
   * otherwise empty
   * @return
   */
  public Integer countByExample(T criteria, String osRolename, String... attributeToInclude) {
    QueryByExamplePolicy policy = new QueryByExamplePolicy();
    policy.excludeDefaultPrimitiveValues();
    policy.addSpecialOperation(String.class, "likeIgnoreCase");
    if (attributeToInclude != null) {
      for (int i = 0; i < attributeToInclude.length; i++) {
        policy.alwaysIncludeAttribute(entityClass, attributeToInclude[i]);
      }
    }
    ExpressionBuilder eb = new ExpressionBuilder(entityClass);
    ReportQuery q = new ReportQuery(eb);
    q.setQueryByExamplePolicy(policy);
    q.setExampleObject(criteria);
    q.addCount();

    // Wrap the native query in a standard JPA Query and execute it
    Query query = JpaHelper.createQuery(q, getEntityManager());

    return (Integer) query.getSingleResult();
  }

  public void refresh() {
    getEntityManager().getEntityManagerFactory().getCache().evictAll();
  }

  public void refresh(T entity) {
    getEntityManager().refresh(entity);
  }

  public List<T> getAll() {
    return getEntityManager()
            .createQuery("select x from " + entityClass.getName() + " x", entityClass)
            .getResultList();
  }
}
