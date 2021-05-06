package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.line.repository.dto.LineDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineDto save(final LineDto linedto) {
        Line line = new Line(linedto.getName(), linedto.getColor());
        return LineDto.from(lineRepository.save(line));
    }

    public List<LineDto> findAll() {
        return lineRepository.findAll().stream()
                .map(LineDto::from)
                .collect(Collectors.toList());
    }

    public LineDto findById(final Long id) {
        return LineDto.from(lineRepository.findById(id));
    }

    public void delete(final Long id) {
        lineRepository.delete(id);
    }

    public void update(final LineDto lineDto) {
        Line line = lineRepository.findById(lineDto.getId());
        Line updatedLine = line.update(lineDto.getName(), lineDto.getColor());
        lineRepository.update(updatedLine);
    }
}
