package shortage.prediction;

import entities.ShortageEntity;
import external.CurrentStock;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class ShortageCalculator {
    private CurrentStock stock;
    private List<LocalDate> dates;
    private Productions outputs;
    private Demands demandsPerDay;

    public ShortageCalculator(CurrentStock stock, List<LocalDate> dates, Productions outputs, Demands demandsPerDay) {
        this.stock = stock;
        this.dates = dates;
        this.outputs = outputs;
        this.demandsPerDay = demandsPerDay;
    }

    public List<ShortageEntity> findShortages() {
        long level = stock.getLevel();

        List<ShortageEntity> gap = new LinkedList<>();
        for (LocalDate day : dates) {
            Demands.Demand demand = demandsPerDay.get(day);
            if (demand == null) {
                level += outputs.getOutput(day);
                continue;
            }
            long produced = outputs.getOutput(day);

            long levelOnDelivery = demand.calculateLevelOnDelivery(level, produced);

            if (!(levelOnDelivery >= 0)) {
                ShortageEntity entity = new ShortageEntity();
                entity.setRefNo(outputs.getProductRefNo());
                entity.setFound(LocalDate.now());
                entity.setMissing(levelOnDelivery * -1L);
                entity.setAtDay(day);
                gap.add(entity);
            }
            long endOfDayLevel = level + produced - demand.getLevel();
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return gap;
    }
}
