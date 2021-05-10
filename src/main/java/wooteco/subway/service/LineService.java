package wooteco.subway.service;

import org.springframework.stereotype.Service;
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

    public Line createLine(String name, String color, long upStationId, long downStationId, int distance) {
        Line line = new Line(name, color);
        long id = lineRepository.save(line);
        sectionService.createSection(upStationId, downStationId, distance, id);
        return lineRepository.findById(id);
    }

    public List<Line> findAll() {
        return lineRepository.findAll();
    }

    public Line findById(long id) {
        return lineRepository.findById(id);
    }

    public void editLine(long id, String name, String color) {
        Line line = new Line(id, name, color);
        lineRepository.update(line);
    }

    public void deleteLine(long id) {
        lineRepository.deleteById(id);
    }
}
