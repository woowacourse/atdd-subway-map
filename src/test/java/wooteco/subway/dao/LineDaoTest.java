package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
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
}
