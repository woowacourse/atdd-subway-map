package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.UnitTest;
import wooteco.subway.exception.SubwayCustomException;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

@DisplayName("노선 DAO 관련 기능")
class LineDaoTest extends UnitTest {

    private static final Line LINE_2 = new Line(1L, "2호선", "bg-green-600");
    private static final Line LINE_3 = new Line(2L, "3호선", "bg-orange-600");
    private static final Line LINE_4 = new Line(3L, "4호선", "bg-skyBlue-600");

    private final LineDao lineDao;
    private final StationDao stationDao;

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
        Line newLine = lineDao.save(LINE_2);

        assertThat(LINE_2).isEqualTo(newLine);
    }

    @Test
    @DisplayName("중복된 이름을 갖는 노선은 저장이 안된다.")
    void duplicateSaveValidate() {
        lineDao.save(LINE_2);

        assertThatThrownBy(() -> lineDao.save(LINE_2))
            .isInstanceOf(SubwayCustomException.class)
            .hasMessage(SubwayException.DUPLICATE_LINE_EXCEPTION.message());
    }

    @Test
    @DisplayName("모든 노선 목록을 조회한다")
    void findAll() {
        Line newLine2 = lineDao.save(LINE_2);
        Line newLine3 = lineDao.save(LINE_3);
        Line newLine4 = lineDao.save(LINE_4);

        List<Line> lines = lineDao.findAll();

        assertThat(lines).hasSize(3)
            .containsExactly(newLine2, newLine3, newLine4);
    }

    @Test
    @DisplayName("id를 이용하여 노선을 조회한다.")
    void findById() {
        Line line = lineDao.save(LINE_2);

        Line newLine = lineDao.findById(1L);
        assertThat(newLine).isEqualTo(line);
    }

    @Test
    @DisplayName("없는 id를 조회하면 에러가 출력된다.")
    void notExistLineFindException() {
        assertThatThrownBy(() -> lineDao.findById(1L))
            .isInstanceOf(SubwayCustomException.class)
            .hasMessage(SubwayException.NOT_EXIST_LINE_EXCEPTION.message());
    }

    @Test
    @DisplayName("노선의 이름 또는 색상을 수정한다.")
    void update() {
        //given
        lineDao.save(LINE_2);

        Line newLine = new Line(1L, "3호선", "bg-orange-600");

        //when
        lineDao.update(newLine);

        //then
        assertThat(lineDao.findById(1L)).isEqualTo(newLine);
    }

    @Test
    @DisplayName("이미 존재하는 노선의 이름으로 저장하면 에러가 발생한다.")
    void updateWithDuplicatedName() {
        //given
        lineDao.save(LINE_2);
        lineDao.save(LINE_3);

        Line updateLine = new Line(1L, "3호선", "bg-orange-600");

        //when, then
        assertThatThrownBy(() -> lineDao.update(updateLine))
            .isInstanceOf(SubwayCustomException.class)
            .hasMessage(SubwayException.DUPLICATE_LINE_EXCEPTION.message());
    }


    @Test
    @DisplayName("id를 이용하여 노선을 삭제한다.")
    void delete() {
        //given
        lineDao.save(LINE_2);

        //when
        lineDao.delete(1L);

        //then
        assertThat(lineDao.findAll()).hasSize(0);
    }
}