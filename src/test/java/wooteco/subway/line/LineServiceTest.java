package wooteco.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.domain.Id;
import wooteco.subway.exception.line.NotFoundLineException;
import wooteco.subway.exception.section.InvalidDistanceException;
import wooteco.subway.exception.station.NotFoundStationException;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Color;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Name;
import wooteco.subway.line.dto.CreateLineDto;
import wooteco.subway.line.dto.LineServiceDto;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Distance;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.dto.DeleteStationDto;
import wooteco.subway.section.dto.SectionServiceDto;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {

    private final Line oneLine = new Line(1L, "OneLine", "BLUE");
    private final Station sinSeolStation = new Station(1L, "신설동역");
    private final Station dongMyoStation = new Station(2L, "동묘앞역");
    private final Station dongDaeMoonStation = new Station(3L, "동대문역");
    private final Station seoulStation = new Station(4L, "서울역");
    private final Distance distance = new Distance(10);
    private final Section firstSection = new Section(new Id(1L), oneLine, sinSeolStation, dongMyoStation, distance);
    private final Section secondSection = new Section(new Id(2L), oneLine, dongMyoStation, dongDaeMoonStation, distance);
    private final Long lineId = 1L;
    private final Long stationSinSeolId = 1L;
    private final Long stationDongMyoId = 2L;
    private final Long stationDongDaeMoonId = 3L;
    private final int intDistance = 10;
    private final List<Section> sections = Arrays.asList(firstSection,secondSection);

    @Mock
    private SectionDao mockSectionDao;
    @Mock
    private StationDao mockStationDao;
    @Mock
    private LineDao mockLineDao;
    @InjectMocks
    private LineService lineService;

    @BeforeEach
    void setUp() {
        SectionServiceDto sinSeolAndDongMyoDto = new SectionServiceDto(lineId, stationSinSeolId,
            stationDongMyoId, intDistance);
        SectionServiceDto dongMyoAndDongDaeMoonDto = new SectionServiceDto(lineId, stationDongMyoId,
            stationDongDaeMoonId, intDistance);
    }

    @Test
    @DisplayName("Line 추가")
    void createLine() {
        // given
        long newLineId = 2L;
        String lineName = "2호선";
        String lineColor = "초록색";
        long sectionId = 1L;
        long downStationId = 4L;
        long upStationId = 3L;
        int distance = 10;
        Line line = new Line(new Id(newLineId), new Name(lineName), new Color(lineColor));
        when(mockLineDao.save(any())).thenReturn(line);
        when(mockStationDao.show(upStationId)).thenReturn(Optional.of(dongDaeMoonStation));
        when(mockStationDao.show(downStationId)).thenReturn(Optional.of(seoulStation));
        when(mockStationDao.showAll()).thenReturn(Arrays.asList(dongDaeMoonStation, seoulStation));
        when(mockLineDao.show(newLineId)).thenReturn(Optional.of(line));
        when(mockSectionDao.save(any())).thenReturn(new Section(new Id(1L), line,
            dongDaeMoonStation,seoulStation, new Distance(distance)));
        when(mockSectionDao.findAllByLineId(2L)).thenReturn(Arrays.asList());


        // when
        CreateLineDto createLineDto = new CreateLineDto(lineName, lineColor, upStationId, downStationId, distance);
        LineServiceDto lineServiceDto = lineService.createLine(createLineDto);

        // then
        assertThat(lineServiceDto.getId()).isEqualTo(newLineId);
        assertThat(lineServiceDto.getName()).isEqualTo(lineName);
        assertThat(lineServiceDto.getColor()).isEqualTo(lineColor);
    }

    @Test
    @DisplayName("Line 업데이트시에 해당 라인 없을시 오류")
    void updateLineException() {
        // given
        long newLineId = 2L;
        String updateLineName = "3호선";
        String updateLineColor = "보라색";
        long sectionId = 1L;
        when(mockLineDao.show(2L)).thenReturn(Optional.empty());

        // when
        LineServiceDto lineServiceDto = new LineServiceDto(newLineId, updateLineName, updateLineColor);


        //then
        assertThatThrownBy(()-> lineService.update(lineServiceDto))
            .isInstanceOf(NotFoundLineException.class);
    }

    @Test
    @DisplayName("Section 추가")
    void createSectionWithLineAndStations() {
        // given
        long sectionId = 3L;
        long downStationId = 4L;
        long upStationId = 3L;
        int distance = 10;
        String name = "회기역";
        when(mockStationDao.show(3L)).thenReturn(Optional.of(dongDaeMoonStation));
        when(mockStationDao.show(4L)).thenReturn(Optional.of(seoulStation));
        when(mockLineDao.show(1L)).thenReturn(Optional.of(oneLine));
        when(mockSectionDao.save(any())).thenReturn(new Section(new Id(3L), oneLine,
            dongDaeMoonStation,seoulStation, new Distance(distance)));
        when(mockSectionDao.findAllByLineId(1L)).thenReturn(sections);


        // when
        SectionServiceDto dongDaeMoonAndHaegiDto = new SectionServiceDto(lineId, upStationId,
            downStationId, distance);
        SectionServiceDto savedDongDaeMoonAndHaegiDto = lineService.saveSection(dongDaeMoonAndHaegiDto);

        // then
        assertThat(savedDongDaeMoonAndHaegiDto.getId()).isEqualTo(sectionId);
        assertThat(savedDongDaeMoonAndHaegiDto.getLineId()).isEqualTo(lineId);
        assertThat(savedDongDaeMoonAndHaegiDto.getUpStationId()).isEqualTo(upStationId);
        assertThat(savedDongDaeMoonAndHaegiDto.getDownStationId()).isEqualTo(downStationId);
        assertThat(savedDongDaeMoonAndHaegiDto.getDistance()).isEqualTo(distance);
    }

    @Test
    @DisplayName("역 사이에 추가")
    void createSectionBetweenSections() {
        // given
        long targetUpStationId = 2L;
        long targetDownStationId = 4L;
        int targetDistance = 5;
        String name = "회기역";

        List<Section> allByLineId = sections;
        when(mockStationDao.show(2L)).thenReturn(Optional.of(dongMyoStation));
        when(mockStationDao.show(4L)).thenReturn(Optional.of(seoulStation));
        when(mockLineDao.show(1L)).thenReturn(Optional.of(oneLine));
        when(mockSectionDao.findAllByLineId(1L)).thenReturn(sections);
        when(mockSectionDao.save(any())).thenReturn(new Section(new Id(3L), oneLine,
            dongMyoStation,seoulStation, new Distance(targetDistance)));

        // when
        SectionServiceDto dongDaeMoonAndHaegiDto = new SectionServiceDto(lineId, targetUpStationId,
            targetDownStationId, targetDistance);
        SectionServiceDto savedDongDaeMoonAndHaegiDto = lineService.saveSection(dongDaeMoonAndHaegiDto);
        assertThat(savedDongDaeMoonAndHaegiDto).isNotNull();


        // then
        assertThat(savedDongDaeMoonAndHaegiDto.getLineId()).isEqualTo(lineId);
        assertThat(savedDongDaeMoonAndHaegiDto.getUpStationId()).isEqualTo(targetUpStationId);
        assertThat(savedDongDaeMoonAndHaegiDto.getDownStationId()).isEqualTo(targetDownStationId);
        assertThat(savedDongDaeMoonAndHaegiDto.getDistance()).isEqualTo(targetDistance);
    }

    @Test
    @DisplayName("역 사이에 먼 거리 추가")
    void createSectionBetweenSectionsWithExcessDistance() {
        // given
        long targetUpStationId = 2L;
        long targetDownStationId = 4L;
        Distance targetDistance = new Distance(15);
        when(mockStationDao.show(2L)).thenReturn(Optional.of(dongMyoStation));
        when(mockStationDao.show(4L)).thenReturn(Optional.of(seoulStation));
        when(mockLineDao.show(1L)).thenReturn(Optional.of(oneLine));
        when(mockSectionDao.findAllByLineId(1L)).thenReturn(sections);

        // when
        SectionServiceDto dongDaeMoonAndHaegiDto =
            new SectionServiceDto(lineId, targetUpStationId, targetDownStationId, targetDistance.value());

        // then
        assertThatThrownBy(() -> lineService.saveSection(dongDaeMoonAndHaegiDto))
            .isInstanceOf(InvalidDistanceException.class);
    }

    @Test
    @DisplayName("지하철 역이 삭제 시 없는 노선 확인")
    void deleteSection() {
        // given
        DeleteStationDto deleteStationDto = new DeleteStationDto(lineId, 5L);

        // when

        // then
        assertThatThrownBy(() ->lineService.deleteSection(deleteStationDto))
            .isInstanceOf(NotFoundStationException.class);
    }

    @Test
    @DisplayName("지하철 역이 2개만 있을 때(구간이 1개일 때)의 삭제")
    void deleteSectionWithTwoStations() {
        // given
        List<Section> sections = Arrays.asList(firstSection);
        DeleteStationDto preparedDeleteStationDto = new DeleteStationDto(lineId, stationDongMyoId);
        DeleteStationDto targetDeleteStationDto = new DeleteStationDto(lineId, stationSinSeolId);
        when(mockSectionDao.findAllByLineId(any())).thenReturn(sections);
        when(mockStationDao.show(stationSinSeolId)).thenReturn(Optional.of(sinSeolStation));

        // when

        // then
        assertThatThrownBy(() -> lineService.deleteSection(targetDeleteStationDto))
            .isInstanceOf(IllegalStateException.class);
    }
}