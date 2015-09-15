package es.udc.subasta.model.productservice;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.category.Category;
import es.udc.subasta.model.product.ExpiredDateException;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.product.ProductBlock;

public interface ProductService {

	public Product insertAd(String name, String description,
			BigDecimal startingPrice, Calendar endingDate,
			String deliveryInformation, Long ownerId, Long categoryId)
			throws DatesException, InstanceNotFoundException;

	public Product viewProductDetails(Long productId)
			throws InstanceNotFoundException, ExpiredDateException;

	public ProductBlock findProductsByKeywordsCategory(String keywords,
			Long categoryId, int startIndex, int count);

	public ProductBlock findAdvertisedProductsByUserId(Long userId,
			int startIndex, int count) throws InstanceNotFoundException;

	public List<Category> getAllCategories();

	public Product findByProductId(Long Id) throws InstanceNotFoundException;

}
