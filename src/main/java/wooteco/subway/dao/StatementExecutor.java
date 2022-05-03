package wooteco.subway.dao;

import java.util.function.Supplier;
import org.springframework.dao.DataAccessException;

public class StatementExecutor<T> {

    private final Supplier<T> statement;

    public StatementExecutor(Supplier<T> statement) {
        this.statement = statement;
    }

    public T executeOrThrow(Supplier<RuntimeException> exceptionSupplier) {
        try {
            return statement.get();
        } catch (DataAccessException e) {
            throw exceptionSupplier.get();
        }
    }

    public UpdateResult update() {
        return new UpdateResult((int) statement.get());
    }

    public UpdateResult updateOrThrow(Supplier<RuntimeException> exceptionSupplier) {
        try {
            return update();
        } catch (DataAccessException e) {
            throw exceptionSupplier.get();
        }
    }
}
