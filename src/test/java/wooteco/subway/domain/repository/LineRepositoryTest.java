package wooteco.subway.domain.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import wooteco.subway.domain.Line;
import wooteco.subway.utils.exception.NotFoundException;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
public class LineRepositoryTest {

    private static final long LINE_ID = 1L;

    @Autowired
    private DataSource dataSource;

    private LineRepository lineRepository;

    @BeforeEach
    void setUp() {
        lineRepository = new LineRepositoryImpl(dataSource);
    }

    @DisplayName("노선을 저장한다.")
    @Test
    void save() {
        Line line = new Line("분당선", "bg-red-600");
        Line saveLine = lineRepository.save(line);

        assertAll(
                () -> assertThat(saveLine.getId()).isNotNull(),
                () -> assertThat(saveLine).isEqualTo(line)
        );
    }

    @DisplayName("모든 노선을 조회한다.")
    @Test
    void findAll() {
        Line line1 = new Line("분당선", "bg-red-600");
        Line saveLine1 = lineRepository.save(line1);
        Line line2 = new Line("신분당선", "bg-red-600");
        Line saveLine2 = lineRepository.save(line2);

        List<Line> lines = lineRepository.findAll();

        assertAll(
                () -> assertThat(lines).hasSize(3),
                () -> assertThat(lines).contains(saveLine1, saveLine2)
        );
    }

    @DisplayName("식별자로 노선을 조회한다.")
    @Test
    void findById() {
        Line saveLine = lineRepository.save(new Line("분당선", "bg-red-600"));
        Line findLine = lineRepository.findById(saveLine.getId()).get();

        assertThat(findLine).isEqualTo(saveLine);
    }

    @DisplayName("이름으로 노선을 조회한다.")
    @Test
    void findByName() {
        Line saveLine = lineRepository.save(new Line("분당선", "bg-red-600"));
        Line findLine = lineRepository.findByName("분당선").get();

        assertThat(findLine).isEqualTo(saveLine);
    }

    @DisplayName("동일한 이름의 노선이 존재할 경우 true를 반환한다.")
    @Test
    void existByName() {
        lineRepository.save(new Line("분당선", "bg-red-600"));
        assertThat(lineRepository.existByName("분당선")).isTrue();
    }

    @DisplayName("동일한 이름의 노선이 존재하지않을 경우 false를 반환한다.")
    @Test
    void existByNameFailure() {
        assertThat(lineRepository.existByName("분당선")).isFalse();
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void update() {
        Line saveLine = lineRepository.save(new Line("분당선", "bg-red-600"));
        Line updatedLine = new Line("신분당선", "bg-yellow-600");
        lineRepository.update(saveLine.getId(), updatedLine);
        Line findUpdateLine = lineRepository.findById(saveLine.getId()).get();

        assertAll(
                () -> assertThat(findUpdateLine.getName()).isEqualTo(updatedLine.getName()),
                () -> assertThat(findUpdateLine.getColor()).isEqualTo(updatedLine.getColor())
        );
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteById() {
        Line line = lineRepository.findById(LINE_ID)
                .orElseThrow(() -> new NotFoundException("[ERROR] 식별자에 해당하는 노선을 찾지 못했습니다."));
        lineRepository.delete(line);

        assertThat(lineRepository.findAll()).isEmpty();
    }
}
