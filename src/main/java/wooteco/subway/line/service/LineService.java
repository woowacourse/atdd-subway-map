package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineRepository;

import java.util.List;

@Service
public class LineService {
    private final LineRepository lineRepository;

    public LineService(final LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public List<Line> getLines() {
        return lineRepository.getLines();
    }

    public Line save(final Line line) {
        if (lineRepository.isExistName(line)) {
            throw new IllegalArgumentException("이미 존재하는 Line 입니다.");
        }
        return lineRepository.save(line);
    }

    public Line getLine(final Long id) {
        return lineRepository.getLine(id);
    }

    public void updateLine(final Line line) {
        lineRepository.update(line);
    }

    public void deleteById(final Long id) {
        lineRepository.deleteById(id);
    }
}
