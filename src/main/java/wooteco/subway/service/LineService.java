package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.SectionDto;
import wooteco.subway.controller.dto.StationDto;
import wooteco.subway.controller.dto.request.LineEditRequestDto;
import wooteco.subway.controller.dto.request.LineRequestDto;
import wooteco.subway.controller.dto.response.LineCreateResponseDto;
import wooteco.subway.controller.dto.response.LineFindAllResponseDto;
import wooteco.subway.controller.dto.response.LineFindResponseDto;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.StationRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String ERROR_MESSAGE_NOT_FOUND_LINE_ID = "Id에 해당하는 노선이 없습니다.";
    private static final int FIRST_INDEX = 0;

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineCreateResponseDto createLine(LineRequestDto lineRequest) {
//        lineJdbcDao.findByName(lineRequest.getName()).ifPresent(line -> {
//            throw new IllegalArgumentException("이미 존재하는 노선 이름입니다.");
//        });
        Line newLine = lineRepository.saveLineWithSection(
                lineRequest.getName(),
                lineRequest.getColor(),
                lineRequest.getUpStationId(),
                lineRequest.getDownStationId(),
                lineRequest.getDistance()
        );
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
        List<Section> sections = line.getSections();
        List<StationDto> stationDtos = new ArrayList<>();
        for (Section section : sections) {
            // TODO - List<Section>을 바탕으로 Station의 정보를 조회해서 stationDtos 만들기
            // TODO - 이 때, List<Section>이 Station 순서대로 조회되야 한다!
        }

        return new LineFindResponseDto(
                line, stationDtos
        );
    }

    public long editLine(Long lineId, LineEditRequestDto request) {
        return lineRepository.edit(lineId, request.getName(), request.getColor());
    }

    public long deleteLine(Long lineId) {
        return lineRepository.deleteLineWithSectionByLineId(lineId);
    }
}
