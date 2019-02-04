package shortage.prediction;

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
        private final LevelOnDeliveryPolicy policy;
        private final long level;

        public Demand(long level, LevelOnDeliveryPolicy policy) {
            this.level = level;
            this.policy = policy;
        }

        public long getLevel() {
            return level;
        }

        public long calculateLevelOnDelivery(long level, long produced) {
            return policy.calculateLevelOnDelivery(level, this, produced);
        }
    }
}
