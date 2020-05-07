package wooteco.subway.admin.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import wooteco.subway.admin.domain.Line;

@DataJdbcTest
class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @Test
    void save() {
        Line line = new Line("1호선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);

        Line persistLine = lineRepository.save(line);

        assertThat(persistLine.getId()).isNotNull();
    }

    @Test
    void findByName() {
        Line line = new Line("1호선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);

        lineRepository.save(line);

        assertThat(lineRepository.findByTitle("1호선").get().getTitle()).isEqualTo("1호선");
    }

    @Test
    void deleteByName() {
        Line line = new Line("1호선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10);

        lineRepository.save(line);
        lineRepository.delete(line);

        assertThat(lineRepository.findByTitle("1호선").isPresent()).isFalse();
    }
}