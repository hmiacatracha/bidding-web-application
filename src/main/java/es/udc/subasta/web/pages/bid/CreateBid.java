package es.udc.subasta.web.pages.bid;

import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.bidservice.BidService;
import es.udc.subasta.model.bidservice.DecreaseBidException;
import es.udc.subasta.model.bidservice.MinimumBidPriceException;
import es.udc.subasta.model.bidservice.OwnedProductException;
import es.udc.subasta.model.product.ExpiredDateException;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.productservice.ProductService;
import es.udc.subasta.web.pages.search.ProductDetails;
import es.udc.subasta.web.services.AuthenticationPolicy;
import es.udc.subasta.web.services.AuthenticationPolicyType;
import es.udc.subasta.web.util.UserSession;

@AuthenticationPolicy(AuthenticationPolicyType.AUTHENTICATED_USERS)
public class CreateBid {

	@Property
	private String amount;

	@SessionState(create = false)
	private UserSession userSession;

	@Component
	private Form createBidForm;

	@Component(id = "amount")
	private TextField amountTextField;

	@Inject
	private Messages messages;

	@Inject
	private BidService bidService;

	@Inject
	private ProductService productService;

	@Inject
	private Locale locale;

	private Bid bid;

	@InjectPage
	private ProductDetails productDetails;

	private Long productIdContext;

	private double amountAsDouble;

	public Long getProductIdContext() {
		return productIdContext;
	}

	public void setProductIdContext(Long productIdContext) {
		this.productIdContext = productIdContext;
	}

	Long onPassivate() {
		return productIdContext;
	}

	void onActivate(Long productIdContext) {
		this.productIdContext = productIdContext;
	}

	void onValidateFromCreateBidForm() throws InstanceNotFoundException,
			ExpiredDateException {

		if (!createBidForm.isValid()) {
			return;
		}
		NumberFormat numberFormatter = NumberFormat.getInstance(locale);
		ParsePosition position = new ParsePosition(0);
		Number number = numberFormatter.parse(amount, position);

		if (position.getIndex() != amount.length()) {
			createBidForm.recordError(amountTextField,
					messages.format("error-incorrectNumberFormat", amount));
			return;
		}

		amountAsDouble = number.doubleValue();
		if (amountAsDouble <= 0) {
			createBidForm.recordError(amountTextField,
					messages.format("error-insufficientAmount"));
			return;
		}

		BigDecimal amountBg = new BigDecimal(amount);
		if (productService.viewProductDetails(productIdContext).getCurrentBid() == null) {
			if (amountBg.compareTo(productService.viewProductDetails(
					productIdContext).getStartingPrice()) == -1)
				createBidForm.recordError(amountTextField, messages.format(
						"error-minimunBidPriceException", amount));
			return;
		} else {
			BigDecimal incremento = new BigDecimal(0.5);
			if (amountBg.compareTo(productService
					.viewProductDetails(productIdContext).getCurrentPrice()
					.add(incremento)) == -1)
				createBidForm.recordError(amountTextField, messages.format(
						"error-minimunBidPriceExceptionO5", amount));
			return;
		}
	}

	public Format getNumberFormat() {
		return NumberFormat.getInstance(locale);
	}

	public String getName() throws InstanceNotFoundException {
		return productService.findByProductId(productIdContext).getName();
	}

	public BigDecimal getMinimunBid() throws InstanceNotFoundException {
		Product product = productService.findByProductId(productIdContext);
		if (product.getCurrentBid() == null) {
			return product.getStartingPrice();
		}
		return (productService.findByProductId(productIdContext)
				.getCurrentPrice().add(new BigDecimal(0.5)));
	}

	public String getProductName() throws InstanceNotFoundException {
		Product product = productService.findByProductId(productIdContext);
		return product.getName();
	}

	Object onSuccess() {
		BigDecimal amount2 = new BigDecimal(amount);

		try {
			bid = bidService.createBid(userSession.getUserProfileId(), amount2,
					productIdContext);
		} catch (InstanceNotFoundException e) {
			createBidForm.recordError(messages
					.get("error-createFailed-InstanceNotFoundException"));
		} catch (MinimumBidPriceException e) {
			createBidForm.recordError(messages
					.get("error-createFailed-MinimumBidPriceException"));
		} catch (ExpiredDateException e) {
			createBidForm.recordError(messages
					.get("error-createFailed-ExpiredDateException"));
		} catch (DecreaseBidException e) {
			createBidForm.recordError(messages
					.get("error-createFailed-DecreaseBidException"));
		} catch (OwnedProductException e) {
			createBidForm.recordError(messages
					.get("error-createFailed-OwnedProductException"));
			return null;
		}

		productDetails.setProductId(productIdContext);
		productDetails.setBidBoolean(true);

		return productDetails;
	}

}
