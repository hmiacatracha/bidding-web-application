package es.udc.subasta.model.bidservice;

import java.math.BigDecimal;
import java.util.Calendar;

import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.bid.BidBlock;
import es.udc.subasta.model.product.ExpiredDateException;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.userprofile.UserProfile;

public interface BidService {

	public Bid createBid(Long bidUser, BigDecimal amount, Long productId)
			throws MinimumBidPriceException, InstanceNotFoundException,
			ExpiredDateException, DecreaseBidException, OwnedProductException;

	public Bid findBid(Long bidId) throws InstanceNotFoundException;

	public BidBlock findBidsByUserId(Long userId, int startIndex, int count)
			throws InstanceNotFoundException;
}
