package wooteco.subway.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.exception.ExceptionStatus;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.service.dto.SectionDto;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
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
    void createSection() {
        long upStationId = 1;
        long downStationId = 2;
        int distance = 14;
        long lineId = 1;
        long sectionId = 1;
        Station upStation = new Station(upStationId, "천호역");
        Station downStation = new Station(downStationId, "강남역");
        Section section = new Section(upStation, downStation, distance, lineId);
        given(stationService.findById(upStationId)).willReturn(upStation);
        given(stationService.findById(downStationId)).willReturn(downStation);
        given(sectionRepository.save(section)).willReturn(sectionId);

        SectionDto sectionDto = SectionDto.builder()
                .upStationId(upStationId)
                .downStationId(downStationId)
                .distance(distance)
                .build();
        long createdSectionId = sectionService.createSection(sectionDto, lineId);

        assertThat(createdSectionId).isEqualTo(sectionId);
        verify(stationService, times(1)).findById(upStationId);
        verify(stationService, times(1)).findById(downStationId);
        verify(sectionRepository, times(1)).save(section);
    }

    @DisplayName("구간 정보 저장시 상행과 하행이 같을 수 없다.")
    @Test
    void cannotCreateSection() {
        long upStationId = 1;
        int distance = 14;
        long lineId = 1;
        Station upStation = new Station(upStationId, "천호역");
        SectionDto sectionDto = SectionDto.builder()
                .upStationId(upStationId)
                .downStationId(upStationId)
                .distance(distance)
                .build();
        given(stationService.findById(upStationId)).willReturn(upStation);

        assertThatCode(() -> sectionService.createSection(sectionDto, lineId))
                .isInstanceOf(SubwayException.class)
                .hasMessage(ExceptionStatus.INVALID_SECTION.getMessage());
        verify(stationService, times(2)).findById(upStationId);
    }

    @DisplayName("기존의 구간의 중간에 신규 구간을 삽입한다.")
    @Test
    void insertSection() {
        Station upStation = new Station(1L, "천호역");
        Station downStation = new Station(2L, "강남역");
        Station insertStation = new Station(3L, "중간역");

        Section currentSection = new Section(1L, upStation, downStation, 10, 1L);
        Section requestSection = new Section(upStation, insertStation, 5, 1L);

        List<Section> currentSectionList = Arrays.asList(currentSection);
        Sections currentSections = new Sections(currentSectionList);
        Section splitSection = currentSections.splitLongerSectionAfterAdding(requestSection);

        given(stationService.findById(1L)).willReturn(upStation);
        given(stationService.findById(3L)).willReturn(insertStation);
        given(sectionRepository.findAllByLineId(1L)).willReturn(currentSectionList);

        SectionDto sectionDto = SectionDto.builder()
                .upStationId(1L)
                .downStationId(3L)
                .distance(5)
                .build();
        sectionService.addSection(sectionDto, 1L);

        verify(stationService, times(1)).findById(1L);
        verify(stationService, times(1)).findById(3L);
        verify(sectionRepository, times(1)).findAllByLineId(1L);
        verify(sectionRepository, times(1)).update(splitSection);
        verify(sectionRepository, times(1)).save(requestSection);
    }

    @DisplayName("기존의 구간의 신규 종점 구간을 등록한다.")
    @Test
    void addNewEndSection() {
        Station upStation = new Station(1L, "천호역");
        Station downStation = new Station(2L, "강남역");
        Station newEndStation = new Station(3L, "중간역");

        Section currentSection = new Section(1L, upStation, downStation, 10, 1L);
        Section requestSection = new Section(newEndStation, upStation, 5, 1L);

        given(stationService.findById(1L)).willReturn(upStation);
        given(stationService.findById(3L)).willReturn(newEndStation);
        given(sectionRepository.findAllByLineId(1L)).willReturn(Arrays.asList(currentSection));

        SectionDto sectionDto = SectionDto.builder()
                .upStationId(3L)
                .downStationId(1L)
                .distance(5)
                .build();
        sectionService.addSection(sectionDto, 1L);

        verify(stationService, times(1)).findById(1L);
        verify(stationService, times(1)).findById(3L);
        verify(sectionRepository, times(1)).findAllByLineId(1L);
        verify(sectionRepository, times(1)).save(requestSection);
    }

    @DisplayName("기존 구간의 종점 구간을 삭제한다.")
    @Test
    void deleteEndSection() {
        Station upStation = new Station(1L, "천호역");
        Station middleStation = new Station(2L, "강남역");
        Station downStation = new Station(3L, "의정역");

        Section firstSection = new Section(1L, upStation, middleStation, 10, 1L);
        Section lastSection = new Section(2L, middleStation, downStation, 10, 1L);
        List<Section> currentSections = Arrays.asList(firstSection, lastSection);

        given(sectionRepository.findAllByLineId(1L)).willReturn(currentSections);
        given(sectionRepository.findAllByStationId(1L)).willReturn(Arrays.asList(firstSection));

        sectionService.deleteSection(1L, 1L);

        verify(sectionRepository, times(1)).findAllByLineId(1L);
        verify(sectionRepository, times(1)).findAllByStationId(1L);
        verify(sectionRepository, times(1)).delete(firstSection);
    }

    @DisplayName("기존 구간의 중간 구간을 삭제한다.")
    @Test
    void deleteMiddleSection() {
        Station upStation = new Station(1L, "천호역");
        Station middleStation = new Station(2L, "강남역");
        Station downStation = new Station(3L, "의정역");

        Section firstSection = new Section(1L, upStation, middleStation, 10, 1L);
        Section lastSection = new Section(2L, middleStation, downStation, 10, 1L);
        List<Section> currentSectionList = Arrays.asList(firstSection, lastSection);
        Sections currentSections = new Sections(currentSectionList);
        Sections removableSections = new Sections(currentSectionList);

        given(sectionRepository.findAllByLineId(1L)).willReturn(currentSectionList);
        given(sectionRepository.findAllByStationId(2L)).willReturn(currentSectionList);

        sectionService.deleteSection(1L, 2L);

        verify(sectionRepository, times(1)).findAllByLineId(1L);
        verify(sectionRepository, times(1)).findAllByStationId(2L);
        verify(sectionRepository, times(1)).save(removableSections.append());
        verify(sectionRepository, times(1)).delete(firstSection);
        verify(sectionRepository, times(1)).delete(lastSection);
    }
}
