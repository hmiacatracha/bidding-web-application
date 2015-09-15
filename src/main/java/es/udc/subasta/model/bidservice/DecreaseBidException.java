package es.udc.subasta.model.bidservice;

@SuppressWarnings("serial")
public class DecreaseBidException extends Exception {
    private Long bidId;

    public DecreaseBidException(Long bidId) {
        super("Is not possible to decrease the bid, please increase this bid: " + bidId);
        this.bidId = bidId;
    }

    public Long getBidId() {
        return bidId;
    }

}
