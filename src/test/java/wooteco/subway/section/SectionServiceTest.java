package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.NotExistStationException;
import wooteco.subway.exception.SectionDistanceException;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.service.StationService;

@DisplayName("Line Service")
@Sql("classpath:tableInit.sql")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest
public class SectionServiceTest {

    private final SectionService sectionService;
    private final StationService stationService;

    public SectionServiceTest(SectionService sectionService,
        StationService stationService) {
        this.sectionService = sectionService;
        this.stationService = stationService;
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = stationService.createStation(new StationRequest("B"));

        // when
        Section section = sectionService.create(1L, new SectionRequest(new Section(1L, stationA, stationB, 10)));

        // then
        assertThat(section.getLineId()).isEqualTo(1L);
        assertThat(section.getUpStationId()).isEqualTo(1L);
        assertThat(section.getDownStationId()).isEqualTo(2L);
        assertThat(section.getDistance()).isEqualTo(10);
    }

    @DisplayName("구간 생성시 존재 하지 않는 역이라면, 예외를 던진다.")
    @Test
    void createSectionNotExistStationException() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = new Station();
        SectionRequest sectionRequest = new SectionRequest(stationA.getId(), stationB.getId(), 10);

        // when, then
        assertThatThrownBy(() -> {
            sectionService.create(1L, sectionRequest);
        }).isInstanceOf(NotExistStationException.class);
    }

    @DisplayName("구간 생성시 구간의 길이가 0보다 크지 않으면, 예외를 던진다.")
    @Test
    void createSectionDistanceException() {
        // given
        Station stationA = stationService.createStation(new StationRequest("A"));
        Station stationB = stationService.createStation(new StationRequest("B"));
        SectionRequest sectionRequest = new SectionRequest(stationA.getId(), stationB.getId(), 0);

        // when, then
        assertThatThrownBy(() -> {
            sectionService.create(1L, sectionRequest);
        }).isInstanceOf(SectionDistanceException.class);
    }

}
