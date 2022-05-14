package wooteco.subway.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class SectionServiceTest {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;
    private final SectionService sectionService;

    SectionServiceTest(LineDao lineDao, StationDao stationDao, SectionDao sectionDao, SectionService sectionService) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
        this.sectionService = sectionService;
    }

    @Test
    @DisplayName("구간을 추가한다(처음)")
    void addSection_First() {
        Station station1 = stationDao.save("강남역");
        Station station2 = stationDao.save("선릉역");
        Station station3 = stationDao.save("잠실역");
        LineRequest lineRequest = new LineRequest("2호선", "green", station2.getId(), station3.getId(), 10);
        Line line = lineDao.save(lineRequest);
        sectionDao.saveInitialSection(lineRequest, line.getId());
        SectionRequest sectionRequest = new SectionRequest(station1.getId(), station2.getId(), 5);

        sectionService.addSection(sectionRequest, line.getId());

        List<Section> actual = sectionDao.findByLine(line.getId()).get();
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getUpStationId()).isEqualTo(station2.getId());
        assertThat(actual.get(0).getDownStationId()).isEqualTo(station3.getId());
        assertThat(actual.get(1).getUpStationId()).isEqualTo(station1.getId());
        assertThat(actual.get(1).getDownStationId()).isEqualTo(station2.getId());
        assertThat(actual.get(0).getDistance()).isEqualTo(10);
    }


    @Test
    @DisplayName("구간을 추가한다(중간 - 1-2 추가)")
    void addSection_BetweenFromFront() {
        Station station1 = stationDao.save("강남역");
        Station station2 = stationDao.save("선릉역");
        Station station3 = stationDao.save("잠실역");
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station3.getId(), 10);
        Line line = lineDao.save(lineRequest);
        sectionDao.saveInitialSection(lineRequest, line.getId());
        SectionRequest sectionRequest = new SectionRequest(station1.getId(), station2.getId(), 4);

        sectionService.addSection(sectionRequest, line.getId());

        List<Section> actual = sectionDao.findByLine(line.getId()).get();
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getUpStationId()).isEqualTo(station2.getId());
        assertThat(actual.get(0).getDownStationId()).isEqualTo(station3.getId());
        assertThat(actual.get(1).getUpStationId()).isEqualTo(station1.getId());
        assertThat(actual.get(1).getDownStationId()).isEqualTo(station2.getId());
        assertThat(actual.get(0).getDistance()).isEqualTo(6);
    }

    @Test
    @DisplayName("구간을 추가한다(중간 - 2-3 추가)")
    void addSection_BetweenFromBack() {
        Station station1 = stationDao.save("강남역");
        Station station2 = stationDao.save("선릉역");
        Station station3 = stationDao.save("잠실역");
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station3.getId(), 10);
        Line line = lineDao.save(lineRequest);
        sectionDao.saveInitialSection(lineRequest, line.getId());
        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 4);

        sectionService.addSection(sectionRequest, line.getId());

        List<Section> actual = sectionDao.findByLine(line.getId()).get();
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getUpStationId()).isEqualTo(station1.getId());
        assertThat(actual.get(0).getDownStationId()).isEqualTo(station2.getId());
        assertThat(actual.get(1).getUpStationId()).isEqualTo(station2.getId());
        assertThat(actual.get(1).getDownStationId()).isEqualTo(station3.getId());
        assertThat(actual.get(0).getDistance()).isEqualTo(6);
    }

    @Test
    @DisplayName("구간을 추가한다(끝)")
    void addSection_End() {
        Station station1 = stationDao.save("강남역");
        Station station2 = stationDao.save("선릉역");
        Station station3 = stationDao.save("잠실역");
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        sectionDao.saveInitialSection(lineRequest, line.getId());
        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 4);

        sectionService.addSection(sectionRequest, line.getId());

        List<Section> actual = sectionDao.findByLine(line.getId()).get();
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getUpStationId()).isEqualTo(station1.getId());
        assertThat(actual.get(0).getDownStationId()).isEqualTo(station2.getId());
        assertThat(actual.get(1).getUpStationId()).isEqualTo(station2.getId());
        assertThat(actual.get(1).getDownStationId()).isEqualTo(station3.getId());
        assertThat(actual.get(0).getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("추가할 위치의 구간이 없다면 예외를 발생시킨다.")
    void addSection_NoSectionException() {
        Station station1 = stationDao.save("강남역");
        Station station2 = stationDao.save("선릉역");
        Station station3 = stationDao.save("잠실역");
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        SectionRequest sectionRequest = new SectionRequest(station2.getId(), station3.getId(), 5);


        assertThatThrownBy(() -> sectionService.addSection(sectionRequest, line.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 구간이 존재하지 않습니다.");

    }

    @Test
    @DisplayName("기존 구간의 길이가 추가하려는 구간의 길이보다 짧다면 예외를 발생시킨다.")
    void addSection_distance1Exception() {
        Station station1 = stationDao.save("강남역");
        Station station2 = stationDao.save("선릉역");
        Station station3 = stationDao.save("잠실역");
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        sectionDao.saveInitialSection(lineRequest, line.getId());
        SectionRequest sectionRequest = new SectionRequest(station1.getId(), station3.getId(), 11);


        assertThatThrownBy(() -> sectionService.addSection(sectionRequest, line.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추가하려는 구간의 길이는 기존 구간길이보다 길 수 없습니다.");

    }

    @Test
    @DisplayName("기존 구간의 길이가 추가하려는 구간의 길이와 같다면 예외를 발생시킨다.")
    void addSection_equalDistanceException() {
        Station station1 = stationDao.save("강남역");
        Station station2 = stationDao.save("선릉역");
        Station station3 = stationDao.save("잠실역");
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        sectionDao.saveInitialSection(lineRequest, line.getId());
        SectionRequest sectionRequest = new SectionRequest(station1.getId(), station3.getId(), 10);


        assertThatThrownBy(() -> sectionService.addSection(sectionRequest, line.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("추가하려는 구간의 길이는 기존 구간길이보다 길 수 없습니다.");

    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void deleteSection() {
        Station station1 = stationDao.save("강남역");
        Station station2 = stationDao.save("선릉역");
        Station station3 = stationDao.save("잠실역");
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station3.getId(), 10);
        Line line = lineDao.save(lineRequest);
        sectionDao.saveInitialSection(lineRequest, line.getId());
        SectionRequest sectionRequest = new SectionRequest(station1.getId(), station2.getId(), 5);
        sectionService.addSection(sectionRequest, line.getId());

        sectionService.deleteSection(station2.getId(), line.getId());

        List<Section> actual = sectionDao.findByLine(line.getId()).get();
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getUpStationId()).isEqualTo(station1.getId());
        assertThat(actual.get(0).getDownStationId()).isEqualTo(station3.getId());
        assertThat(actual.get(0).getDistance()).isEqualTo(10);
    }

    @Test
    @DisplayName("삭제하려는 역이 포함된 구간이 존재하지 않으면 예외를 발생시킨다.")
    void deleteSection_NoStationException() {
        Station station1 = stationDao.save("강남역");
        Station station2 = stationDao.save("선릉역");
        Station station3 = stationDao.save("잠실역");
        Station station4 = stationDao.save("잠실새내역");
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        sectionDao.saveInitialSection(lineRequest, line.getId());
        SectionRequest sectionRequest = new SectionRequest(station1.getId(), station3.getId(), 5);
        sectionService.addSection(sectionRequest, line.getId());

        assertThatThrownBy(() -> sectionService.deleteSection(station4.getId(), line.getId()))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("해당 구간이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("노선 내의 구간이 하나 이상 존재하지 않으면 예외를 발생시킨다.")
    void deleteSection_OnlyOneSectionException() {
        Station station1 = stationDao.save("강남역");
        Station station2 = stationDao.save("선릉역");
        LineRequest lineRequest = new LineRequest("2호선", "green", station1.getId(), station2.getId(), 10);
        Line line = lineDao.save(lineRequest);
        sectionDao.saveInitialSection(lineRequest, line.getId());

        assertThatThrownBy(() -> sectionService.deleteSection(station1.getId(), line.getId()))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("노선 내의 구간이 하나 이하라면 삭제할 수 없습니댜.");
    }
}
