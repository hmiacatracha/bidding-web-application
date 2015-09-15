package es.udc.subasta.web.pages.product;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.SelectModelFactory;

import es.udc.subasta.model.category.Category;
import es.udc.subasta.model.category.CategoryDao;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.productservice.DatesException;
import es.udc.subasta.model.productservice.ProductService;
import es.udc.subasta.model.userprofile.UserProfile;
import es.udc.subasta.model.userservice.IncorrectPasswordException;
import es.udc.subasta.model.userservice.UserService;
import es.udc.subasta.web.pages.Index;
import es.udc.subasta.web.services.AuthenticationPolicy;
import es.udc.subasta.web.services.AuthenticationPolicyType;
import es.udc.subasta.web.util.CookiesManager;
import es.udc.subasta.web.util.UserSession;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@AuthenticationPolicy(AuthenticationPolicyType.AUTHENTICATED_USERS)
public class InsertAd {

	@Property
	private String advertName;

	@Property
	private String description;

	@Property
	private BigDecimal startingPrice;

	@Property
	private int duracion;

	@Property
	private String deliveryInformation;

	@Property
	private Long ownerId;

	@Property
	private Long categoryId;

	@SessionState(create = false)
	private UserSession userSession;

	@Component
	private Form insertAdForm;

	@Inject
	private Messages messages;

	@Inject
	private ProductService productService;
	
	private Product product;

	@InjectPage
	private AdCreated adCreated;

	public String getCategoriesString() {
		List<Category> categoriesList = productService.getAllCategories();
		String categoriesString = "";
		for(Category cat : categoriesList){
			categoriesString += cat.getCategoryId().toString() + "=" + cat.getName() + ",";
		}
		categoriesString = categoriesString.substring(0, categoriesString.length()-1);
		return categoriesString;
	}
	
	void onValidateFromInsertAdForm() throws ParseException {

		if (!insertAdForm.isValid()) {
			return;
		}

		
	}

	Object onSuccess() {
		try {
			Calendar fechaFin = Calendar.getInstance();
			fechaFin.add(Calendar.MINUTE, duracion);
			ownerId = userSession.getUserProfileId();
			product = productService.insertAd(advertName, description,
					startingPrice, fechaFin, deliveryInformation, ownerId,
					categoryId);
		} catch (InstanceNotFoundException e) {
			insertAdForm.recordError(messages
					.get("error-insertFailed-InstanceNotFoundException"));
		} catch (DatesException e) {
			insertAdForm.recordError(messages
					.get("error-insertFailed-DatesException"));
		}
		adCreated.setAdId(product.getProductId());

		return adCreated;
	}
}