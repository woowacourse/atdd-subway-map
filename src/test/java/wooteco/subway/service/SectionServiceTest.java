package wooteco.subway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Distance;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.SectionResponse;
import wooteco.subway.dto.StationRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@JdbcTest
class SectionServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private LineService lineService;
    private StationService stationService;
    private SectionService sectionService;

    private Long createdLineId;
    private Long createdStationId1;
    private Long createdStationId2;

    @BeforeEach
    void setUp() {
        lineService = new LineService(new LineDao(jdbcTemplate));
        stationService = new StationService(new StationDao(jdbcTemplate));
        sectionService = new SectionService(new SectionDao(jdbcTemplate));

        createdLineId = lineService.createLine(new LineRequest("2호선", "bg-green-600")).getId();
        createdStationId1 = stationService.createStation(new StationRequest("선릉역")).getId();
        createdStationId2 = stationService.createStation(new StationRequest("잠실역")).getId();
    }

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() {
        // given
        Section section = new Section(createdLineId, createdStationId1, createdStationId2, new Distance(10));
        SectionRequest sectionRequest = new SectionRequest(section);

        // when
        SectionResponse sectionResponse = sectionService.createLine(createdLineId, sectionRequest);

        // then
        assertAll(
                () -> assertThat(sectionResponse.getUpStationId()).isEqualTo(section.getUpStationId()),
                () -> assertThat(sectionResponse.getDownStationId()).isEqualTo(section.getDownStationId()),
                () -> assertThat(sectionResponse.getDistance()).isEqualTo(section.getDistance().getValue())
        );
    }
}
