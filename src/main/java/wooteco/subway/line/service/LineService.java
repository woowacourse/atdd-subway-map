package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;
import wooteco.subway.exception.DuplicatedNameException;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.line.repository.dto.LineDto;
import wooteco.subway.line.service.dto.LineSaveDto;

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

    @Transactional
    public LineDto save(final LineSaveDto lineSaveDto) {
        try {
            Line line = new Line(lineSaveDto.getName(), lineSaveDto.getColor());
            return LineDto.from(lineRepository.save(line));
        } catch (DuplicateKeyException e) {
            throw new DuplicatedNameException("이미 존재하는 지하철 노선 이름 입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<LineDto> findAll() {
        return lineRepository.findAll().stream()
                .map(LineDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineDto findById(final Long id) {
        Optional<Line> optionalLine = lineRepository.findById(id);
        Line line = optionalLine.orElseThrow(LineNotFoundException::new);
        return LineDto.from(line);
    }

    @Transactional
    public void delete(final Long id) {
        Optional<Line> optionalLine = lineRepository.findById(id);
        optionalLine.orElseThrow(LineNotFoundException::new);
        lineRepository.delete(id);
    }

    @Transactional
    public void update(final LineDto lineDto) {
        try {
            Line line = lineRepository.findById(lineDto.getId()).orElseThrow(LineNotFoundException::new);
            Line updatedLine = line.update(lineDto.getName(), lineDto.getColor());
            lineRepository.update(updatedLine);
        } catch (NoSuchElementException e) {
            throw new LineNotFoundException("해당 Id의 지하철 노선은 존재하지 않습니다.");
        }
    }
}
