import java.util.List;
import java.util.stream.Collectors;

public class SalesPromotionRepositoryTestImpl implements SalesPromotionRepository {
    @Override
    public List<SalesPromotion> findAll() {
        return TestData.ALL_SALES_PROMOTIONS;
    }

    @Override
    public SalesPromotion findOne(String type) {
        if (type ==null || type.equals("")) {
            return null;
        }
        List<SalesPromotion> promotions = TestData.ALL_SALES_PROMOTIONS.stream()
                .filter( p -> p.getType().equals(type))
                .collect(Collectors.toList());
        if (!promotions.isEmpty()){
            return promotions.get(0);
        }
        return null;
    }


}
