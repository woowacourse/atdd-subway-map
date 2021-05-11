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

    public LineService(final LineRepository lineRepository, final SectionService sectionService) {
        this.lineRepository = lineRepository;
    }

    @Transactional
    public LineDto save(final Line requestedLine) {
        validateExistingName(requestedLine.getName());

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
        validateNameById(requestedLine.getName(), requestedLine.getId());

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

    private void validateExistingName(final String name) {
        final Optional<Line> line = lineRepository.findByName(name);
        if (line.isPresent()) {
            throw new LineException("이미 존재하는 노선 이름입니다.");
        }
    }

    private void validateNameById(final String name, final Long id) {
        final Optional<Line> possibleLine = lineRepository.findByName(name);
        if (possibleLine.isPresent()) {
            final Line line = possibleLine.get();
            checkId(id, line);
        }
    }

    private void checkId(Long id, Line thisLine) {
        if (!thisLine.isId(id)) {
            throw new LineException("이미 존재하는 노선 이름입니다.");
        }
    }
}
