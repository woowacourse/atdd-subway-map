package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotExistItemException;

@Sql("classpath:tableInit.sql")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
class LineDaoTest {

    private final LineDao lineDao;

    public LineDaoTest(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @DisplayName("노선 한 개가 저장된다.")
    @Test
    void save() {
        Line line = lineDao.save(new Line("2호선", "bg-green-600"));
        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("bg-green-600");
    }

    @DisplayName("중복된 이름을 갖는 노선은 저장이 안된다.")
    @Test
    void duplicateSaveValidate() {
        Line line = new Line("2호선", "bg-green-600");
        lineDao.save(line);

        assertThatThrownBy(() -> {
            lineDao.save(line);
        }).isInstanceOf(DuplicateException.class);
    }

    @DisplayName("모든 노선 목록을 조회한다")
    @Test
    void findAll() {
        Line line2 = lineDao.save(new Line("2호선", "bg-green-600"));
        Line line3 = lineDao.save(new Line("3호선", "bg-orange-600"));
        Line line4 = lineDao.save(new Line("4호선", "bg-skyBlue-600"));

        List<Line> lines = Arrays.asList(line2, line3, line4);
        List<Line> linesAll = lineDao.findAll();

        assertThat(linesAll).hasSize(3);

        for (int i = 0; i < linesAll.size(); i++) {
            assertThat(lines.get(i).getId()).isEqualTo(lines.get(i).getId());
            assertThat(lines.get(i).getName()).isEqualTo(lines.get(i).getName());
            assertThat(lines.get(i).getColor()).isEqualTo(lines.get(i).getColor());
        }
    }

    @DisplayName("id를 이용하여 노선을 조회한다.")
    @Test
    void findById() {
        lineDao.save(new Line("2호선", "bg-green-600"));
        lineDao.save(new Line("3호선", "bg-orange-600"));

        Line line = lineDao.findById(1L);
        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("bg-green-600");
    }

    @DisplayName("없는 id를 조회하면 에러가 출력된다.")
    @Test
    void notExistLineFindException() {
        assertThatThrownBy(() -> lineDao.findById(1L))
            .isInstanceOf(NotExistItemException.class);
    }

    @DisplayName("노선의 이름 또는 색상을 수정한다.")
    @Test
    void update() {
        Line line2 = new Line("2호선", "bg-green-600");
        Line newLine = new Line(1L, "3호선", "bg-orange-600");

        lineDao.save(line2);
        assertThat(lineDao.update(newLine)).isSameAs(newLine);
    }

    @DisplayName("id를 이용하여 노선을 삭제한다.")
    @Test
    void delete() {
        Line line2 = new Line("2호선", "bg-green-600");
        lineDao.save(line2);

        assertThatCode(() -> lineDao.delete(1L)).doesNotThrowAnyException();
        assertThat(lineDao.findAll()).hasSize(0);
    }
}