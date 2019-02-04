package demands;

import enums.DeliverySchema;

import java.time.LocalDate;

public class DemandReadModel {
    private final LocalDate day;
    private final long level;
    private final DeliverySchema deliverySchema;

    public DemandReadModel(LocalDate day, long level, DeliverySchema deliverySchema) {
        this.day = day;
        this.level = level;
        this.deliverySchema = deliverySchema;
    }

    public LocalDate getDay() {
        return day;
    }

    public long getLevel() {
        return level;
    }

    public DeliverySchema getDeliverySchema() {
        return deliverySchema;
    }
}
