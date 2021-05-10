package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotExistItemException;

@SpringBootTest()
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Sql("classpath:tableInit.sql")
@DisplayName("노선 DAO 관련 기능")
class LineDaoTest {

    private final LineDao lineDao;

    public LineDaoTest(LineDao lineDao) {
        this.lineDao = lineDao;
    }

    @Test
    @DisplayName("노선 한 개가 저장된다.")
    void save() {
        Line line = lineDao.save(new Line("2호선", "bg-green-600"));

        assertThat(line.getId()).isEqualTo(1L);
        assertThat(line.getName()).isEqualTo("2호선");
        assertThat(line.getColor()).isEqualTo("bg-green-600");
    }

    @Test
    @DisplayName("중복된 이름을 갖는 노선은 저장이 안된다.")
    void duplicateSaveValidate() {
        Line line = new Line("2호선", "bg-green-600");
        lineDao.save(line);

        assertThatThrownBy(() -> lineDao.save(line))
            .isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("모든 노선 목록을 조회한다")
    void findAll() {
        Line line2 = lineDao.save(new Line("2호선", "bg-green-600"));
        Line line3 = lineDao.save(new Line("3호선", "bg-orange-600"));
        Line line4 = lineDao.save(new Line("4호선", "bg-skyBlue-600"));

        List<Line> lines = lineDao.findAll();

        assertThat(lines).hasSize(3).containsExactly(line2, line3, line4);
    }

    @Test
    @DisplayName("id를 이용하여 노선을 조회한다.")
    void findById() {
        Line line = lineDao.save(new Line("2호선", "bg-green-600"));
        lineDao.save(new Line("3호선", "bg-orange-600"));

        Line newLine = lineDao.findById(1L);
        assertThat(newLine).isEqualTo(line);
    }

    @Test
    @DisplayName("없는 id를 조회하면 에러가 출력된다.")
    void notExistLineFindException() {
        assertThatThrownBy(() -> lineDao.findById(1L))
            .isInstanceOf(NotExistItemException.class);
    }

    @Test
    @DisplayName("노선의 이름 또는 색상을 수정한다.")
    void update() {
        //given
        Line line = new Line("2호선", "bg-green-600");
        Line newLine = new Line(1L, "3호선", "bg-orange-600");
        lineDao.save(line);

        //when
        lineDao.update(newLine);

        //then
        assertThat(lineDao.findById(1L)).isEqualTo(newLine);
    }

    @Test
    @DisplayName("이미 존재하는 노선의 이름으로 저장하면 에러가 발생한다.")
    void updateWithDuplicatedName() {
        //given
        Line line2 = new Line("2호선", "bg-green-600");
        Line line3 = new Line("3호선", "bg-orange-600");
        lineDao.save(line2);
        lineDao.save(line3);
        Line newLine = new Line(1L, "3호선", "bg-orange-600");

        //when, then
        assertThatThrownBy(() -> lineDao.update(newLine)).isInstanceOf(DuplicateException.class);
    }


    @Test
    @DisplayName("id를 이용하여 노선을 삭제한다.")
    void delete() {
        //given
        Line line = new Line("2호선", "bg-green-600");
        lineDao.save(line);

        //when
        lineDao.delete(1L);

        //then
        assertThat(lineDao.findAll()).hasSize(0);
    }
}