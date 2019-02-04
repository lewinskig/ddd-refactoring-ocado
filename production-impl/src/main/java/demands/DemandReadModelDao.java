package demands;

import dao.DemandDao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DemandReadModelDao {

    DemandDao demands;

    public List<DemandReadModel> findFrom(LocalDateTime localDateTime, String productRefNo) {
        return this.demands.findFrom(localDateTime, productRefNo)
                .stream().map(demand -> new DemandReadModel(
                        demand.getDay(),
                        Util.getLevel(demand),
                        Util.getDeliverySchema(demand)
                )).collect(Collectors.toList());
    }
}
