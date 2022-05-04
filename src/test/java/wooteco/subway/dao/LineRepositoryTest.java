package wooteco.subway.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.dto.LineUpdateDto;
import wooteco.subway.domain.Line;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
public class LineRepositoryTest {

    @Autowired
    LineRepository lineRepository;

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
                () -> assertThat(lines).hasSize(2),
                () -> assertThat(lines).containsExactly(saveLine1, saveLine2)
        );
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void findById() {
        Line saveLine = lineRepository.save(new Line("분당선", "bg-red-600"));
        Line findLine = lineRepository.findById(saveLine.getId());

        assertThat(findLine).isEqualTo(saveLine);
    }

    @DisplayName("이름으로 노선을 조회한다.")
    @Test
    void findByName() {
        Line saveLine = lineRepository.save(new Line("분당선", "bg-red-600"));
        Line findLine = lineRepository.findByName("분당선");

        assertThat(findLine).isEqualTo(saveLine);
    }

    @DisplayName("이름으로 노선을 조회시 없을 경우 null을 반환한다.")
    @Test
    void findByNameNull() {
        Line findLine = lineRepository.findByName("분당선");

        assertThat(findLine).isNull();
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void update() {
        Line saveLine = lineRepository.save(new Line("분당선", "bg-red-600"));
        LineUpdateDto lineUpdateDto = new LineUpdateDto(saveLine.getId(), "신분당선", "bg-yellow-600");
        lineRepository.update(lineUpdateDto);
        Line findUpdateLine = lineRepository.findById(saveLine.getId());

        assertAll(
                () -> assertThat(findUpdateLine.getName()).isEqualTo("신분당선"),
                () -> assertThat(findUpdateLine.getColor()).isEqualTo("bg-yellow-600")
        );
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void deleteById() {
        Line saveLine = lineRepository.save(new Line("분당선", "bg-red-600"));
        lineRepository.deleteById(saveLine.getId());

        assertThat(lineRepository.findAll()).isEmpty();
    }
}
