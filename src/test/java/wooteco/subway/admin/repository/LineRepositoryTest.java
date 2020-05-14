package wooteco.subway.admin.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import wooteco.subway.admin.domain.Line;

import java.sql.PreparedStatement;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJdbcTest
class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Test
    void uniqueLineName() {
        Line line = new Line("2호선", null, LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
        Line anotherLine = new Line("2호선", null, LocalTime.of(05, 30), LocalTime.of(22, 30), 5);

        lineRepository.save(line);
        assertThatThrownBy(() -> {
            lineRepository.save(anotherLine);
        }).isInstanceOf(DbActionExecutionException.class)
                .hasCauseInstanceOf(DuplicateKeyException.class);
    }
}