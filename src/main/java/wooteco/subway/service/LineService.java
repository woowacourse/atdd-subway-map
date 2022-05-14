package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.domain.Line;
import wooteco.subway.domain.LineSeries;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.repository.LineRepository;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    public LineResponse save(LineRequest lineRequest) {
        LineSeries lineSeries = new LineSeries(lineRepository.findAllLines());
        final Line line = lineSeries.create(lineRequest.getName(), lineRequest.getColor());
        line.addSection(sectionService.create(
            lineRequest.getUpStationId(),
            lineRequest.getDownStationId(),
            lineRequest.getDistance()));
        return LineResponse.from(lineRepository.save(line));
    }

    public List<LineResponse> findAll() {
        return lineRepository.findAllLines()
            .stream()
            .map(LineResponse::from)
            .collect(Collectors.toList());
    }

    public LineResponse findOne(Long id) {
        return LineResponse.from(lineRepository.findById(id));
    }

    public void update(Long id, LineRequest lineRequest) {
        lineRepository.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void delete(Long id) {
        lineRepository.delete(id);
    }
}
