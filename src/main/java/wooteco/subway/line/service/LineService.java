package wooteco.subway.line.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.controller.dto.LineDto;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;
import wooteco.subway.line.exception.LineException;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;

    public LineService(final LineDao lineRepository) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineDto save(final Line requestedLine) {
        validateName(requestedLine.getName());

        final Line createdLine = lineRepository.save(requestedLine);
        return LineDto.of(createdLine);
    }

    public LineDto show(Long id) {
        final Line line = validateExisting(id);

        return LineDto.of(line);
    }

    public List<LineDto> showAll() {
        final List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(final Line requestedLine) {
        validateExisting(requestedLine.getId());
        validateName(requestedLine.getName());

        lineRepository.update(requestedLine);
    }

    @Transactional
    public void delete(final Long id) {
        validateExisting(id);
        lineRepository.delete(id);

    }

    private Line validateExisting(final Long id) {
        final Optional<Line> line = lineRepository.findById(id);
        if (!line.isPresent()) {
            throw new LineException("노선이 존재하지 않습니다.");
        }
        return line.get();
    }

    private void validateName(final String name) {
        final Optional<Line> line = lineRepository.findByName(name);
        if (line.isPresent()) {
            throw new LineException("이미 존재하는 노선 이름입니다.");
        }
    }
}
