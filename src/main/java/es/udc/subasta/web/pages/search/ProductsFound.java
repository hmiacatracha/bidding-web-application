package es.udc.subasta.web.pages.search;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;

import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.product.ProductBlock;
import es.udc.subasta.model.productservice.ProductService;

public class ProductsFound {

	private final static int PRODUCTS_PER_PAGE = 4;

	private String productName;
	private Long categoryId;

	private int startIndex = 0;

	@Property
	private Product product;

	private ProductBlock productBlock;

	@Inject
	private ProductService productService;

	@Inject
	private Locale locale;

	@Inject
	private Request request;

	@InjectComponent
	private Zone productsZoneTable;
	
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	public Long getProductId() {
		return product.getProductId();
	}

	public void setproductName(String productName) {
		this.productName = productName;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public List<Product> getProducts() {
		return productBlock.getProducts();
	}

	public int getProductsPerPage() {
		return PRODUCTS_PER_PAGE;
	}

	public Format getFormat() {
		return NumberFormat.getInstance(locale);
	}

	public int getRemainingTime() {
		int time = (int) product.getRemainingMinutes();
		if (time > 0)
			return time;
		else
			/* venció el tiempo */
			return 0;
	}

	public BigDecimal getPrice() {
		if (product.getCurrentBid() == null) {
			return product.getStartingPrice();
		}
		return product.getCurrentPrice();
	}

	Object onActivate(String productName, Long categoryId, int startIndex) {
		this.productName = productName;
		this.startIndex = startIndex;
		productBlock = productService.findProductsByKeywordsCategory(
				productName, categoryId, startIndex, PRODUCTS_PER_PAGE);
		return null;
	}

	Object[] onPassivate() {
		return new Object[] { productName, categoryId, startIndex };
	}

	public Object[] getPreviousLinkContext() {

		if (startIndex - PRODUCTS_PER_PAGE >= 0) {
			return new Object[] { productName, categoryId,
					startIndex - PRODUCTS_PER_PAGE };
		} else {
			return null;
		}

	}

	public Object[] getNextLinkContext() {

		if (productBlock.getExistMoreProducts()) {
			return new Object[] { productName, categoryId,
					startIndex + PRODUCTS_PER_PAGE };
		} else {
			return null;
		}

	}
	
	void onChangePage(String productName, Long categoryId, int startIndex) {
		onActivate(productName,categoryId,startIndex);
		renderAjax();
	}

	private void renderAjax() {
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender("productsZoneTable", productsZoneTable);
		}
	}


}
