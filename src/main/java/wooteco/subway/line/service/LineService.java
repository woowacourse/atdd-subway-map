package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.DuplicateNameException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.LinesResponse;
import wooteco.subway.line.repository.LineRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {
    private final LineRepository lineRepository;

    public LineService(final LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public List<LinesResponse> getAllLines() {
        List<Line> lines = lineRepository.getLines();
        return lines.stream()
                .map(it -> new LinesResponse(it.getId(), it.getName(), it.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse save(final LineRequest lineRequest) {
        Line newLine = new Line(lineRequest.getColor(), lineRequest.getName());
        if (lineRepository.isExistName(newLine)) {
            throw new DuplicateNameException("이미 존재하는 Line 입니다.");
        }
        Line savedLine = lineRepository.save(newLine);
        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor());
    }

    public LineResponse getLineById(final Long id) {
        Line line = lineRepository.getLineById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    public void updateLine(final Long id, final LineRequest lineRequest) {
        lineRepository.update(new Line(id, lineRequest.getColor(), lineRequest.getName()));
    }

    public void deleteById(final Long id) {
        lineRepository.deleteById(id);
    }
}
