package wooteco.subway.line.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DuplicateLineNameException;
import wooteco.subway.exception.NotExistLineException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineRepository;

@Service
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public Line createLine(Line line) {
        if (lineRepository.isExistName(line)) {
            throw new DuplicateLineNameException();
        }
        return lineRepository.save(line);
    }

    @Transactional
    public Line showLine(Long id) {
        Optional<Line> line = lineRepository.findById(id);
        if (line.isPresent()) {
            return line.get();
        }
        throw new NotExistLineException();
    }

    @Transactional
    public List<Line> showLines() {
        List<Line> lines = lineRepository.findAll();

        if (lines.isEmpty()) {
            throw new NotExistLineException();
        }
        return lines;
    }

    @Transactional
    public void updateLine(Line line) {
        if (lineRepository.update(line) == 0) {
            throw new NotExistLineException();
        }
    }

    @Transactional
    public void deleteLine(Long id) {
        if (lineRepository.delete(id) == 0) {
            throw new NotExistLineException();
        }
    }

}
