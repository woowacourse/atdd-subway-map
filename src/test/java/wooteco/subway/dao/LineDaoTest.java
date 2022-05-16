package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

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
        Line expected = new Line(persistLine.getId(), line.getName(), line.getColor());

        assertEquals(expected, persistLine);
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
    void findAllLine() {
        Line lineTwo = new Line("2호선", "초록색");
        Line lineEight = new Line("8호선", "분홍색");
        lineDao.save(lineTwo);
        lineDao.save(lineEight);

        assertThat(lineDao.findAll()).hasSize(2);
    }

    @DisplayName("특정 id를 가지는 노선을 조회한다.")
    @Test
    void findById() {
        Line line = new Line("2호선", "초록색");
        Long id = lineDao.save(line).getId();

        Line actual = lineDao.findById(id)
                .orElseGet(() -> fail("존재하지 않는 노선입니다."));
        Line expected = new Line(id, line.getName(), line.getColor());

        assertEquals(expected, actual);
    }

    @DisplayName("특정 id를 가지는 노선의 이름과 색을 변경한다.")
    @Test
    void updateById() {
        Line line = new Line("2호선", "초록색");
        Long id = lineDao.save(line).getId();
        Line updateLine = new Line("8호선", "분홍색");
        lineDao.updateById(id, updateLine);

        Line actual = lineDao.findById(id)
                .orElseGet(() -> fail("존재하지 않는 노선입니다."));
        Line expected = new Line(id, updateLine.getName(), updateLine.getColor());

        assertEquals(expected, actual);
    }

    @DisplayName("이미 저장된 노선의 이름 또는 색상으로는 변경할 수 없다.")
    @Test
    void invalidUpdate() {
        Line line = new Line("2호선", "초록색");
        Long id = lineDao.save(line).getId();
        Line updateLine = new Line("8호선", "분홍색");
        lineDao.save(updateLine);

        assertThatThrownBy(() -> lineDao.updateById(id, updateLine))
                .isInstanceOf(DuplicateKeyException.class);
    }

    @DisplayName("특정 id를 가지는 노선을 삭제한다.")
    @Test
    void deleteById() {
        Line line = new Line("2호선", "초록색");
        Long id = lineDao.save(line).getId();

        lineDao.deleteById(id);

        assertThat(lineDao.findAll()).isEmpty();
    }

    @DisplayName("id 값이 저장되어 있는지 확인한다.")
    @Test
    void checkExistsId() {
        Line line = new Line("2호선", "초록색");
        Long id = lineDao.save(line).getId();

        boolean actual = lineDao.existsId(id);

        assertThat(actual).isTrue();
    }

    @DisplayName("이름이 저장되어 있는지 확인한다.")
    @Test
    void checkExistsName() {
        Line line = new Line("2호선", "초록색");
        lineDao.save(line);

        boolean actual = lineDao.existsName(line);

        assertThat(actual).isTrue();
    }

    @DisplayName("색상이 저장되어 있는지 확인한다.")
    @Test
    void checkExistsColor() {
        Line line = new Line("2호선", "초록색");
        lineDao.save(line);

        boolean actual = lineDao.existsColor(line);

        assertThat(actual).isTrue();
    }

    @DisplayName("id가 같은 값에 대해서는 저장되어 있는지 검사를 하지 않는다.")
    @Test
    void notCheckExists() {
        Line line = new Line("2호선", "초록색");
        Long id = lineDao.save(line).getId();
        Line checkLine = new Line(id, line.getName(), line.getColor());

        assertThat(lineDao.existsName(checkLine)).isFalse();
        assertThat(lineDao.existsColor(checkLine)).isFalse();
    }

    private void assertEquals(Line expected, Line actual) {
        assertThat(expected.getId()).isEqualTo(actual.getId());
        assertThat(expected.getName()).isEqualTo(actual.getName());
        assertThat(expected.getColor()).isEqualTo(actual.getColor());
    }

}
