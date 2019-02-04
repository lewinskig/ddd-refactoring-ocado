package shortage.prediction;

import entities.DemandEntity;
import enums.DeliverySchema;
import tools.Util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demands {

    private final Map<LocalDate, DemandEntity> demandsPerDay = new HashMap<>();

    public Demands(List<DemandEntity> demands) {
        for (DemandEntity demand1 : demands) {
            demandsPerDay.put(demand1.getDay(), demand1);
        }
    }

    public Demand get(LocalDate day) {
        DemandEntity demand = demandsPerDay.get(day);
        if (demand == null) {
            return null;
        }
        return new Demand(Util.getLevel(demand), Util.getDeliverySchema(demand));
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
