package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;

@Service
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse create(final LineRequest lineRequest) {
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line savedLine = lineRepository.save(line);
        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor());
    }
}
