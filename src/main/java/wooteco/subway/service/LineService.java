package wooteco.subway.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineRepository;
import wooteco.subway.dao.SectionRepository;
import wooteco.subway.dao.StationRepository;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.utils.exception.NameDuplicatedException;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, SectionRepository sectionRepository,
                       StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse create(final LineRequest lineRequest) {
        validateDuplicateName(lineRepository.findByName(lineRequest.getName()));
        Long id = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor()));
        Line line = new Line(id, lineRequest.getName(), lineRequest.getColor(), new Sections(Collections.emptyList()));

        Station upStation = stationRepository.findById(lineRequest.getUpStationId());
        Station downStation = stationRepository.findById(lineRequest.getDownStationId());
        Section section = new Section(line.getId(), upStation, downStation, lineRequest.getDistance());
        sectionRepository.save(section);
        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                List.of(new StationResponse(upStation),
                        new StationResponse(downStation)));
    }

    private void validateDuplicateName(final Optional<Line> line) {
        line.ifPresent(l -> {
            throw new NameDuplicatedException(NameDuplicatedException.NAME_DUPLICATE_MESSAGE);
        });
    }

    public List<LineResponse> showLines() {
        List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(line -> new LineResponse(line.getId(), line.getName(), line.getColor(),
                        sortSections(line.getSections())))
                .collect(Collectors.toList());
    }

    public LineResponse showLine(final Long id) {
        Line line = lineRepository.findById(id);
        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                sortSections(new Sections(sectionRepository.findByLineId(id)))
        );
    }

    private List<StationResponse> sortSections(final Sections sections) {
        List<StationResponse> stationResponses = new ArrayList<>();
        Station firstStation = sections.findFirstStation();
        stationResponses.add(new StationResponse(firstStation));
        while (sections.nextStation(firstStation).isPresent()) {
            firstStation = sections.nextStation(firstStation).get();
            stationResponses.add(new StationResponse(firstStation));
        }
        return stationResponses;
    }

    @Transactional
    public void update(final Long id, final LineRequest lineRequest) {
        Line currentLine = lineRepository.findById(id);
        if (!currentLine.isSameName(lineRequest.getName())) {
            validateDuplicateName(lineRepository.findByName(lineRequest.getName()));
        }
        lineRepository.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    public void delete(final Long id) {
        sectionRepository.deleteByLineId(id);
        lineRepository.deleteById(id);
    }
}
