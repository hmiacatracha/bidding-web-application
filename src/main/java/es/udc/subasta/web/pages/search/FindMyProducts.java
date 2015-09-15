package es.udc.subasta.web.pages.search;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.product.ProductBlock;
import es.udc.subasta.model.productservice.ProductService;
import es.udc.subasta.web.services.AuthenticationPolicy;
import es.udc.subasta.web.services.AuthenticationPolicyType;
import es.udc.subasta.web.util.UserSession;

@AuthenticationPolicy(AuthenticationPolicyType.AUTHENTICATED_USERS)
public class FindMyProducts {

	private final static int PRODUCT_PER_PAGE = 4;

	private int startIndex = 0;

	@Property
	private Product product;

	private ProductBlock productBlock;

	@Inject
	private ProductService productService;

	@Inject
	private Locale locale;

	@Property
	@SessionState(create = false)
	private UserSession userSession;

	@Inject
	private Request request;

	@InjectComponent
	private Zone productsZoneTable;
	
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;
	
	public List<Product> getProducts() {
		return productBlock.getProducts();
	}

	public int getProductsPerPage() {
		return PRODUCT_PER_PAGE;
	}

	public int getRemainingTime() {
		int time = (int) product.getRemainingMinutes();
		if (time > 0)
			return time;
		else
			/* venció el tiempo */
			return 0;
	}

	public BigDecimal getCurrentPrice() {
		BigDecimal price = product.getCurrentPrice();
		System.out.println(price);
		if (product.getCurrentPrice() == null) {
			return product.getStartingPrice();
		}
		return price;
	}

	public Format getNumberFormat() {
		return NumberFormat.getInstance(locale);
	}

	public int getRowsPerPage() {
		return PRODUCT_PER_PAGE;
	}

	public Object[] getPreviousLinkContext() {
		if (startIndex - PRODUCT_PER_PAGE >= 0) {
			return new Object[] { startIndex - PRODUCT_PER_PAGE };
		} else {
			return null;
		}
	}

	public Object[] getNextLinkContext() {

		if (productBlock.getExistMoreProducts()) {
			return new Object[] { startIndex + PRODUCT_PER_PAGE };
		} else {
			return null;
		}

	}

	void onActivate(int index) throws ParseException, InstanceNotFoundException {
		this.startIndex = index;
		productBlock = productService.findAdvertisedProductsByUserId(
				userSession.getUserProfileId(), startIndex, PRODUCT_PER_PAGE);
	}

	int onPassivate() {
		return startIndex;
	}
	
	
	void onChangePage(int startIndex) throws InstanceNotFoundException, ParseException {
		onActivate(startIndex);
		renderAjax();
	}

	private void renderAjax() {
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender("productsZoneTable", productsZoneTable);
		}
	}

}
