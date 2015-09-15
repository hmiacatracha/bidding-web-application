package es.udc.subasta.web.pages.bid;

public class BidCreated {
	
	private Long bidId;
	
	public Long getBidId() {
		return bidId;
	}

	public void setBidId(Long bidId) {
		this.bidId = bidId;
	}

	Long onPassivate() {
		return bidId;
	}
	
	void onActivate(Long bidId) {
		this.bidId = bidId;
	}
	
}
