package pos.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public abstract class AbstractDao {
	
	@PersistenceContext
	protected EntityManager em;

	public <T> void addAbs(T pojoObject){
		em.persist(pojoObject);
	}
	public <T> List<T> selectAll(Class<T> pojo){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cr = cb.createQuery(pojo);
		Root<T> root = cr.from(pojo);
		cr.select(root);

		TypedQuery<T> query =  em.createQuery(cr);
		List<T> results = query.getResultList();
		return results;
	}
	public <T> T select(Class<T> pojo,Integer id){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cr = cb.createQuery(pojo);
		Root<T> root = cr.from(pojo);
		cr.select(root).where(cb.equal(root.get("id"), id));

		TypedQuery<T> query =  em.createQuery(cr);
		return getSingle(query);

	}
	protected <T> T getSingle(TypedQuery<T> query) {
		return query.getResultList().stream().findFirst().orElse(null);
	}

	protected EntityManager em() {
		return em;
	}

}
