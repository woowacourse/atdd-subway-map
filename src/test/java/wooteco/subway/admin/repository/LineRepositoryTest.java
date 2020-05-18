package wooteco.subway.admin.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.admin.domain.Line;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@Sql("/truncate.sql")
class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Test
    void findAll() {
        Line line2 = new Line(1L, "2호선", null, LocalTime.of(2, 00), LocalTime.of(23, 00), 10);
        Line line3 = new Line(2L, "3호선", null, LocalTime.of(3, 00), LocalTime.of(23, 00), 5);
        lineRepository.saveAll(Arrays.asList(line2, line3));

        List<Line> lines = lineRepository.findAll();

        assertThat(lines.get(0).getName()).isEqualTo("2호선");
        assertThat(lines.get(1).getName()).isEqualTo("3호선");
    }
}