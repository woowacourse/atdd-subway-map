package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.DuplicateException;
import wooteco.subway.exception.NotExistItemException;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Sql("classpath:tableInit.sql")
@DisplayName("노선 DAO 관련 기능")
class LineDaoTest {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final Line line2 = new Line(1L,"2호선", "bg-green-600", 1L, 2L, 10);
    private final Line line3 = new Line(2L, "3호선", "bg-orange-600", 1L, 3L, 13);
    private final Line line4 = new Line(3L, "4호선", "bg-skyBlue-600", 1L, 4L, 15);

    public LineDaoTest(LineDao lineDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
    }

    @BeforeEach
    void setUp() {
        stationDao.save(new Station("강남역"));
        stationDao.save(new Station("잠실역"));
        stationDao.save(new Station("역삼역"));
        stationDao.save(new Station("신림역"));
    }

    @Test
    @DisplayName("노선 한 개가 저장된다.")
    void save() {
        Line newLine = lineDao.save(line2);

        assertThat(line2).isEqualTo(newLine);
    }

    @Test
    @DisplayName("중복된 이름을 갖는 노선은 저장이 안된다.")
    void duplicateSaveValidate() {
        lineDao.save(line2);

        assertThatThrownBy(() -> lineDao.save(line2))
            .isInstanceOf(DuplicateException.class);
    }

    @Test
    @DisplayName("모든 노선 목록을 조회한다")
    void findAll() {
        Line newLine2 = lineDao.save(line2);
        Line newLine3 = lineDao.save(line3);
        Line newLine4 = lineDao.save(line4);

        List<Line> lines = lineDao.findAll();

        assertThat(lines).hasSize(3)
            .containsExactly(newLine2, newLine3, newLine4);
    }

    @Test
    @DisplayName("id를 이용하여 노선을 조회한다.")
    void findById() {
        Line line = lineDao.save(line2);

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
        lineDao.save(line2);

        Line newLine = new Line(1L, "3호선", "bg-orange-600", 1L, 2L, 10);

        //when
        lineDao.update(newLine);

        //then
        assertThat(lineDao.findById(1L)).isEqualTo(newLine);
    }

    @Test
    @DisplayName("이미 존재하는 노선의 이름으로 저장하면 에러가 발생한다.")
    void updateWithDuplicatedName() {
        //given
        lineDao.save(line2);
        lineDao.save(line3);

        Line updateLine = new Line(1L, "3호선", "bg-orange-600", 1L, 2L, 10);

        //when, then
        assertThatThrownBy(() -> lineDao.update(updateLine))
            .isInstanceOf(DuplicateException.class);
    }


    @Test
    @DisplayName("id를 이용하여 노선을 삭제한다.")
    void delete() {
        //given
        lineDao.save(line2);

        //when
        lineDao.delete(1L);

        //then
        assertThat(lineDao.findAll()).hasSize(0);
    }
}