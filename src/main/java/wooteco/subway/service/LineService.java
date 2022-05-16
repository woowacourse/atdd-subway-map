package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.request.CreateSectionRequest;
import wooteco.subway.dto.request.UpdateLineRequest;
import wooteco.subway.dto.response.LineResponse;
import wooteco.subway.exception.duplicate.DuplicateLineException;
import wooteco.subway.exception.notfound.NotFoundLineException;
import wooteco.subway.repository.LineRepository;
import wooteco.subway.repository.SectionRepository;

@Transactional
@Service
public class LineService {

    private final LineRepository lineRepository;
    private final SectionRepository sectionRepository;

    private final StationService stationService;

    public LineService(final LineRepository lineRepository, final SectionRepository sectionRepository,
                       StationService stationService) {
        this.lineRepository = lineRepository;
        this.sectionRepository = sectionRepository;
        this.stationService = stationService;
    }

    public LineResponse create(final CreateLineRequest request) {
        try {
            final Long lineId = lineRepository.save(request.toLine());
            final Station upStation = stationService.show(request.getUpStationId());
            final Station downStation = stationService.show(request.getDownStationId());
            sectionRepository.save(lineId, new Section(upStation, downStation, request.getDistance()));
            return show(lineId);
        } catch (final DuplicateKeyException e) {
            throw new DuplicateLineException();
        }
    }

    @Transactional(readOnly = true)
    public List<LineResponse> showAll() {
        final List<Line> lines = lineRepository.findAll();
        return lines.stream()
                .map(l -> {
                    final List<Station> stations = l.getSortedStations();
                    return LineResponse.of(l, stations);
                }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LineResponse show(final Long id) {
        final Line line = lineRepository.find(id);
        final List<Station> stations = line.getSortedStations();
        return LineResponse.of(line, stations);
    }

    public void updateLine(final Long id, final UpdateLineRequest request) {
        validateNotExistLine(id);
        lineRepository.update(request.toLine(id));
    }

    public void deleteLine(final Long id) {
        validateNotExistLine(id);
        lineRepository.deleteById(id);
    }

    public void createSection(final Long lineId, final CreateSectionRequest request) {
        validateCreateSection(lineId, request);
        final Sections originSections = sectionRepository.findAllByLineId(lineId);
        final Sections newSections = new Sections(originSections.getValues());
        final Station upStation = stationService.show(request.getUpStationId());
        final Station downStation = stationService.show(request.getDownStationId());
        newSections.add(new Section(upStation, downStation, request.getDistance()));

        deleteOldSections(lineId, originSections, newSections);
        saveNewSections(lineId, originSections, newSections);
    }

    private void validateCreateSection(final Long lineId, final CreateSectionRequest request) {
        validateNotExistLine(lineId);
        stationService.validateNotExistStation(request.getUpStationId());
        stationService.validateNotExistStation(request.getDownStationId());
    }

    private void deleteOldSections(final Long lineId, final Sections originSections, final Sections newSections) {
        final List<Section> differentSections = originSections.findDifferentSections(newSections);
        sectionRepository.batchDelete(lineId, differentSections);
    }

    private void saveNewSections(final Long lineId, final Sections originSections, final Sections newSections) {
        final List<Section> differentSections = newSections.findDifferentSections(originSections);
        sectionRepository.batchSave(lineId, differentSections);
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        validateDeleteSection(lineId, stationId);
        final Sections originSections = sectionRepository.findAllByLineId(lineId);
        final Sections newSections = new Sections(originSections.getValues());
        newSections.remove(stationId);

        deleteOldSections(lineId, originSections, newSections);
        saveNewSections(lineId, originSections, newSections);
    }

    private void validateDeleteSection(Long lineId, Long stationId) {
        validateNotExistLine(lineId);
        stationService.validateNotExistStation(stationId);
    }

    private void validateNotExistLine(final Long id) {
        if (!lineRepository.existsById(id)) {
            throw new NotFoundLineException();
        }
    }
}
