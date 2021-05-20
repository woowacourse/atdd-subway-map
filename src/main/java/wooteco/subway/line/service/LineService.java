package wooteco.subway.line.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.rule.FindDownSectionStrategy;
import wooteco.subway.line.domain.rule.FindSectionStrategy;
import wooteco.subway.line.domain.rule.FindUpSectionStrategy;
import wooteco.subway.line.dto.LineRequest;
import wooteco.subway.line.dto.LineResponse;
import wooteco.subway.line.dto.SectionRequest;
import wooteco.subway.line.repository.LineRepository;
import wooteco.subway.station.dao.StationDao;
import wooteco.subway.station.domain.Station;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationDao stationDao;
    private final List<FindSectionStrategy> findSectionStrategies;

    public LineService(LineRepository lineRepository, StationDao stationDao) {
        this.lineRepository = lineRepository;
        this.stationDao = stationDao;
        this.findSectionStrategies = Arrays.asList(
                new FindUpSectionStrategy(), new FindDownSectionStrategy());
    }

    @Transactional
    public LineResponse save(final LineRequest lineRequest) {
        checkDuplicate(lineRequest.getName());
        Line savedLine = lineRepository.save(lineRequest.getName(), lineRequest.getColor(),
                lineRequest.getUpStationId(), lineRequest.getDownStationId(), lineRequest.getDistance());
        return LineResponse.toDto(savedLine);
    }

    private void checkDuplicate(final String name) {
        if (lineRepository.findByName(name).isPresent()) {
            throw new IllegalStateException("[ERROR] 이미 존재하는 노선입니다.");
        }
    }

    public List<LineResponse> findAll() {
        return LineResponse.toDtos(lineRepository.findAll());
    }

    public LineResponse findById(final Long id) {
        Line findLine = lineRepository.findById(id);
        List<Station> findStations = findLine.stationIds()
                .stream()
                .map(stationId -> stationDao.findById(stationId)
                        .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 역입니다.")))
                .collect(Collectors.toList());

        return LineResponse.toDto(findLine, findStations);
    }

    @Transactional
    public void update(final Long id, LineRequest lineRequest) {
        lineRepository.update(new Line(id, lineRequest.getName(), lineRequest.getColor()));
    }

    @Transactional
    public void deleteSection(final Long lineId, final Long stationId) {
        Line line = lineRepository.findById(lineId);
        checkDeleteableSectionInLine(line);
        List<Section> sections = line.sectionsWhichHasStation(new Station(stationId));
        lineRepository.deleteSection(lineId, stationId);
        if (sections.size() == 2) {
            lineRepository.addSection(
                    lineId, sections.get(0).upStation().id(), sections.get(1).downStation().id(), sections.get(0).addDistance(sections.get(1)));
        }
    }

    private void checkDeleteableSectionInLine(Line line) {
        if (line.hasOnlyOneSection()) {
            throw new IllegalStateException("[ERROR] 구간이 하나만 존재하므로 삭제할 수 없습니다.");
        }
    }

    @Transactional
    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId);
        Section toAddSection = sectionRequest.toSection(lineId);
        Station targetStation = line.registeredStation(toAddSection);
        Section targetSection = line.findSectionWithStation(targetStation, findSectionStrategies);
        lineRepository.updateSection(lineId, targetSection.updateToAdd(toAddSection));
        lineRepository.addSection(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(), sectionRequest.getDistance());
    }

    public void delete(Long id) {
        lineRepository.delete(id);
    }
}
