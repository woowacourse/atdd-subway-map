package wooteco.subway.line.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;

import java.util.List;

@Service
public class LineService {
    public static final String ERROR_DUPLICATED_LINE_NAME = "라인이 중복되었습니다.";

    private final LineRepository lineRepository;

    @Autowired
    public LineService(final LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public Line create(Line line) {
        checkCreateValidation(line);
        final long id = lineRepository.save(line);

        return new Line(id, line.getName(), line.getColor());
    }

    public List<Line> allLines() {
        return lineRepository.findAll();
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

    private void checkCreateValidation(Line line) {
        boolean duplicated = lineRepository.findAll().contains(line);
        if (duplicated) {
            throw new IllegalArgumentException(ERROR_DUPLICATED_LINE_NAME);
        }

    }
}
