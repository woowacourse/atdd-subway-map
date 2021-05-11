package wooteco.subway.line.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.controller.dto.LineCreateDto;
import wooteco.subway.line.controller.dto.LineDto;
import wooteco.subway.line.controller.dto.SectionCreateDto;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;
import wooteco.subway.line.exception.LineException;

@Service
@Transactional(readOnly = true)
public class LineService {

    private final LineRepository lineRepository;
    private final SectionService sectionService;

    public LineService(final LineRepository lineRepository, final SectionService sectionService) {
        this.lineRepository = lineRepository;
        this.sectionService = sectionService;
    }

    @Transactional
    public LineDto save(final LineCreateDto lineInfo) {
        validateExistingName(lineInfo.getName());
        sectionService.validateSection(lineInfo.getDownStationId(), lineInfo.getUpStationId());

        final Line requestedLine = lineInfo.toLineWithNoId();
        final Line savedLine = lineRepository.save(requestedLine);

        final SectionCreateDto newSectionInfo = SectionCreateDto.ofNewLine(savedLine, lineInfo);
        sectionService.save(newSectionInfo);

        return LineDto.of(savedLine);
    }

    public LineDto show(final Long id) {
        final Line line = findById(id);

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
        findById(id);
        lineRepository.delete(id);
    }

    private Line findById(final Long id) {
        return lineRepository.findById(id)
                .orElseThrow(() -> new LineException("노선이 존재하지 않습니다."));
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
            checkId(line, id);
        }
    }

    private void checkId(final Line line, final Long id) {
        if (!line.isId(id)) {
            throw new LineException("이미 존재하는 노선 이름입니다.");
        }
    }
}
