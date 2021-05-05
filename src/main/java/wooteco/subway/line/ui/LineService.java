package wooteco.subway.line.ui;

import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;

public class LineService {

    private LineRepository lineRepository;

    public LineService(final LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public Line save(Line line) {
        final long id = lineRepository.save(line);

        return new Line(id, line.getName(), line.getColor());
    }

}
