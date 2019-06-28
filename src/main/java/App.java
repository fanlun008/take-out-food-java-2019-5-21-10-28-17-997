import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {
    private ItemRepository itemRepository;
    private SalesPromotionRepository salesPromotionRepository;

    public App(ItemRepository itemRepository, SalesPromotionRepository salesPromotionRepository) {
        this.itemRepository = itemRepository;
        this.salesPromotionRepository = salesPromotionRepository;
    }

    public String bestCharge(List<String> inputs) {
        //TODO: write code here
        Map<Item, Integer> order = new LinkedHashMap<>();

        inputs.forEach((input) -> {
            String[] item_count_str = input.split("x");
            String food_id = item_count_str[0].trim();
            Integer food_count = Integer.parseInt(item_count_str[1].trim());
            order.put(itemRepository.findOne(food_id), food_count);
        });

        Map<String, Object> stringObjectMap = calcSolu(order);  //返回了 最佳优惠的 相关信息
//        System.out.println(stringObjectMap.toString());

        String result_str = result_print(order, stringObjectMap);
        System.out.print(result_str);
        return result_str;
    }

    public String result_print(Map<Item, Integer> order, Map<String, Object> stringObjectMap) {
        SalesPromotion salesPromotion = (SalesPromotion) stringObjectMap.get("bestPromotion");
        double bestSave = (double) stringObjectMap.get("bestSave");
        double totalPrice = (double) stringObjectMap.get("totalPrice");

        StringBuffer result = new StringBuffer("============= 订餐明细 =============\n");
        for (Map.Entry<Item, Integer> entry : order.entrySet()) {
            String temp = String.format("%1$s x %2$d = %3$d元\n", entry.getKey().getName(), entry.getValue(), (int)entry.getKey().getPrice() * entry.getValue());
            result.append(temp);
        }
        if (bestSave != 0) {
            result.append("-----------------------------------\n");
            result.append("使用优惠:\n");
            result.append(salesPromotion.getDisplayName());

            if (!salesPromotion.getRelatedItems().isEmpty()) {
                result.append("(");
                List<String> item_name = salesPromotion.getRelatedItems().stream()
                        .map(e -> itemRepository.findOne(e).getName())
                        .collect(Collectors.toList());
                result.append(String.join("，", item_name));
                result.append(")");
            }
            result.append(String.format("，省%d元\n", (int)bestSave));
        }

        result.append("-----------------------------------\n");
        result.append(String.format("总计：%d元\n", (int)totalPrice));
        result.append("===================================");

        return result.toString();
    }

    public Map<String, Object> calcSolu(Map order) {
        Class<?> refClass = this.getClass();
        Constructor declaredConstructor = null;
        Object instance = null;
        try {
            declaredConstructor = refClass.getDeclaredConstructor(ItemRepository.class, SalesPromotionRepository.class);
            instance = declaredConstructor.newInstance(itemRepository, salesPromotionRepository);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        List<SalesPromotion> all_Promotion = salesPromotionRepository.findAll();

        Map<String, Object> best_promap = new HashMap<>();
        SalesPromotion bestPromotion = null;
        Double finalSave = -1d;
        Double totalPrice = -1d;

        if (!all_Promotion.isEmpty()) {
            for (SalesPromotion promotion : all_Promotion) {
                Method method = null;
                try {
                    method = refClass.getDeclaredMethod("SOLU_" + promotion.getType().replace("%", ""), Map.class);
                    Double[] discount = (Double[]) method.invoke(instance, order);

                    if (discount[1] > finalSave) {
                        finalSave = discount[1];
                        totalPrice = discount[0];
                        bestPromotion = promotion;
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            best_promap.put("bestSave", finalSave);  //最终节省的钱数
            best_promap.put("totalPrice", totalPrice);  //减去优惠后的总钱数
            best_promap.put("bestPromotion", bestPromotion);  //最佳优惠方案实例
            return best_promap;
        } else {
            best_promap.clear();
            return best_promap;
        }
    }

    public Double[] SOLU_BUY_30_SAVE_6_YUAN(Map<Item, Integer> order) {
//        System.out.println("SOLU_BUY_30_SAVE_6_YUAN");
        double totalPrice = 0d, saving_money = 0d;
        for (Map.Entry<Item, Integer> entry : order.entrySet()) {
            totalPrice += entry.getKey().getPrice() * entry.getValue();
        }
        if (totalPrice >= 30) {
            totalPrice -= 6;
            saving_money = 6d;
        }
        return new Double[]{totalPrice, saving_money};
    }

    public Double[] SOLU_50_DISCOUNT_ON_SPECIFIED_ITEMS(Map<Item, Integer> order) {
//        System.out.println("SOLU_50%_DISCOUNT_ON_SPECIFIED_ITEMS");
        double totalPrice = 0d, saving_money = 0d;
        List<String> relatedItems = salesPromotionRepository.findOne("50%_DISCOUNT_ON_SPECIFIED_ITEMS").getRelatedItems();

        for (Map.Entry<Item, Integer> entry : order.entrySet()) {
            if (relatedItems.contains(entry.getKey().getId())) {
                totalPrice += entry.getKey().getPrice() * entry.getValue() / 2;
                saving_money += entry.getKey().getPrice() * entry.getValue() / 2;
            } else {
                totalPrice += entry.getKey().getPrice() * entry.getValue();
            }
        }
        return new Double[]{totalPrice, saving_money};
    }
}
