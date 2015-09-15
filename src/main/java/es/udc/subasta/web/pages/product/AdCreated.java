package es.udc.subasta.web.pages.product;

public class AdCreated {
	
	private Long adId;
	
	public Long getAdId() {
		return adId;
	}

	public void setAdId(Long adId) {
		this.adId = adId;
	}

	Long onPassivate() {
		return adId;
	}
	
	void onActivate(Long adId) {
		this.adId = adId;
	}
	
}
