package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.LineSeries;
import wooteco.subway.dto.request.LineRequest;
import wooteco.subway.dto.response.LineResponse;
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
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        line.addSection(sectionService.create(
            lineRequest.getUpStationId(),
            lineRequest.getDownStationId(),
            lineRequest.getDistance()));
        lineSeries.add(line);
        lineRepository.persist(lineSeries);
        return LineResponse.from(line);
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
        LineSeries lineSeries = new LineSeries(lineRepository.findAllLines());
        lineSeries.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
        lineRepository.persist(lineSeries);
    }

    public void delete(Long id) {
        LineSeries lineSeries = new LineSeries(lineRepository.findAllLines());
        lineSeries.delete(id);
        lineRepository.persist(lineSeries);
    }
}
