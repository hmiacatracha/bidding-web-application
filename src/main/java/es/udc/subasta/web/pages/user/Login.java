package es.udc.subasta.web.pages.user;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;

import es.udc.subasta.model.userprofile.UserProfile;
import es.udc.subasta.model.userservice.IncorrectPasswordException;
import es.udc.subasta.model.userservice.UserService;
import es.udc.subasta.web.pages.Index;
import es.udc.subasta.web.pages.search.ProductDetails;
import es.udc.subasta.web.services.AuthenticationPolicy;
import es.udc.subasta.web.services.AuthenticationPolicyType;
import es.udc.subasta.web.util.CookiesManager;
import es.udc.subasta.web.util.UserSession;
import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;

@AuthenticationPolicy(AuthenticationPolicyType.NON_AUTHENTICATED_USERS)
public class Login {

	@Property
	private String loginName;

	@Property
	private String password;

	@Property
	private boolean rememberMyPassword;

	@SessionState(create = false)
	private UserSession userSession;

	@Inject
	private Cookies cookies;

	@Component
	private Form loginForm;

	@Inject
	private Messages messages;

	@Inject
	private UserService userService;
	
	@InjectPage
	private ProductDetails productDetails;

	private UserProfile userProfile = null;

	private Long productId;

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	void onValidateFromLoginForm() {

		if (!loginForm.isValid()) {
			return;
		}

		try {
			userProfile = userService.login(loginName, password, false);
		} catch (InstanceNotFoundException e) {
			loginForm.recordError(messages.get("error-authenticationFailed"));
		} catch (IncorrectPasswordException e) {
			loginForm.recordError(messages.get("error-authenticationFailed"));
		}

	}

	Object onSuccess() {

    	userSession = new UserSession();
        userSession.setUserProfileId(userProfile.getUserProfileId());
        userSession.setFirstName(userProfile.getFirstName());

        if (rememberMyPassword) {
            CookiesManager.leaveCookies(cookies, loginName, userProfile
                    .getEncryptedPassword());
        }
        if(productId != null){
        	productDetails.setProductId(productId);
        	return productDetails;
        }else{
        	return Index.class;
        }
    }

	Long onPassivate() {
		return this.productId;
	}

	void onActivate(Long productId) {
		this.productId = productId;
	}

}
