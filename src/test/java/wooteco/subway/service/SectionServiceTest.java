package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;

@Transactional
@SpringBootTest
public class SectionServiceTest {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private StationService stationService;

    @Autowired
    private LineService lineService;

    @DisplayName("Section 요청 정보를 받아 Section을 저장한다.")
    @Test
    void save() {
        StationResponse stationResponse1 = stationService.createStation(new StationRequest("강남역"));
        StationResponse stationResponse2 = stationService.createStation(new StationRequest("잠실역"));
        StationResponse stationResponse3 = stationService.createStation(new StationRequest("선릉역"));

        LineResponse lineResponse = lineService.createLine(
                new LineRequest("신분당선", "bg-green", stationResponse1.getId(), stationResponse2.getId(), 3));

        SectionRequest sectionRequest = new SectionRequest(stationResponse2.getId(), stationResponse3.getId(), 3);
        Section connectedSection = sectionService.save(lineResponse.getId(), sectionRequest);

        assertAll(
                () -> assertThat(connectedSection.getUpStationId()).isEqualTo(stationResponse2.getId()),
                () -> assertThat(connectedSection.getDownStationId()).isEqualTo(stationResponse3.getId()),
                () -> assertTrue(connectedSection.getDistance() == 3)
        );
    }

    @DisplayName("노선의 Id로 Section을 조회한다.")
    @Test
    void getSectionsByLineId() {
        StationResponse stationResponse1 = stationService.createStation(new StationRequest("강남역"));
        StationResponse stationResponse2 = stationService.createStation(new StationRequest("잠실역"));
        StationResponse stationResponse3 = stationService.createStation(new StationRequest("선릉역"));

        LineResponse lineResponse = lineService.createLine(
                new LineRequest("신분당선", "bg-green", stationResponse1.getId(), stationResponse2.getId(), 3));

        sectionService.save(lineResponse.getId(), new SectionRequest(stationResponse3.getId(), stationResponse1.getId(), 5));

        List<Section> sections = sectionService.getSectionsByLineId(lineResponse.getId());

        assertThat(sections).containsExactly(new Section(lineResponse.getId(), stationResponse1.getId(), stationResponse2.getId(), 3),
                new Section(lineResponse.getId(), stationResponse3.getId(), stationResponse1.getId(), 5));
    }
}
