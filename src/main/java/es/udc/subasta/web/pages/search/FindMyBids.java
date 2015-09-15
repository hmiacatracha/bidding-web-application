package es.udc.subasta.web.pages.search;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.bid.BidBlock;
import es.udc.subasta.model.bidservice.BidService;
import es.udc.subasta.web.services.AuthenticationPolicy;
import es.udc.subasta.web.services.AuthenticationPolicyType;
import es.udc.subasta.web.util.UserSession;

@AuthenticationPolicy(AuthenticationPolicyType.AUTHENTICATED_USERS)
public class FindMyBids {
	private final static int BIDS_PER_PAGE = 4;
	private static final DateFormat DATE_FORMAT =  new SimpleDateFormat("HH:mm:ss"); 


	private int startIndex = 0;
	private BidBlock bidBlock;

	@Property
	private Bid bid;


	@Property
	@SessionState(create = false)
	private UserSession userSession;

	@Inject
	private BidService bidService;

	@Inject
	private Locale locale;
	
	@Inject
	private Request request;

	@InjectComponent
	private Zone bidsZoneTable;
	
	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	public List<Bid> getbids() {
		return bidBlock.getBids();
	}

	public String getWiner(){
		return bid.getCurrentWinnerProduct().getLoginName();	
	}
	
	public Date getFecha() {
		Calendar cal = bid.getDate();
		Date date = cal.getTime();
		return date;
	}
	
	public Date getHora(){
		Calendar cal = bid.getDate();
		Date date = cal.getTime();
		return date;
	}

	
	public Format getNumberFormat() {
		return NumberFormat.getInstance(locale);
	}

	public Format getDateFormat() {
		return DateFormat.getDateInstance(DateFormat.SHORT, locale);
	}
	
	public String getHourFormat() {
		Date hora = this.getFecha();
		return DATE_FORMAT.format(hora);
	}
	
	public Object[] getPreviousLinkContext() {
		if (startIndex - BIDS_PER_PAGE >= 0) {
			return new Object[] { startIndex - BIDS_PER_PAGE };
		} else {
			return null;
		}
	}

	public Object[] getNextLinkContext() {

		if (bidBlock.getExistMoreBids()) {
			return new Object[] { startIndex + BIDS_PER_PAGE };
		} else {
			return null;
		}

	}

	void onActivate(int startIndex) throws InstanceNotFoundException {
		this.startIndex = startIndex;
		bidBlock = bidService.findBidsByUserId(userSession.getUserProfileId(),
				startIndex, BIDS_PER_PAGE);

	}

	Object[] onPassivate() {
		return new Object[] { startIndex };
	}
	
	
	void onChangePage(int startIndex) throws InstanceNotFoundException {
		onActivate(startIndex);
		renderAjax();
	}

	private void renderAjax() {
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender("bidsZoneTable", bidsZoneTable);
		}
	}

}
