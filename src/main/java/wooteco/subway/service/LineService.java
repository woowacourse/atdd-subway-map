package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;
import wooteco.subway.repository.StationRepository;
import wooteco.subway.utils.exception.NameDuplicatedException;

@Transactional
@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository,
                       SectionRepository sectionRepository,
                       StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse create(final LineRequest lineRequest) {
        String name = lineRequest.getName();
        validateDuplicateName(lineRepository.isNameExists(name), name);
        Long id = lineRepository.save(new Line(name, lineRequest.getColor()));
        Station upStation = stationRepository.findById(lineRequest.getUpStationId());
        Station downStation = stationRepository.findById(lineRequest.getDownStationId());

        Section section = new Section(id, upStation, downStation, lineRequest.getDistance());
        sectionRepository.save(section);
        Line line = new Line(id, name, lineRequest.getColor(), new Sections(List.of(section)));
        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                List.of(new StationResponse(upStation),
                        new StationResponse(downStation)));
    }

    private void validateDuplicateName(final boolean isDuplicateName, final String name) {
        if (isDuplicateName) {
            throw new NameDuplicatedException(NameDuplicatedException.NAME_DUPLICATE_MESSAGE + name);
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(),
                        line.getName(),
                        line.getColor(),
                        line.getSections().sortSections()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse showLine(final Long id) {
        Line line = lineRepository.findById(id);
        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                new Sections(sectionRepository.findByLineId(id)).sortSections());
    }

    public void update(final Long id, final LineRequest lineRequest) {
        Line currentLine = lineRepository.findById(id);
        String name = lineRequest.getName();
        if (!currentLine.isSameName(name)) {
            validateDuplicateName(lineRepository.isNameExists(name), name);
        }
        lineRepository.update(new Line(id, name, lineRequest.getColor()));
    }

    public void delete(final Long id) {
        sectionRepository.deleteByLineId(id);
        lineRepository.deleteById(id);
    }
}
