package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.fakeDao.LineDaoImpl;
import wooteco.subway.service.fakeDao.SectionDaoImpl;
import wooteco.subway.service.fakeDao.StationDaoImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LineServiceTest {
    private final LineService lineService =
            new LineService(LineDaoImpl.getInstance(), StationDaoImpl.getInstance(), SectionDaoImpl.getInstance());

    private Station station1;
    private Station station2;
    private Station station3;
    private Station station4;

    @BeforeEach
    void setUp() {
        final List<Line> lines = LineDaoImpl.getInstance().findAll();
        lines.clear();
        final List<Station> stations = StationDaoImpl.getInstance().findAll();
        stations.clear();
        final List<Section> sections = SectionDaoImpl.getInstance().findAll();
        sections.clear();

        station1 = new Station("애플역");
        station2 = new Station("갤럭시역");
        station3 = new Station("옵티머스역");
        station4 = new Station("롤리팝역");
    }

    @Test
    @DisplayName("노선을 올바르게 저장한다.")
    void saveLine() {
        final LineRequest lineRequest = createLineRequest();

        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(lineRequest.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(lineRequest.getColor())
        );
    }

    @Test
    @DisplayName("노선을 저장할 때 구간도 같이 등록된다.")
    void saveLineWithSection() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        final LineRequest lineRequest =
                new LineRequest("신분당선", "bg-red-600", id1, id2, 20);

        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final List<Section> actual = SectionDaoImpl.getInstance().findByLineId(lineResponse.getId());

        assertThat(actual).contains(new Section(lineResponse.getId(), id1, id2, 20));
    }

    @Test
    @DisplayName("이미 존재하는 노선을 생성하려고 하면 에러를 발생한다.")
    void save_duplicate_station() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        final Long id3 = StationDaoImpl.getInstance().save(station3);

        final LineRequest lineRequest1 =
                new LineRequest("신분당선", "bg-green-600", id1, id2, 15);
        final LineRequest lineRequest2 =
                new LineRequest("신분당선", "bg-green-600", id1, id3, 15);

        lineService.saveLine(lineRequest1);

        assertThatThrownBy(() -> lineService.saveLine(lineRequest2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 이름의 노선이 존재합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 노선을 접근하려고 하면 에러를 발생한다.")
    void not_exist_station() {
        final LineRequest lineRequest = createLineRequest();

        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final Long invalidLineId = lineResponse.getId() + 1L;

        assertThatThrownBy(() -> lineService.deleteLine(invalidLineId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(invalidLineId + "번에 해당하는 노선이 존재하지 않습니다.");
    }

    private LineRequest createLineRequest() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        return new LineRequest("신분당선", "bg-red-600", id1, id2, 20);
    }

    @Test
    @DisplayName("상행 종점 구간을 추가한다.")
    void saveFinalUpSection() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        final Long id3 = StationDaoImpl.getInstance().save(station3);
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", id1, id2, 20);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(id3, id1, 10);

        lineService.addSection(lineResponse.getId(), sectionRequest);

        final List<Section> actual = SectionDaoImpl.getInstance().findByLineId(lineResponse.getId());

        assertThat(actual).contains(new Section(lineResponse.getId(), id3, id1, 10));
    }

    @Test
    @DisplayName("하행 종점 구간을 추가한다.")
    void saveFinalDownSection() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        final Long id3 = StationDaoImpl.getInstance().save(station3);
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", id1, id2, 20);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(id2, id3, 10);

        lineService.addSection(lineResponse.getId(), sectionRequest);
        final List<Section> actual = SectionDaoImpl.getInstance().findByLineId(lineResponse.getId());

        assertThat(actual).contains(new Section(lineResponse.getId(), id2, id3, 10));
    }

    @Test
    @DisplayName("상행역이 동일한 구간을 추가한다.")
    void saveSameUpStationSection() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        final Long id3 = StationDaoImpl.getInstance().save(station3);
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", id1, id2, 20);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(id1, id3, 10);

        lineService.addSection(lineResponse.getId(), sectionRequest);
        final List<Section> actual = SectionDaoImpl.getInstance().findByLineId(lineResponse.getId());

        assertThat(actual).contains(new Section(lineResponse.getId(), id1, id3, 10));
    }

    @Test
    @DisplayName("하행역이 동일한 구간을 추가한다.")
    void saveSameDownStationSection() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        final Long id3 = StationDaoImpl.getInstance().save(station3);
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", id1, id2, 20);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(id3, id2, 10);

        lineService.addSection(lineResponse.getId(), sectionRequest);
        final List<Section> actual = SectionDaoImpl.getInstance().findByLineId(lineResponse.getId());

        assertThat(actual).contains(new Section(lineResponse.getId(), id3, id2, 10));
    }

    @Test
    @DisplayName("올바르지 않은 구간을 추가할 때 예외를 발생시킨다.")
    void saveInvalidSection() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        final Long id3 = StationDaoImpl.getInstance().save(station3);
        final Long id4 = StationDaoImpl.getInstance().save(station4);
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", id1, id2, 20);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(id4, id3, 50);

        assertThatThrownBy(() -> lineService.addSection(lineResponse.getId(), sectionRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("등록하려는 구간의 상행역과 하행역 둘 중 하나는 노선에 포함된 역이어야 합니다.");
    }

    @Test
    @DisplayName("상행 종점 구간을 삭제한다.")
    void deleteFinalUpSection() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        final Long id3 = StationDaoImpl.getInstance().save(station3);
        final LineResponse lineResponse = addSection(id1, id2, id3);

        lineService.deleteSection(lineResponse.getId(), id1);
        final List<Section> actual = SectionDaoImpl.getInstance().findByLineId(lineResponse.getId());

        assertThat(actual).doesNotContain(new Section(lineResponse.getId(), id1, id3, 10));
    }

    @Test
    @DisplayName("하행 종점 구간을 삭제한다.")
    void deleteFinalDownSection() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        final Long id3 = StationDaoImpl.getInstance().save(station3);
        final LineResponse lineResponse = addSection(id1, id2, id3);

        lineService.deleteSection(lineResponse.getId(), id2);
        final List<Section> actual = SectionDaoImpl.getInstance().findByLineId(lineResponse.getId());

        assertThat(actual).doesNotContain(new Section(lineResponse.getId(), id3, id2, 10));
    }

    @Test
    @DisplayName("중간 구간을 삭제한다.")
    void deleteMiddleSection() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        final Long id3 = StationDaoImpl.getInstance().save(station3);
        final LineResponse lineResponse = addSection(id1, id2, id3);

        lineService.deleteSection(lineResponse.getId(), id3);
        final List<Section> actual = SectionDaoImpl.getInstance().findByLineId(lineResponse.getId());

        assertThat(actual).doesNotContain(new Section(lineResponse.getId(), id1, id3, 10));
    }

    @Test
    @DisplayName("올바르지 않은 구간을 삭제할 때 예외를 발생시킨다..")
    void deleteInvalidSection() {
        final Long id1 = StationDaoImpl.getInstance().save(station1);
        final Long id2 = StationDaoImpl.getInstance().save(station2);
        final Long id3 = StationDaoImpl.getInstance().save(station3);
        final Long id4 = StationDaoImpl.getInstance().save(station4);
        final LineResponse lineResponse = addSection(id1, id2, id3);

        assertThatThrownBy(() -> lineService.deleteSection(lineResponse.getId(), id4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 역입니다.");
    }

    private LineResponse addSection(Long id1, Long id2, Long id3) {
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", id1, id2, 20);
        final LineResponse lineResponse = lineService.saveLine(lineRequest);
        final SectionRequest sectionRequest = new SectionRequest(id3, id2, 10);
        lineService.addSection(lineResponse.getId(), sectionRequest);
        return lineResponse;
    }
}
