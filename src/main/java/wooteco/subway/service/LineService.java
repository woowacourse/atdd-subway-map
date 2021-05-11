package wooteco.subway.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.controller.dto.SectionDto;
import wooteco.subway.controller.dto.StationDto;
import wooteco.subway.controller.dto.request.LineCreateRequestDto;
import wooteco.subway.controller.dto.request.LineEditRequestDto;
import wooteco.subway.controller.dto.response.LineCreateResponseDto;
import wooteco.subway.controller.dto.response.LineFindAllResponseDto;
import wooteco.subway.controller.dto.response.LineFindResponseDto;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.exception.SubwayException;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.StationRepository;

@Service
@Transactional
public class LineService {

    private static final int FIRST_INDEX = 0;

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineCreateResponseDto createLine(LineCreateRequestDto lineRequest) {
        Line newLine;
        try {
            newLine = lineRepository.saveLineWithSection(
                lineRequest.getName(),
                lineRequest.getColor(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance()
            );
        } catch (DuplicateKeyException e) {
            throw new SubwayException("중복된 이름으로 Line을 등록할 수 없습니다.");
        }
        Section section = newLine.getSections().get(FIRST_INDEX);
        List<SectionDto> sectionDtos = Arrays.asList(
            new SectionDto(
                section.getId(),
                section.getUpStationId(),
                section.getDownStationId(),
                section.getDistance()
            )
        );
        return new LineCreateResponseDto(
            newLine.getId(),
            newLine.getName(),
            newLine.getColor(),
            sectionDtos
        );
    }

    public List<LineFindAllResponseDto> showLines() {
        List<Line> lines = lineRepository.findAllLine();
        return lines.stream()
            .map(it -> new LineFindAllResponseDto(it.getId(), it.getName(), it.getColor()))
            .collect(Collectors.toList());
    }

    public LineFindResponseDto showLine(Long lineId) {
        Line line = lineRepository.findLineWithSectionsById(lineId);
        List<Section> sections = line.sortSections();
        List<Station> stations = new ArrayList<>();
        List<Long> stationIds = sections.stream().map(section -> section.getDownStationId())
            .collect(Collectors.toList());
        List<StationDto> stationDtos = stations.stream()
            .map(station -> new StationDto(station.getId(), station.getName()))
            .collect(Collectors.toList());

        Station firstStation = stationRepository.findStationById(sections.get(0).getUpStationId())
            .orElseThrow(() -> new SubwayException("해당 라인에 역이 존재하지 않습니다."));
        stations.add(firstStation);
        List<Station> foundStations = stationRepository.findStationsByIds(stationIds);
        stations.addAll(foundStations);

        return new LineFindResponseDto(
            line, stationDtos
        );
    }

    public long editLine(Long lineId, LineEditRequestDto request) {
        return lineRepository.edit(lineId, request.getName(), request.getColor());
    }

    public int deleteLine(Long lineId) {
        return lineRepository.deleteLineWithSectionByLineId(lineId);
    }

    public void createSectionInLine(Long lineId, Long upStationId, Long downStationId,
        int distance) {
        lineRepository.createSectionInLine(lineId, upStationId, downStationId, distance);
    }

    public void deleteSectionInLine(Long lineId, Long stationId) {
        lineRepository.deleteSectionInLine(lineId, stationId);
    }
}
