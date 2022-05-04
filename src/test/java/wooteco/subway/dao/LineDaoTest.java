package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wooteco.subway.domain.Line;

class LineDaoTest {

    @BeforeEach
    void setUp() {
        LineDao.deleteAll();
    }

    @Test
    @DisplayName("노선을 저장한다.")
    void save() {
        Line line = LineDao.save(new Line("2호선", "bg-red-600"));
        assertThat(line.getName()).isEqualTo("2호선");
    }

    @Test
    @DisplayName("노선을 id로 조회한다.")
    void findById() {
        Line line = LineDao.save(new Line("2호선", "bg-red-600"));
        Line findLine = LineDao.findById(line.getId());
        assertThat(findLine.getName()).isEqualTo("2호선");
    }

    @ParameterizedTest(name = "name : {0}, color : {1}")
    @CsvSource({"1호선, black", "1호선, blue", "2호선, black"})
    void duplicateNameAndColor(String name, String color) {
        LineDao.save(new Line(name, color));
        assertThatIllegalArgumentException()
            .isThrownBy(() -> LineDao.save(new Line("1호선", "black")))
            .withMessage("노선의 이름은 중복될 수 없습니다.");
    }

    @Test
    @DisplayName("노선을 저장할 때 id가 1씩 증가한다.")
    void increaseId() {
        Line line1 = LineDao.save(new Line("2호선", "bg-red-600"));
        Line line2 = LineDao.save(new Line("3호선", "bg-blue-600"));
        assertThat(line2.getId() - line1.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("노선 목록을 조회한다.")
    void showLineList() {
        LineDao.save(new Line("1호선", "blue"));
        LineDao.save(new Line("2호선", "green"));
        List<Line> values = LineDao.findAll();
        assertThat(values).hasSize(2);
    }

    @Test
    @DisplayName("없는 노선 목록을 조회할 경우 예외를 발생시킨다.")
    void notFindById() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> LineDao.findById(1L))
            .withMessage("조회하려는 id가 없습니다.");
    }

    @Test
    @DisplayName("id로 노선을 수정한다.")
    void modifyById() {
        Line savedLine = LineDao.save(new Line("1호선", "blue"));
        LineDao.modify(savedLine.getId(),"3호선","green");

        Line modifiedLine = LineDao.findById(savedLine.getId());
        assertThat(modifiedLine.getName()).isEqualTo("3호선");
    }

    @Test
    @DisplayName("id로 노선을 삭제한다.")
    void deleteById() {
        Line savedLine = LineDao.save(new Line("1호선", "blue"));
        LineDao.deleteById(savedLine.getId());
        assertThat(LineDao.findAll()).hasSize(0);
    }
}
