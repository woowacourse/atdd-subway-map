package wooteco.subway.admin.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import wooteco.subway.admin.domain.Line;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
public class LineRepositoryTest {
    @Autowired
    private LineRepository lineRepository;

    @DisplayName("노선 추가 테스트")
    @Test
    void save() {
        Line line = new Line("2호선", LocalTime.of(8, 00), LocalTime.of(8, 00), 10, "0");

        Line persistLine = lineRepository.save(line);

        assertThat(persistLine.getId()).isNotNull();
    }
}
