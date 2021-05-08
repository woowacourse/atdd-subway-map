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
        final Optional<Line> line = lineRepository.findByName(requestedLine.getName());
        if (line.isPresent()) {
            throw new LineException("이미 존재하는 노선 이름입니다.");
        }

        final Line createdLine = lineRepository.save(requestedLine);
        return LineDto.of(createdLine);
    }

    public List<LineDto> showAll() {
        final List<Line> lines = lineRepository.findAll();

        return lines.stream()
                .map(LineDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void update(final Line requestedLine) {
        final Optional<Line> line = lineRepository.findById(requestedLine.getId());
        if (line.isPresent()) {
            lineRepository.update(requestedLine);
            return;
        }

        throw new LineException("수정하려는 노선이 존재하지 않습니다.");
    }

    @Transactional
    public void delete(final Long id) {
        final Optional<Line> line = lineRepository.findById(id);
        if (line.isPresent()) {
            lineRepository.delete(id);
            return;
        }

        throw new LineException("지우려고 하는 노선이 존재하지 않습니다");
    }

    public LineDto show(Long id) {
        final Optional<Line> line = lineRepository.findById(id);
        if (line.isPresent()) {
            return LineDto.of(line.get());
        }

        throw new LineException("해당 id의 노선이 존재하지 않습니다.");
    }
}
