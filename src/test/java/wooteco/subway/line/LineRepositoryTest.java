package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineRepository;

@DisplayName("Line Repository")
@Sql("classpath:tableInit.sql")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class LineRepositoryTest {

    private final LineRepository lineRepository;

    public LineRepositoryTest(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @DisplayName("노선을 생성한다")
    @Test
    void save() {
        Line line = lineRepository.save(new Line("2호선", "bg-green-600"));

        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("bg-green-600");
    }

    @DisplayName("중복된 이름을 갖는 노선을 생성하면, 예외가 발생한다.")
    @Test
    void duplicateSaveValidate() {
        Line line = lineRepository.save(new Line("2호선", "bg-green-600"));

        assertThatThrownBy(() -> {
            lineRepository.save(line);
        }).isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("노선을 조회한다.")
    @Test
    void findById() {
        lineRepository.save(new Line("2호선", "bg-green-600"));
        lineRepository.save(new Line("3호선", "bg-orange-600"));

        Line line = lineRepository.findById(1L).get();
        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("bg-green-600");
    }

    @DisplayName("없는 노선을 조회하면, false를 반환한다.")
    @Test
    void notExistLineFindException() {
        assertThat(lineRepository.findById(1L).isPresent()).isEqualTo(false);
    }

    @DisplayName("노선 목록을 조회한다")
    @Test
    void findAll() {
        Line line2 = lineRepository.save(new Line("2호선", "bg-green-600"));
        Line line3 = lineRepository.save(new Line("3호선", "bg-orange-600"));
        Line line4 = lineRepository.save(new Line("4호선", "bg-skyBlue-600"));

        List<Line> lines = lineRepository.findAll();

        assertThat(lines).hasSize(3);
        assertThat(lines).containsExactly(line2, line3, line4);
    }

    @DisplayName("조회되는 노선 목록이 없으면, 비어있는 리스트를 반환한다.")
    @Test
    void showNotExistLinesException() {
        List<Line> lines = lineRepository.findAll();
        assertThat(lines).hasSize(0);
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void update() {
        Line line2 = new Line("2호선", "bg-green-600");
        Line newLine = new Line(1L, "3호선", "bg-orange-600");

        lineRepository.save(line2);
        assertThat(lineRepository.update(newLine)).isEqualTo(1);
    }

    @DisplayName("없는 노선을 수정하면, 0이 반환된다.")
    @Test
    void notUpdate() {
        Line newLine = new Line(1L, "3호선", "bg-orange-600");
        assertThat(lineRepository.update(newLine)).isEqualTo(0);
    }

    @DisplayName("노선을 삭제한다.")
    @Test
    void delete() {
        Line line2 = new Line("2호선", "bg-green-600");
        lineRepository.save(line2);

        assertThat(lineRepository.delete(1L)).isEqualTo(1);
        assertThat(lineRepository.findAll()).hasSize(0);
    }

    @DisplayName("없는 노선을 삭제하면, 0이 반환된다.")
    @Test
    void notDelete() {
        assertThat(lineRepository.delete(1L)).isEqualTo(0);
    }

}