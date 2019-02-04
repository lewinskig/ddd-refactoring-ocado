package shortage.prediction;

import enums.DeliverySchema;

import java.time.LocalDate;
import java.util.Map;

public class Demands {

    private final Map<LocalDate, Demand> demandsPerDay;

    public Demands(Map<LocalDate, Demand> demandsPerDay) {
        this.demandsPerDay = demandsPerDay;
    }

    public Demand get(LocalDate day) {
        return demandsPerDay.getOrDefault(day, null);
    }

    public static class Demand {
        private final DeliverySchema deliverySchema;
        private final long level;

        public Demand(long level, DeliverySchema deliverySchema) {
            this.level = level;
            this.deliverySchema = deliverySchema;
        }

        public DeliverySchema getDeliverySchema() {
            return deliverySchema;
        }

        public long getLevel() {
            return level;
        }
    }
}
