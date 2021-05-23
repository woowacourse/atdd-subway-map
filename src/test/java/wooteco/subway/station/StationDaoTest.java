package wooteco.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.UnitTest;
import wooteco.subway.exception.SubwayCustomException;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.line.Line;
import wooteco.subway.line.LineDao;
import wooteco.subway.line.section.Section;
import wooteco.subway.line.section.SectionDao;

@DisplayName("지하철역 DAO 관련 기능")
class StationDaoTest extends UnitTest {

    private final StationDao stationDao;
    private final LineDao lineDao;
    private final SectionDao sectionDao;

    public StationDaoTest(StationDao stationDao, LineDao lineDao,
        SectionDao sectionDao) {
        this.stationDao = stationDao;
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
    }

    @Test
    @DisplayName("역 한개가 저장된다.")
    void save() {
        Station station = stationDao.save(new Station("잠실역"));
        assertThat(station.getId()).isEqualTo(1L);
        assertThat(station.getName()).isEqualTo("잠실역");
    }

    @Test
    @DisplayName("중복된 이름을 갖는 역은 저장이 안된다.")
    void duplicateSaveValidate() {
        Station station = new Station("잠실역");
        stationDao.save(station);

        assertThatThrownBy(() -> stationDao.save(station))
            .isInstanceOf(SubwayCustomException.class)
            .hasMessage(SubwayException.DUPLICATE_STATION_EXCEPTION.message());
    }

    @Test
    @DisplayName("전체 가져오기 테스트")
    void findAll() {
        Station station1 = stationDao.save(new Station("잠실역"));
        Station station2 = stationDao.save(new Station("잠실새내역"));

        List<Station> stationsAll = stationDao.findAll();
        assertThat(stationsAll).hasSize(2)
            .containsExactly(station1, station2);
    }

    @Test
    @DisplayName("id를 이용해 한개 삭제한다.")
    void delete() {
        //given
        stationDao.save(new Station("잠실역"));

        //when
        stationDao.delete(1L);

        //then
        assertThat(stationDao.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("Station 이 구간에 포함되어 있을때 역을 삭제하면 에러가 발생한다.")
    void deleteWithUseStation() {
        //given
        stationDao.save(new Station("잠실역"));
        stationDao.save(new Station("역삼역"));
        lineDao.save(new Line("2호선", "green"));
        sectionDao.save(1L, new Section(1L, 2L, 10));

        //when
        ThrowableAssert.ThrowingCallable callable = () -> stationDao.delete(1L);

        //then
        assertThatThrownBy(callable).isInstanceOf(SubwayCustomException.class)
            .hasMessage(SubwayException.ILLEGAL_STATION_DELETE_EXCEPTION.message());
    }
}