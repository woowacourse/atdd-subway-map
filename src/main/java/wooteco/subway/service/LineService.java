package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineRepository;
import wooteco.subway.service.dto.DtoAssembler;
import wooteco.subway.service.dto.line.LineRequest;
import wooteco.subway.service.dto.line.LineResponse;

@Service
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse create(String name, String color) {
        Long lineId = lineRepository.saveLine(new Line(name, color));
        Line line = lineRepository.findLineById(lineId);
        return DtoAssembler.lineResponse(line);
    }

    public List<LineResponse> findAll() {
        return lineRepository.findLines()
                .stream()
                .map(DtoAssembler::lineResponse)
                .collect(Collectors.toUnmodifiableList());
    }

    public LineResponse findOne(Long id) {
        return DtoAssembler.lineResponse(lineRepository.findLineById(id));
    }

    @Transactional
    public void update(Long id, LineRequest lineRequest) {
        Line line = lineRepository.findLineById(id);
        line.update(lineRequest.getName(), lineRequest.getColor());
        lineRepository.updateLine(line);
    }

    @Transactional
    public void remove(Long id) {
        lineRepository.removeLine(id);
    }
}
