package es.udc.subasta.model.bidservice;

@SuppressWarnings("serial")
public class OwnedProductException extends Exception {
    private Long userId;

    public OwnedProductException(Long userId) {
        super("You can't bid this product, you are its owner: " + userId);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

}
