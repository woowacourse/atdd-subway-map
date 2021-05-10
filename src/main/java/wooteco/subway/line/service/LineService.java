package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineRepository;

import java.util.List;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(final LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public List<Line> getLines() {
        return lineRepository.getLines();
    }

    public Line save(final Line line) {
        if (lineRepository.isNameExist(line)) {
            throw new DuplicateLineNameException();
        }
        return lineRepository.save(line);
    }

    public Line getLine(final Long id) {
        if (lineRepository.isIdNotExist(id)) {
            throw new NoSuchLineException();
        }
        return lineRepository.getLine(id);
    }

    public void updateLine(final Line line) {
        if (lineRepository.isIdNotExist(line)) {
            throw new NoSuchLineException();
        }
        lineRepository.update(line);
    }

    public void deleteById(final Long id) {
        if (lineRepository.isIdNotExist(id)) {
            throw new NoSuchLineException();
        }
        lineRepository.deleteById(id);
    }
}
