package wooteco.subway.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.utils.exception.NameDuplicatedException;

@Service
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineResponse create(final LineRequest lineRequest) {
        validateDuplicateName(lineRepository.findByName(lineRequest.getName()));
        Line line = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    private void validateDuplicateName(final Optional<Line> line) {
        line.ifPresent(l -> {
            throw new NameDuplicatedException(NameDuplicatedException.NAME_DUPLICATE_MESSAGE);
        });
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse showLine(final Long id) {
        Line line = lineRepository.findById(id);
        return new LineResponse(line.getId(), line.getName(), line.getColor());
    }

    @Transactional
    public void update(final Long id, final LineRequest lineRequest) {
        Line currentLine = lineRepository.findById(id);
        if (!currentLine.isSameName(lineRequest.getName())) {
            validateDuplicateName(lineRepository.findByName(lineRequest.getName()));
        }
        lineRepository.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void delete(final Long id) {
        lineRepository.deleteById(id);
    }
}
