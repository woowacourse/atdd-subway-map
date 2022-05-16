package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.request.SectionRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.service.fakeDao.LineDaoImpl;
import wooteco.subway.service.fakeDao.SectionDaoImpl;
import wooteco.subway.service.fakeDao.StationDaoImpl;

public class LineServiceTest {

    private final LineDao lineDao = LineDaoImpl.getInstance();
    private final StationDao stationDao = StationDaoImpl.getInstance();
    private final SectionDao sectionDao = SectionDaoImpl.getInstance();
    private final LineService lineService = new LineService(lineDao, stationDao, sectionDao);

    @BeforeEach
    void setUp() {
        final List<Line> lines = lineDao.findAll();
        final List<Station> stations = stationDao.findAll();
        lines.clear();
        stations.clear();
    }

    @Test
    @DisplayName("이미 존재하는 노선을 생성하려고 하면 에러를 발생한다.")
    void save_duplicate_station() {
        final Station station1 = stationDao.save(new Station("지하철역이름"));
        final Station station2 = stationDao.save(new Station("또다른지하철역이름"));
        final LineRequest lineRequest1 = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);
        final LineRequest lineRequest2 = new LineRequest("신분당선", "bg-green-600", station1.getId(), station2.getId(), 10);
        lineService.saveLine(lineRequest1);

