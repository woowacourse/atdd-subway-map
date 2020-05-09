package wooteco.subway.admin.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import wooteco.subway.admin.domain.Line;

@DataJdbcTest
class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @DisplayName("저장한 노선을 모두 불러오는지 테스트")
    @Test
    void findAll() {
        List<Line> lines = new ArrayList<>();
        lines.add(new Line("신분당선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10));
        lines.add(new Line("1호선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10));
        lines.add(new Line("2호선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10));
        lineRepository.saveAll(lines);
        assertThatCode(() -> lineRepository.findAll())
            .doesNotThrowAnyException();
    }
    
    @DisplayName("중복된 노선 이름이 있는지 테스트")
    @Test
    void existsByNameTest() {
        List<Line> lines = new ArrayList<>();
        lines.add(new Line("신분당선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10));
        lines.add(new Line("1호선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10));
        lines.add(new Line("2호선", LocalTime.of(5, 30), LocalTime.of(23, 30), 10));
        lineRepository.saveAll(lines);

        assertThat(lineRepository.existsByName("신분당선")).isTrue();
        assertThat(lineRepository.existsByName("3호선")).isFalse();
    }
}