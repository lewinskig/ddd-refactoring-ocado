package tools;

import demands.DemandReadModel;
import demands.DemandReadModelDao;
import entities.DemandEntity;
import entities.FormEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import enums.DeliverySchema;
import external.CurrentStock;
import shortage.prediction.Demands;
import shortage.prediction.LevelOnDeliveryPolicy;
import shortage.prediction.Productions;
import shortage.prediction.ShortageCalculator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ShortageFinderACL {

    /**
     * Production at day of expected delivery is quite complex:
     * We are able to produce and deliver just in time at same day
     * but depending on delivery time or scheme of multiple deliveries,
     * we need to plan properly to have right amount of parts ready before delivery time.
     * <p/>
     * Typical schemas are:
     * <li>Delivery at prod day start</li>
     * <li>Delivery till prod day end</li>
     * <li>Delivery during specified shift</li>
     * <li>Multiple deliveries at specified times</li>
     * Schema changes the way how we calculate shortages.
     * Pick of schema depends on customer demand on daily basis and for each product differently.
     * Some customers includes that information in callof document,
     * other stick to single schema per product. By manual adjustments of demand,
     * customer always specifies desired delivery schema
     * (increase amount in scheduled transport or organize extra transport at given time)
     * <p>
     * TODO algorithm is finding wrong shortages, when more productions is planned in a single day
     */
    public static List<ShortageEntity> findShortages(LocalDate today, int daysAhead, CurrentStock stock,
                                                     List<ProductionEntity> productions, List<DemandEntity> demands) {

        List<ShortageEntity> oldModel = ShortageFinder.findShortages(today, daysAhead, stock, productions, demands);

        // if (calcualte with new model)
        ShortageCalculatorFactory factory = new ShortageCalculatorFactory(today, daysAhead, stock, productions, demands);
        ShortageCalculator calculator = factory.create();

        List<ShortageEntity> newModel = calculator.findShortages();

        // compare calculations
        return oldModel;
    }

    private ShortageFinderACL() {
    }


    private static class ShortageCalculatorFactory {
        private LocalDate today;
        private int daysAhead;
        private CurrentStock stock;
        private ProductionsProvider productions;
        private DemandsProvider demands;

        public ShortageCalculatorFactory(LocalDate today, int daysAhead, CurrentStock stock, List<ProductionEntity> productions, List<DemandEntity> demands) {
            this.today = today;
            this.daysAhead = daysAhead;
            this.stock = stock;
            this.productions = new ProductionsProvider(productions);
            this.demands = new DemandsProvider(demands);
        }

        public ShortageCalculator create() {
            List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                    .limit(daysAhead)
                    .collect(toList());

            Productions outputs = productions.createProductions();
            Demands demandsPerDay = demands.createDemands();

            return new ShortageCalculator(stock, dates, outputs, demandsPerDay);
        }

        public static class ProductionsProvider {

            private List<ProductionEntity> productions;

            public ProductionsProvider(List<ProductionEntity> productions) {
                this.productions = productions;
            }

            public Productions createProductions() {
                String productRefNo = productions.stream()
                        .map(ProductionEntity::getForm)
                        .map(FormEntity::getRefNo)
                        .findFirst()
                        .orElse(null);
                Map<LocalDate, Long> outputs = Collections.unmodifiableMap(productions.stream().collect(Collectors.toMap(
                        production -> production.getStart().toLocalDate(),
                        production -> production.getOutput(),
                        (level1, level2) -> level1 + level2
                )));

                return new Productions(productRefNo, outputs);
            }
        }
    }

    private static class DemandsProvider {
        private DemandReadModelDao demands;
        private final Map<DeliverySchema, LevelOnDeliveryPolicy> mapping;

        public DemandsProvider(List<DemandEntity> demands) {
            mapping = init();
        }


        public Demands createDemands() {
            List<DemandReadModel> entities = this.demands.findFrom(null, null);

            Map<LocalDate, Demands.Demand> demands = entities
                    .stream().collect(Collectors.toMap(
                            readModel -> readModel.getDay(),
                            readModel -> new Demands.Demand(
                                    readModel.getLevel(),
                                    pickVariant(readModel.getDeliverySchema()))
            ));

            return new Demands(demands);
        }


        private LevelOnDeliveryPolicy pickVariant(DeliverySchema deliverySchema) {
            return mapping.getOrDefault(deliverySchema, (level, demand1, produced) -> {
                throw new NotImplementedException();
            });
        }

        private Map<DeliverySchema, LevelOnDeliveryPolicy> init() {
            Map<DeliverySchema, LevelOnDeliveryPolicy> mapping = new HashMap<>();
            mapping.put(DeliverySchema.atDayStart, LevelOnDeliveryPolicy.AT_DAY_START);
            mapping.put(DeliverySchema.tillEndOfDay, LevelOnDeliveryPolicy.TILL_END_OF_DAY);
            return Collections.unmodifiableMap(mapping);
        }

    }

}
