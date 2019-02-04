package shortage.prediction;

public interface LevelOnDeliveryPolicy {

    LevelOnDeliveryPolicy AT_DAY_START = (level, demand, produced) -> level - demand.getLevel();
    LevelOnDeliveryPolicy TILL_END_OF_DAY = (level, demand, produced) -> level - demand.getLevel() + produced;

    long calculateLevelOnDelivery(long level, Demands.Demand demand, long produced);
}
