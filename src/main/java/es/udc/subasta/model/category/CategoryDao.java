package es.udc.subasta.model.category;

import java.util.List;

import es.udc.pojo.modelutil.dao.GenericDao;

public interface CategoryDao extends GenericDao<Category, Long> {

	List<Category> getAll();

}
