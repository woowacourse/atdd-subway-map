package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.controller.request.LineRequest;
import wooteco.subway.domain.line.Line;
import wooteco.subway.repository.LineRepository;

import java.util.List;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(LineRepository lineRepository, SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    public Line createLine(LineRequest lineRequest) {
        Line line = Line.builder()
                .name(lineRequest.getName())
                .color(lineRequest.getColor())
                .build();
        long id = lineRepository.save(line);
        sectionService.createSection(lineRequest, id);
        return lineRepository.findById(id);
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public Line findById(long id) {
        return lineRepository.findById(id);
    }

    public void editLine(long id, LineRequest lineRequest) {
        Line line = Line.builder()
                .id(id)
                .name(lineRequest.getName())
                .color(lineRequest.getColor())
                .build();
        lineRepository.update(line);
    }

    public void deleteLine(long id) {
        lineRepository.deleteById(id);
        sectionService.deleteAllByLineId(id);
    }
}
