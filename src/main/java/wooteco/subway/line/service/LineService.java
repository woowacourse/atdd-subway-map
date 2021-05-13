package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.exception.LineNotFoundException;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.line.repository.dto.LineDto;
import wooteco.subway.line.service.dto.LineSaveDto;
import wooteco.subway.section.domain.Distance;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.repository.SectionRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
    }

    @Transactional
    public LineDto saveLineAndSection(final LineSaveDto lineSaveDto) {
        Line savedLine = lineRepository.save(new Line(lineSaveDto.getName(), lineSaveDto.getColor()));
        sectionRepository.save(new Section(savedLine.getId(), lineSaveDto.getUpStationId(),
                lineSaveDto.getDownStationId(), new Distance(lineSaveDto.getDistance())));
        return LineDto.from(savedLine);
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
