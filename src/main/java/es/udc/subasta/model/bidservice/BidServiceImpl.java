package es.udc.subasta.model.bidservice;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.hibernate.metamodel.relational.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.udc.pojo.modelutil.exceptions.InstanceNotFoundException;
import es.udc.subasta.model.bid.Bid;
import es.udc.subasta.model.bid.BidBlock;
import es.udc.subasta.model.bid.BidDao;
import es.udc.subasta.model.product.ExpiredDateException;
import es.udc.subasta.model.product.Product;
import es.udc.subasta.model.product.ProductDao;
import es.udc.subasta.model.userprofile.UserProfile;
import es.udc.subasta.model.userprofile.UserProfileDao;

@Service("bidService")
@Transactional
public class BidServiceImpl implements BidService {

	@Autowired
	private UserProfileDao userProfileDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private BidDao bidDao;

	@Override
	public Bid createBid(Long bidUserId, BigDecimal amount, Long productId)
			throws MinimumBidPriceException, InstanceNotFoundException,
			ExpiredDateException, DecreaseBidException, OwnedProductException {

		Product product = productDao.find(productId);
		Bid currentBid = product.getCurrentBid();
		BigDecimal currentPrice = product.getCurrentPrice();
		UserProfile owner = product.getOwner();
		System.out.println(owner.getUserProfileId() + "  " + bidUserId);
		if (owner.getUserProfileId() == bidUserId) {
			throw new OwnedProductException(bidUserId);
		}
		UserProfile userBid = userProfileDao.find(bidUserId);
		UserProfile currentWinnerProduct = null;

		Calendar now = Calendar.getInstance();
		if (now.after(product.getEndingDate())) {
			throw new ExpiredDateException(productId);
		}

		if (product.getCurrentBid() != null)
			currentWinnerProduct = product.getCurrentBid().getUser();

		if (currentWinnerProduct != null) {
			// AUMENTAR PUJA
			if (currentWinnerProduct.getUserProfileId() == bidUserId) {
				if (amount.compareTo(product.getCurrentPrice()) > 0) {
					Bid bid = new Bid(userBid, amount, now, currentPrice,
							currentWinnerProduct, product);
					product.setCurrentBid(bid);
					bidDao.save(bid);
					return bid;
				} else {
					throw new DecreaseBidException(product.getCurrentBid()
							.getBidId());
				}
				// SOBREPUJAR
			} else {

				currentPrice = product.getCurrentPrice();
				BigDecimal MinimumIncrementBid = new BigDecimal(0.5);
				if (currentPrice.add(MinimumIncrementBid).compareTo(amount) == 1) {
					throw new MinimumBidPriceException(currentBid.getBidId());
				}

				if (amount.compareTo(currentBid.getAmount().add(
						MinimumIncrementBid)) == 1) {
					product.setCurrentPrice(currentBid.getAmount().add(
							MinimumIncrementBid));
				} else {
					product.setCurrentPrice(amount);
				}

			}
			// PUJAR POR PRIMERA VEZ
		} else {
			currentPrice = product.getStartingPrice();
			if (currentPrice.compareTo(amount) == 1) {
				throw new MinimumBidPriceException(currentBid.getBidId());
			}
			product.setCurrentPrice(amount);
		}

		Bid bid = new Bid(userBid, amount, now, currentPrice,
				currentWinnerProduct, product);
		product.setCurrentBid(bid);
		bidDao.save(bid);
		return bid;

	}

	@Transactional(readOnly = true)
	public BidBlock findBidsByUserId(Long userId, int startIndex, int count)
			throws InstanceNotFoundException {

		userProfileDao.find(userId);
		List<Bid> bids = bidDao.findBidsByUserId(userId, startIndex, count + 1);

		boolean existMoreBids = bids.size() == (count + 1);

		if (existMoreBids) {
			bids.remove(bids.size() - 1);

		}
		return new BidBlock(bids, existMoreBids);

	}

	@Transactional(readOnly = true)
	public Bid findBid(Long bidId) throws InstanceNotFoundException {

		return bidDao.find(bidId);
	}
}
