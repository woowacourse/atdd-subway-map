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
        Line line = new Line("2호선", LocalTime.now(), LocalTime.now(), 5,
            "bg-gray-600");
        Line persistLine = lineRepository.save(line);
        assertThat(persistLine.getId()).isNotNull();
    }
}