package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.dao.DuplicateKeyException;
import wooteco.subway.domain.Line;

@JdbcTest
class LineDaoTest {

    @Autowired
    private DataSource dataSource;

    private LineDao lineDao;

    @BeforeEach
    void setUp() {
        lineDao = new LineDao(dataSource);
    }

    @DisplayName("노선 저장 기능을 테스트한다.")
    @Test
    void saveLine() {
        Line line = new Line("2호선", "초록색");

        Line persistLine = lineDao.save(line);

        assertThat(persistLine.getId()).isNotNull();
        assertThat(persistLine.getName()).isEqualTo("2호선");
        assertThat(persistLine.getColor()).isEqualTo("초록색");
    }

    @DisplayName("중복된 이름이나 색상의 노선을 저장할 경우 예외가 발생한다.")
    @ParameterizedTest
    @CsvSource(value = {"2호선,검은색", "성수지선,초록색"})
    void saveDuplicateNameLine(String name, String color) {
        Line line = new Line("2호선", "초록색");
        lineDao.save(line);
        Line duplicateLine = new Line(name, color);

        assertThatThrownBy(() -> lineDao.save(duplicateLine))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("전체 노선의 개수가 맞는지 확인한다.")
    @Test
    void find_All_Line() {
        Line lineTwo = new Line("2호선", "초록색");
        Line lineEight = new Line("8호선", "분홍색");
        lineDao.save(lineTwo);
        lineDao.save(lineEight);

        assertThat(lineDao.findAll().size()).isEqualTo(2);
    }

    @DisplayName("특정 id를 가지는 노선을 조회한다.")
    @Test
    void findById() {
        Line line = new Line("2호선", "초록색");
        Long id = lineDao.save(line).getId();

        Line actual = lineDao.findById(id);
        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo("2호선");
        assertThat(actual.getColor()).isEqualTo("초록색");
    }

    @DisplayName("특정 id를 가지는 라인의 이름과 색을 변경한다.")
    @Test
    void updateLineById() {
        Line line = new Line("2호선", "초록색");
        Long id = lineDao.save(line).getId();

        Line updateLine = new Line(id, "8호선", "분홍색");
        lineDao.updateById(updateLine);

        Line actual = lineDao.findById(id);
        assertThat(actual.getName()).isEqualTo("8호선");
        assertThat(actual.getColor()).isEqualTo("분홍색");
    }

    @DisplayName("특정 id를 가지는 노선을 삭제한다.")
    @Test
    void deleteById() {
        Line line = new Line("2호선", "초록색");
        Long id = lineDao.save(line).getId();

        lineDao.deleteById(id);

        assertThat(lineDao.findAll().size()).isEqualTo(0);
    }
}
