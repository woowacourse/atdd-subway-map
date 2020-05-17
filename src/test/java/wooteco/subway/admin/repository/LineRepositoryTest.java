package wooteco.subway.admin.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import wooteco.subway.admin.domain.Line;

import java.time.LocalTime;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
public class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @BeforeEach
    void setUp() {
        lineRepository.save(
                new Line("1호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-yellow-700"));
        lineRepository.save(
                new Line("2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-yellow-700"));
        lineRepository.save(
                new Line("3호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-yellow-700"));
    }

    @DisplayName("모든 노선을 가져올 수 있는지 테스트")
    @Test
    void findAllTest() {
        assertThat(lineRepository.findAll().size()).isEqualTo(3);
    }

    @DisplayName("노선 이름으로 노선을 가져올 수 있는지 테스트")
    @Test
    void findByTitleTest() {
        Line line1 = lineRepository.findByTitle("1호선").orElseThrow(NoSuchElementException::new);
        Line line2 = lineRepository.findByTitle("2호선").orElseThrow(NoSuchElementException::new);
        Line line3 = lineRepository.findByTitle("3호선").orElseThrow(NoSuchElementException::new);

        assertThat(line1.getId()).isEqualTo(1L);
        assertThat(line2.getId()).isEqualTo(2L);
        assertThat(line3.getId()).isEqualTo(3L);
    }
}
