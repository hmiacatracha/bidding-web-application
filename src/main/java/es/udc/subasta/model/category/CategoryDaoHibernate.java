package es.udc.subasta.model.category;

import java.util.List;

import org.springframework.stereotype.Repository;

import es.udc.pojo.modelutil.dao.GenericDaoHibernate;

@Repository("CategoryDao")
public class CategoryDaoHibernate extends GenericDaoHibernate<Category, Long>
		implements CategoryDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Category> getAll() {
		return (List<Category>) getSession().createQuery(
				"SELECT c FROM Category c").list();
	}

}