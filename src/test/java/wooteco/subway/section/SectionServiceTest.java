package wooteco.subway.section;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.exception.NotExistStationException;
import wooteco.subway.exception.SectionDistanceException;
import wooteco.subway.section.domain.Section;
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
        Station station1 = new Station("쌍문역");
        Station station2 = new Station("수유역");

        stationService.createStation(station1);
        stationService.createStation(station2);

        Section section = new Section(4L, 1L, 2L, 10);
        sectionService.createSection(section);

        assertThat(section.getLineId()).isEqualTo(4L);
        assertThat(section.getUpStationId()).isEqualTo(1L);
        assertThat(section.getDownStationId()).isEqualTo(2L);
        assertThat(section.getDistance()).isEqualTo(10);
    }

    @DisplayName("구간 생성시 존재 하지 않는 역이라면, 예외를 던진다.")
    @Test
    void createSectionNotExistStationException() {
        Station station1 = new Station("쌍문역");

        stationService.createStation(station1);

        Section section = new Section(4L, 1L, 2L, 10);
        assertThatThrownBy(() -> {
            sectionService.createSection(section);
        }).isInstanceOf(NotExistStationException.class);
    }

    @DisplayName("구간 생성시 구간의 길이가 0보다 크지 않으면, 예외를 던진다.")
    @Test
    void createSectionDistanceException() {
        Station station1 = new Station("쌍문역");
        Station station2 = new Station("수유역");

        stationService.createStation(station1);
        stationService.createStation(station2);

        Section section = new Section(4L, 1L, 2L, 0);

        assertThatThrownBy(() -> {
            sectionService.createSection(section);
        }).isInstanceOf(SectionDistanceException.class);
    }

}
