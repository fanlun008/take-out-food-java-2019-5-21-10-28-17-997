import java.util.List;

public interface SalesPromotionRepository {
    List<SalesPromotion> findAll();

    SalesPromotion findOne(String type);
}
