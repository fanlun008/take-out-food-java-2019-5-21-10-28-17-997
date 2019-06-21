import java.util.List;
import java.util.stream.Collectors;

public class ItemRepositoryTestImpl implements ItemRepository {
    @Override
    public List<Item> findAll() {
        return TestData.ALL_ITEMS;
    }

    @Override
    public Item findOne(String id) {
        if (id == null || id.equals("")) {
            return null;
        }
        List<Item> items = TestData.ALL_ITEMS.stream()
                .filter((p) -> p.getId().equals(id))
                .collect(Collectors.toList());
        if (items.get(0) != null) {
            return items.get(0);
        }
        return null;
    }
}
