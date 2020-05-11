package wooteco.subway.admin.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Test
    void findByName() {
        lineRepository.save(new Line("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "5"));
        assertThat(lineRepository.findByName("2호선").isPresent()).isTrue();
    }

    @Test
    void countByStationId() {
        Line line = new Line("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "5");

        line.addLineStation(new LineStation(null, 1L, 10, 10));
        lineRepository.save(line);
        assertThat(lineRepository.countByStationId(1L)).isEqualTo(1);
    }
}