        assertThatThrownBy(() -> lineService.saveLine(lineRequest2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 이름의 노선이 존재합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 노선을 접근하려고 하면 에러를 발생한다.")
    void not_exist_station() {
        final Station station1 = stationDao.save(new Station("지하철역이름"));
        final Station station2 = stationDao.save(new Station("또다른지하철역이름"));
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);

        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final Long invalidLineId = lineResponse.getId() + 1L;

        assertThatThrownBy(() -> lineService.deleteLine(invalidLineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당하는 노선이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("상행 종점을 등록한다.")
    void saveSectionToLastUpStation() {
        // given
        final Station station1 = stationDao.save(new Station("새로운상행종점"));
        final Station station2 = stationDao.save(new Station("기존상행종점"));
        final Station station3 = stationDao.save(new Station("기존하행종점"));
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station2.getId(), station3.getId(), 10);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(station1.getId(), station2.getId(), 10);

        // when
        final Long expected = station1.getId();
        lineService.saveSection(lineResponse.getId(), sectionRequest);

        // then
        final Line updatedLine = lineDao.findById(lineResponse.getId());
        final Long actual = getSections(updatedLine).getLastUpStation().getId();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("하행 종점을 등록한다.")
    void saveSectionToLastDownStation() {
        // given
        final Station station1 = stationDao.save(new Station("기존상행종점"));
        final Station station2 = stationDao.save(new Station("기존하행종점"));
        final Station station3 = stationDao.save(new Station("새로운하행종점"));
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 10);

        // when
        final Long expected = station3.getId();
        lineService.saveSection(lineResponse.getId(), sectionRequest);

        // then
        final Line updatedLine = lineDao.findById(lineResponse.getId());
        final Long actual = getSections(updatedLine).getLastDownStation().getId();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("등록하려는 구간의 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 에러를 발생한다.")
    void saveAllEqualsStation() {
        // given
        final Station station1 = stationDao.save(new Station("기존상행종점"));
        final Station station2 = stationDao.save(new Station("기존하행종점"));
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(station1.getId(), station2.getId(), 10);

        // when and then
        assertThatThrownBy(() -> lineService.saveSection(lineResponse.getId(), sectionRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("등록하려는 구간의 상행역과 하행역이 모두 노선에 등록되어 있지 않다면 에러를 발생한다.")
    void saveAllNotEqualsStation() {
        // given
        final Station station1 = stationDao.save(new Station("기존상행종점"));
        final Station station2 = stationDao.save(new Station("기존하행종점"));
        final Station station3 = stationDao.save(new Station("새로운상행역"));
        final Station station4 = stationDao.save(new Station("새로운하행역"));
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(station3.getId(), station4.getId(), 10);

        // when and then
        assertThatThrownBy(() -> lineService.saveSection(lineResponse.getId(), sectionRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("갈래길방지1 - 기존 구간의 상행역과 같고 하행역이 다를 경우, 기존 구간을 변경하고 등록한다.")
    void saveInterruptSameUpStation() {
        // given
        final Station station1 = stationDao.save(new Station("기존상행종점"));
        final Station station2 = stationDao.save(new Station("새로운하행"));
        final Station station3 = stationDao.save(new Station("기존하행종점"));
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station1.getId(), station3.getId(), 10);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(station1.getId(), station2.getId(), 3);

        // when
        lineService.saveSection(lineResponse.getId(), sectionRequest);

        // then
        final Line updatedLine = lineDao.findById(lineResponse.getId());
        Sections sections = getSections(updatedLine);
        List<Station> stations = sections.getStations();
        assertAll(() -> assertThat(stations.contains(station1)).isTrue(),
                () -> assertThat(stations.contains(station2)).isTrue(),
                () -> assertThat(stations.contains(station3)).isTrue());
    }

    @Test
    @DisplayName("갈래길방지2 - 기존 구간의 하행역과 같고 상행역이 다를 경우, 기존 구간을 변경하고 등록한다.")
    void saveInterruptSameDownStation() {
        // given
        final Station station1 = stationDao.save(new Station("기존상행종점"));
        final Station station2 = stationDao.save(new Station("새로운상행"));
        final Station station3 = stationDao.save(new Station("기존하행종점"));
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station1.getId(), station3.getId(), 10);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 3);

        // when
        lineService.saveSection(lineResponse.getId(), sectionRequest);

        // then
        final Line updatedLine = lineDao.findById(lineResponse.getId());
        Sections sections = getSections(updatedLine);
        List<Station> stations = sections.getStations();
        assertAll(() -> assertThat(stations.contains(station1)).isTrue(),
                () -> assertThat(stations.contains(station2)).isTrue(),
                () -> assertThat(stations.contains(station3)).isTrue());
    }

    @Test
    @DisplayName("갈래길방지3 -  역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 같거나 길면 등록할 수 없다.")
    void saveInterruptSameDistance() {
        // given
        final Station station1 = stationDao.save(new Station("기존상행종점"));
        final Station station2 = stationDao.save(new Station("새로운상행"));
        final Station station3 = stationDao.save(new Station("기존하행종점"));
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station1.getId(), station3.getId(), 10);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final SectionRequest sectionRequest1 = new SectionRequest(station2.getId(), station3.getId(), 10);
        final SectionRequest sectionRequest2 = new SectionRequest(station2.getId(), station3.getId(), 11);

        // when and then
        assertAll(() -> assertThatThrownBy(() -> lineService.saveSection(lineResponse.getId(), sectionRequest1))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> lineService.saveSection(lineResponse.getId(), sectionRequest2))
                        .isInstanceOf(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("구간이 하나인 노선에서 마지막 구간을 제거할 경우 에러를 발생한다.")
    void deleteLastSection() {
        // given
        final Station station1 = stationDao.save(new Station("상행종점"));
        final Station station2 = stationDao.save(new Station("하행종점"));
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);

        // when and then
        assertAll(() -> assertThatThrownBy(() -> lineService.deleteSection(lineResponse.getId(), station1.getId()))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> lineService.deleteSection(lineResponse.getId(), station2.getId()))
                        .isInstanceOf(IllegalArgumentException.class));
    }

    @Test
    @DisplayName("중간역이 제거될 경우 구간을 재배치한다.")
    void deleteMiddleSection() {
        // given
        final Station station1 = stationDao.save(new Station("상행종점"));
        final Station station2 = stationDao.save(new Station("중간종점"));
        final Station station3 = stationDao.save(new Station("하행종점"));
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", station1.getId(), station2.getId(), 10);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final Long lineId = lineResponse.getId();
        final SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 10);
        lineService.saveSection(lineResponse.getId(), sectionRequest);

        // when
        lineService.deleteSection(lineId, station2.getId());

        // then
        Sections sections = getSections(lineDao.findById(lineId));
        Long upStationId = sections.getLastUpStation().getId();
        Long downStationId = sections.getLastDownStation().getId();

        assertThat(upStationId).isEqualTo(station1.getId());
        assertThat(downStationId).isEqualTo(station3.getId());
        assertThat(sectionDao.findByLineIdAndUpStationId(lineId, upStationId).getDistance()).isEqualTo(20);
    }

    private Sections getSections(Line line) {
        List<Section> sections = sectionDao.findByLineId(line.getId());
        return new Sections(sections);
    }
}
