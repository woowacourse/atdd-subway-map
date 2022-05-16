package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.application.LineService;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.LineSaveRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.exception.NoSuchLineException;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private LineService sut;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private SectionDao sectionDao;

    @DisplayName("노선을 성공적으로 등록한다")
    @Test
    void testCreateLine() {
        // given
        Station station1 = stationDao.save(new Station("station1"));
        Station station2 = stationDao.save(new Station("station2"));

        // when
        LineResponse lineResponse = sut.createLine(
                new LineSaveRequest("line1", "color1", station1.getId(), station2.getId(), 10));

        // then
        LineResponse expected = new LineResponse(1L, "line1", "color1",
                List.of(StationResponse.from(station1), StationResponse.from(station2)));
        assertThat(lineResponse)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
    }

    @DisplayName("단건의 노선을 조회한다.")
    @Test
    void findLine() {
        // given
        Station station1 = stationDao.save(new Station("station1"));
        Station station2 = stationDao.save(new Station("station2"));
        LineResponse expected = sut.createLine(
                new LineSaveRequest("line1", "color1", station1.getId(), station2.getId(), 10));

        // when
        LineResponse actual = sut.findLine(expected.getId());

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 노선 id로 조회할 경우 예외를 반환한다.")
    @Test
    void findLineByNonExistId() {
        // given
        Station station1 = stationDao.save(new Station("station1"));
        Station station2 = stationDao.save(new Station("station2"));
        LineResponse createdLine = sut.createLine(
                new LineSaveRequest("line1", "color1", station1.getId(), station2.getId(), 10));

        sut.deleteLineById(createdLine.getId());

        // when && then
        assertThatThrownBy(() -> sut.findLine(createdLine.getId()))
                .isInstanceOf(NoSuchLineException.class);
    }

    @DisplayName("노선을 삭제하면 노선에 등록된 구간도 삭제한다.")
    @Test
    void deleteRelevantSectionsWhenDeleteLine() {
        // given
        Station station1 = stationDao.save(new Station("station1"));
        Station station2 = stationDao.save(new Station("station2"));
        LineResponse createdLine = sut.createLine(
                new LineSaveRequest("line1", "color1", station1.getId(), station2.getId(), 10));

        // when
        sut.deleteLineById(createdLine.getId());

        // then
        assertThat(sectionDao.findByLineId(createdLine.getId())).isEmpty();
    }

    @DisplayName("노선 목록을 조회한다.")
    @Test
    void findLines() {
        // given
        Station station1 = stationDao.save(new Station("station1"));
        Station station2 = stationDao.save(new Station("station2"));
        LineResponse createdLine1 = sut.createLine(
                new LineSaveRequest("line1", "color1", station1.getId(), station2.getId(), 10));

        Station station3 = stationDao.save(new Station("station3"));
        Station station4 = stationDao.save(new Station("station4"));
        LineResponse createdLine2 = sut.createLine(
                new LineSaveRequest("line2", "color2", station3.getId(), station4.getId(), 10));

        // when
        List<LineResponse> lines = sut.findLines();

        // then
        assertThat(lines).usingRecursiveComparison().isEqualTo(List.of(createdLine1, createdLine2));
    }

    @DisplayName("구간을 추가한다.")
    @Test
    void addSection() {
        // given
        Station station1 = stationDao.save(new Station("station1"));
        Station station2 = stationDao.save(new Station("station2"));
        Station station3 = stationDao.save(new Station("station3"));

        LineResponse createdLine = sut.createLine(
                new LineSaveRequest("line1", "color1", station1.getId(), station3.getId(), 10));

        // when
        sut.addSection(createdLine.getId(), new SectionRequest(station2.getId(), station3.getId(), 3));

        // then
        LineResponse line = sut.findLine(createdLine.getId());
        assertThat(line).usingRecursiveComparison()
                .isEqualTo(new LineResponse(createdLine.getId(), createdLine.getName(), createdLine.getColor(),
                        List.of(StationResponse.from(station1),
                                StationResponse.from(station2),
                                StationResponse.from(station3))));
    }

    @DisplayName("구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        Station station1 = stationDao.save(new Station("station1"));
        Station station2 = stationDao.save(new Station("station2"));
        Station station3 = stationDao.save(new Station("station3"));

        LineResponse createdLine = sut.createLine(
                new LineSaveRequest("line1", "color1", station1.getId(), station2.getId(), 10));
        sut.addSection(createdLine.getId(), new SectionRequest(station2.getId(), station3.getId(), 10));

        // when
        sut.deleteSection(createdLine.getId(), station3.getId());

        // then
        LineResponse line = sut.findLine(createdLine.getId());
        assertThat(line).usingRecursiveComparison().isEqualTo(new LineResponse(createdLine.getId(),
                createdLine.getName(), createdLine.getColor(), createdLine.getStations()));
    }
}
