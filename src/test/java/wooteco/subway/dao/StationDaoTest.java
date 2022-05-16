package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.station.NoSuchStationException;

class StationDaoTest extends DaoTest {

    private static final String RED_STATION_NAME = "선릉";
    private static final String BLUE_STATION_NAME = "노원";

    private Station redStation;
    private Station blueStation;

    @BeforeEach
    void setUpData() {
        redStation = new Station(RED_STATION_NAME);
        blueStation = new Station(BLUE_STATION_NAME);
    }

    @Test
    @DisplayName("역을 저장하면 저장된 역 정보를 반환한다.")
    void Save() {
        // when
        final Station savedStation = stationDao.insert(redStation).orElseThrow();

        // then
        assertThat(savedStation.getName()).isEqualTo(RED_STATION_NAME);
    }

    @Test
    @DisplayName("id에 해당하는 노선을 조회한다.")
    void FindById() {
        // given
        final Long id = stationDao.insert(redStation)
                .orElseThrow()
                .getId();

        final Station expected = new Station(id, RED_STATION_NAME);

        // when
        final Optional<Station> actual = stationDao.findById(id);

        // then
        assertThat(actual).isPresent()
                .contains(expected);
    }

    @Test
    @DisplayName("id에 해당하는 노선이 존재하지 않으면 빈 Optional 을 반환한다.")
    void FindById_NotExistId_EmptyOptionalReturned() {
        // given
        final Long id = 999L;

        // when
        final Optional<Station> actual = stationDao.findById(id);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("모든 역 조회하기")
    void FindAll() {
        // given
        stationDao.insert(redStation);
        stationDao.insert(blueStation);

        // when
        final List<Station> stations = stationDao.findAll();

        // then
        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("노선 id에 해당하는 모든 역을 조회한다.")
    void FindAllByLineId() {
        // given
        final String lineName = "2호선";
        final String lineColor = "bg-green-600";
        final Long lineId = lineDao.insert(new Line(lineName, lineColor))
                .orElseThrow()
                .getId();
        final Line line = new Line(lineId, lineName, lineColor);

        final Station upStation = stationDao.insert(redStation)
                .orElseThrow();

        final Station downStation = stationDao.insert(blueStation)
                .orElseThrow();

        final List<Station> expected = List.of(upStation, downStation);

        final Section section = new Section(
                line,
                upStation,
                downStation,
                new Distance(10)
        );
        sectionDao.insert(section);

        // when
        final List<Station> actual = stationDao.findAllByLineId(lineId);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("id에 해당하는 역 삭제하기")
    void DeleteById() {
        // given
        final Station station = stationDao.insert(redStation).orElseThrow();

        // when
        final Integer affectedRows = stationDao.deleteById(station.getId());

        // then
        assertThat(affectedRows).isOne();
    }

    @Test
    @DisplayName("존재하지 않는 id의 역을 삭제하면 예외가 발생한다.")
    void DeleteById_InvalidId_ExceptionThrown() {
        assertThatThrownBy(() -> stationDao.deleteById(999L))
                .isInstanceOf(NoSuchStationException.class);
    }
}