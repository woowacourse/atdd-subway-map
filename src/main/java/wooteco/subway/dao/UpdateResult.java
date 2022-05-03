package wooteco.subway.dao;

import java.util.function.Supplier;

public class UpdateResult {

    private final int effectedRowCount;

    public UpdateResult(int effectedRowCount) {
        this.effectedRowCount = effectedRowCount;
    }

    public void throwOnNonEffected(Supplier<RuntimeException> exceptionSupplier) {
        if (effectedRowCount == 0) {
            throw exceptionSupplier.get();
        }
    }
}
