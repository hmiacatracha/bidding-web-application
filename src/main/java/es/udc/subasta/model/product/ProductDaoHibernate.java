package es.udc.subasta.model.product;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import es.udc.pojo.modelutil.dao.GenericDaoHibernate;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;

@Repository("productDao")
public class ProductDaoHibernate extends GenericDaoHibernate<Product, Long>
		implements ProductDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Product> findByName(String name)
			throws InstanceNotFoundException {
		List<Product> products = (List<Product>) getSession()
				.createQuery("SELECT p FROM Product p WHERE p.name = :name")
				.setParameter("name", name).list();
		if (products == null) {
			throw new InstanceNotFoundException(name, Product.class.getName());
		} else {
			return products;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Product> findProductsByKeywordsCategory(String keywords,
			Long category, int startIndex, int count) {

		String[] words = null;
		/* Firtly, Create a generic query */
		String findQuery = "SELECT p FROM Product p WHERE endingDate > CURRENT_TIMESTAMP()";
		Query query;
		words = keywords != null ? keywords.split(" ") : null;

		if (words != null && words.length > 0) {
			for (int i = 0; i < words.length; i++)
				findQuery += " AND LOWER(p.name) LIKE  LOWER (?)";

			if (category != null)
				findQuery += " AND p.category.categoryId = :cat";
		}

		/* Order by p.name */
		findQuery += " ORDER BY p.endingDate DESC";
		query = getSession().createQuery(findQuery);

		/*
		 * Parameters
		 */

		if (words != null && words.length > 0) {
			for (int i = 0; i < words.length; i++)
				query.setParameter(i, "%" + words[i] + "%");
		}
		if (category != null)
			query.setParameter("cat", category);

		query.setFirstResult(startIndex);
		query.setMaxResults(count);

		return query.list();

	}

	@SuppressWarnings("unchecked")
	public List<Product> findAdvertisedProductsByUserId(Long userId,
			int startIndex, int count) {

		String findQuery = "SELECT u FROM Product u WHERE u.owner.userProfileId = :userId "
				+ "ORDER BY u.endingDate DESC";

		Query query = getSession().createQuery(findQuery);
		query.setParameter("userId", userId);
		query.setFirstResult(startIndex);
		query.setMaxResults(count);
		return query.list();
	}



}
