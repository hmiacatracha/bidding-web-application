package es.udc.subasta.model.bid;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import es.udc.pojo.modelutil.dao.GenericDaoHibernate;

@Repository("BidDao")
public class BidDaoHibernate extends GenericDaoHibernate<Bid, Long> implements
		BidDao {

	/*
	 * Este metodo no comprueba si el usuario existe, necesario comprobarlo en
	 * el servicio
	 * 
	 * @see
	 * es.udc.subasta.model.userprofile.UserProfileDao#findBidsByUserId(java
	 * .lang.Long)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Bid> findBidsByUserId(Long userId, int startIndex, int count) {

		String findQuery = "SELECT u FROM Bid u WHERE u.user.userProfileId = :userId "
				+ "ORDER BY u.date DESC";
		Query query = getSession().createQuery(findQuery);
		query.setParameter("userId", userId);
		query.setFirstResult(startIndex);
		query.setMaxResults(count);
		return query.list();
	}


}
