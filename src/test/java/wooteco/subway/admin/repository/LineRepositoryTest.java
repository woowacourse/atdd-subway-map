package wooteco.subway.admin.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import wooteco.subway.admin.domain.Line;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;

@DataJdbcTest
class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @DisplayName("displayName")
    @Test
    void findAll() {
        List<Line> lines = new ArrayList<>();
        lines.add(new Line("역1", LocalTime.of(5, 30), LocalTime.of(23, 30), 10));
        lines.add(new Line("역2", LocalTime.of(5, 30), LocalTime.of(23, 30), 10));
        lines.add(new Line("역3", LocalTime.of(5, 30), LocalTime.of(23, 30), 10));
        lineRepository.saveAll(lines);
        assertThatCode(() -> {
            List<Line> all = lineRepository.findAll();

        }).doesNotThrowAnyException();
    }
}