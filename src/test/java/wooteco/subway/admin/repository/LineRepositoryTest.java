package wooteco.subway.admin.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import wooteco.subway.admin.domain.Line;

@DataJdbcTest
public class LineRepositoryTest {

    @Autowired
    private LineRepository lineRepository;

    @Test
    void save() {
        //given
        Line line = new Line("신분당선", LocalTime.of(5, 30),
            LocalTime.of(23, 30),
            10, "bg-orange-100");
        //when
        Line persistLine = lineRepository.save(line);
        //then
        assertThat(persistLine.getId()).isNotNull();
    }

    @Test
    void findAll() {
        Line line = new Line("신분당선", LocalTime.of(5, 30),
            LocalTime.of(23, 30),
            10, "bg-orange-100");

        Line persistLine = lineRepository.save(line);

        List<Line> lines = lineRepository.findAll();
        assertThat(lines.size()).isEqualTo(1);
    }
}
