package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.dto.SectionDto;
import wooteco.subway.controller.dto.request.LineRequest;
import wooteco.subway.controller.dto.response.LineCreateResponseDto;
import wooteco.subway.controller.dto.response.LineFindAllResponseDto;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private static final String ERROR_MESSAGE_NOT_FOUND_LINE_ID = "Id에 해당하는 노선이 없습니다.";
    private static final int FIRST_INDEX = 0;

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineCreateResponseDto createLine(LineRequest lineRequest) {
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
//
//    public LineResponse showLine(Long lineId) {
//        Line foundLine = lineJdbcDao.findById(lineId)
//                .orElseThrow(() -> new IllegalArgumentException(ERROR_MESSAGE_NOT_FOUND_LINE_ID));
//        return new LineResponse(foundLine.getId(), foundLine.getName(), foundLine.getName());
//    }
//
//    public long editLine(Long lineId, LineEditRequest request) {
//        return lineJdbcDao.edit(lineId, request.getColor(), request.getName());
//    }
//
    public long deleteLine(Long lineId) {
        return lineRepository.deleteLineWithSectionByLineId(lineId);
    }
}
