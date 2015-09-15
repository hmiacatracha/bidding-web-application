package es.udc.subasta.web.pages.search;

import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.product.ExpiredDateException;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.product.ProductDao;
import es.udc.subasta.model.productservice.ProductService;
import es.udc.subasta.web.pages.bid.CreateBid;
import es.udc.subasta.web.pages.user.Login;
import es.udc.subasta.web.util.UserSession;

public class ProductDetails {

	private Long productId;
	private Product product;
	private Date startDate;

	@Inject
	private ProductService productService;

	@Inject
	private Locale locale;

	@InjectPage
	private CreateBid createBid;

	@InjectPage
	private Login login;

	@SessionState(create = false)
	private UserSession userSession;

	private boolean bidBoolean;

	public void setBidBoolean(boolean b) {
		this.bidBoolean = b;
	}

	public boolean getBidBoolean() {
		return this.bidBoolean;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Product getProduct() {
		return product;
	}

	public int getRemainingTime() {
		return (int) product.getRemainingMinutes();
	}

	public Boolean getIsExpired() {
		return (product.getRemainingMinutes() > 0);
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar cal) {
		this.startDate = cal.getTime();
	}

	public String getCurrentWinner() {
		Bid bid = product.getCurrentBid();
		if (bid == null) {
			return "";
		} else {
			return bid.getUser().getLoginName();
		}
	}

	void onActivate(Long productId, boolean bidBoolean) {

		this.productId = productId;
		this.bidBoolean = bidBoolean;
		try {
			product = productService.findByProductId(productId);
			this.setStartDate(product.getStartingDate());
		} catch (InstanceNotFoundException e) {
		}

	}

	Object[] onPassivate() {
		return new Object[] { productId, bidBoolean };
	}

	Object onSuccess() {
		if (userSession == null) {

			login.setProductId(productId);
			return login;
		} else {

			createBid.setProductIdContext(product.getProductId());

			return createBid;
		}
	}

	public DateFormat getDateFormat() {
		return DateFormat.getDateInstance(DateFormat.SHORT, locale);
	}

	public Format getNumberFormat() {
		return NumberFormat.getInstance(locale);
	}

}
