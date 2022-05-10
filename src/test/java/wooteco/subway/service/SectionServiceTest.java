package wooteco.subway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.StationResponse;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock
    private JdbcSectionDao jdbcSectionDao;

    @Mock
    private StationService stationService;

    @InjectMocks
    private SectionService sectionService;


    @DisplayName("구간을 등록한다.")
    @Test
    void createSection() {
        Section section = new Section(1L, 1L, 2L, 5);

        doReturn(1L)
                .when(jdbcSectionDao)
                .save(section);

        Long id = sectionService.createSection(section);

        assertThat(id).isEqualTo(1L);
    }

    @DisplayName("노선에 따른 지하철역을 조회한다.")
    @Test
    void getStationsByLineId() {
        doReturn(new Sections(List.of(
                new Section(1L, 1L, 2L, 5),
                new Section(1L, 2L, 3L, 5)

        )))
                .when(jdbcSectionDao)
                .findByLineId(1L);

        doReturn(new StationResponse(1L, "강남역"))
                .when(stationService)
                .getStation(1L);

        doReturn(new StationResponse(2L, "사당역"))
                .when(stationService)
                .getStation(2L);

        doReturn(new StationResponse(3L, "잠실역"))
                .when(stationService)
                .getStation(3L);

        List<StationResponse> stationResponses = sectionService.getStationsByLineId(1L);

        assertThat(stationResponses.size()).isEqualTo(3);
    }
}