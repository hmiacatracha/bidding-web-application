package es.udc.subasta.model.bid;

import java.util.List;

import es.udc.pojo.modelutil.dao.GenericDao;

public interface BidDao extends GenericDao<Bid, Long> {

	public List<Bid> findBidsByUserId(Long userId, int startIndex, int count);

}
