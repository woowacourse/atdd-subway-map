package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.line.repository.dto.LineDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineRepository lineRepository;

    public LineService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public LineDto save(final LineDto linedto) {
        Optional<Line> optionalLine = lineRepository.findByName(linedto.getName());
        if (optionalLine.isPresent()) {
            throw new DuplicatedNameException("이미 존재하는 지하철 노선 이름입니다.");
        }
        Line line = new Line(linedto.getName(), linedto.getColor());
        return LineDto.from(lineRepository.save(line));
    }

    public List<LineDto> findAll() {
        return lineRepository.findAll().stream()
                .map(LineDto::from)
                .collect(Collectors.toList());
    }

    public LineDto findById(final Long id) {
        Optional<Line> optionalLine = lineRepository.findById(id);
        if (optionalLine.isPresent()) {
            return LineDto.from(optionalLine.get());
        }
        throw new LineNotFoundException("해당 Id의 지하철 노선은 존재하지 않습니다.");
    }

    public void delete(final Long id) {
        Optional<Line> optionalLine = lineRepository.findById(id);
        if (optionalLine.isPresent()) {
            lineRepository.delete(id);
            return;
        }
        throw new LineNotFoundException("해당 Id의 지하철 노선은 존재하지 않습니다.");
    }

    public void update(final LineDto lineDto) {
        try {
            Line line = lineRepository.findById(lineDto.getId()).get();
            Line updatedLine = line.update(lineDto.getName(), lineDto.getColor());
            lineRepository.update(updatedLine);
        } catch (NoSuchElementException e) {
            throw new LineNotFoundException("해당 Id의 지하철 노선은 존재하지 않습니다.");
        }
    }
}
