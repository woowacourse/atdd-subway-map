package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.dao.dto.LineUpdateDto;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.utils.exception.NameDuplicatedException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
@Service
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineResponse create(final LineRequest lineRequest) {
        validateDuplicateName(lineRepository.findByName(lineRequest.getName()));
        Line line = new Line(lineRequest.getName(), lineRequest.getColor());
        Line savedLine = lineRepository.save(line);
        return new LineResponse(savedLine.getId(), savedLine.getName(), savedLine.getColor());
    }

    private void validateDuplicateName(final Line line) {
        if (Objects.nonNull(line)) {
            throw new NameDuplicatedException("[ERROR] 이미 존재하는 이름입니다.");
        }
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor()))
                .collect(Collectors.toList());
    }

    public LineResponse showLine(final Long id) {
        Line findLine = lineRepository.findById(id);
        return new LineResponse(findLine.getId(), findLine.getName(), findLine.getColor());
    }

    public void update(final Long id, final LineRequest lineRequest) {
        lineRepository.update(LineUpdateDto.of(id, lineRequest));
    }
}
