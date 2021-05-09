package wooteco.subway.line.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;

import java.util.List;

@Service
public class LineService {

    private final LineRepository lineRepository;

    @Autowired
    public LineService(final LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public Line save(Line line) {
        return lineRepository.save(line);
    }

    public List<Line> allLines() {
        return lineRepository.allLines();
    }

    public Line findById(final Long id) {
        return lineRepository.findById(id);
    }

    public void update(final Line line) {
        lineRepository.update(line);
    }

    public void deleteById(final Long id) {
        lineRepository.deleteById(id);
    }
}
