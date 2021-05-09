package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.station.Station;
import wooteco.subway.repository.SectionRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @InjectMocks
    private SectionService sectionService;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private StationService stationService;

    @DisplayName("구간 정보를 저장한다.")
    @Test
    void findAllByLineId() {
        long upStationId = 1;
        long downStationId = 2;
        int distance = 14;
        long lineId = 1;
        Station upStation = new Station(upStationId, "천호역");
        Station downStation = new Station(downStationId, "강남역");
        Section section = new Section(upStation, downStation, distance, lineId);
        given(stationService.findById(upStationId)).willReturn(upStation);
        given(stationService.findById(downStationId)).willReturn(downStation);
        given(sectionRepository.save(section)).willReturn(1L);

        long createdSectionId = sectionService.createSection(upStationId, downStationId, distance, lineId);

        assertThat(createdSectionId).isEqualTo(1L);
        verify(stationService, times(1)).findById(1);
        verify(stationService, times(1)).findById(2);
        verify(sectionRepository, times(1)).save(section);
    }
}
