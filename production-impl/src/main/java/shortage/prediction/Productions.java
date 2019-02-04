package shortage.prediction;

import java.time.LocalDate;
import java.util.Map;

public class Productions {

    private final Map<LocalDate, Long> outputs;
    private final String productRefNo;

    public Productions(String productRefNo, Map<LocalDate, Long> outputs) {
        this.outputs = outputs;
        this.productRefNo = productRefNo;
    }

    public long getOutput(LocalDate day) {
        return outputs.getOrDefault(day, 0L);
    }

    public String getProductRefNo() {
        return productRefNo;
    }
}
