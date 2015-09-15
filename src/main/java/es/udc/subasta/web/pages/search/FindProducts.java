package es.udc.subasta.web.pages.search;

import java.util.List;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.udc.subasta.model.category.Category;
import es.udc.subasta.model.productservice.ProductService;

public class FindProducts {

	@Property
	private String productName;

	@InjectPage
	private ProductsFound productsFound;

	@Property
	private Long categoryId;

	@Inject
	private ProductService productService;

	public String getCategoriesString() {
		List<Category> categoriesList = productService.getAllCategories();
		String categoriesString = "";
		for (Category cat : categoriesList) {
			categoriesString += cat.getCategoryId().toString() + "="
					+ cat.getName() + ",";
		}
		categoriesString = categoriesString.substring(0,
				categoriesString.length() - 1);
		categoriesString = "-1=TODAS LAS CATEGORIAS," + categoriesString;
		return categoriesString;
	}

	Object onSuccess() {
		if (productName != null)
			productsFound.setproductName(productName);
		else 
			productsFound.setproductName("");
		if (categoryId != -1)
			productsFound.setCategoryId(categoryId);
		else
			productsFound.setCategoryId(null);
		return productsFound;
	}

}
