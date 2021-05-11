package wooteco.subway.line.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wooteco.subway.line.domain.Line;
import wooteco.subway.line.domain.LineRepository;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.station.domain.Station;
import wooteco.subway.station.domain.StationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LineService {
    public static final String ERROR_SECTION_GRATER_OR_EQUALS_LINE_DISTANCE = "구간의 길이가 노선의 길이보다 크거나 같을 수 없습니다.";

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    @Autowired
    public LineService(final LineRepository lineRepository, final StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public Line create(Line line) {
        checkCreateValidation(line);
        final long id = lineRepository.save(line);

    public List<Station> getStations(Long lineId) {
        Line line = lineRepository.findById(lineId);
        List<Section> sectionList = line.getSections().toList();
        List<Station> stations = sectionList.stream()
                .map(section -> stationRepository.findById(section.getUpStationId()))
                .collect(Collectors.toList());

        Long lastStationId = sectionList.get(sectionList.size() -1).getDownStationId();
        stations.add(stationRepository.findById(lastStationId));

        return stations;

    }

    public List<Line> allLines() {
        return lineRepository.findAll();
    }

    public Line findById(final Long id) {
        return lineRepository.findById(id);
    }

    public void update(final Line line) {
        lineRepository.update(line);
    }

    public void deleteById(final Long id) {
        lineRepository.deleteById(id);
    }

    public void addSection(final Long id, final Section section) {
        Line line = findById(id);
        validateAddSection(line, section);
        lineRepository.addSection(id, section);
    }

    private void validateAddSection(Line line, Section section) {
        Sections sections = line.getSections();
        if (sections.sumSectionDistance() <= section.getDistance()) {
            throw new IllegalArgumentException(ERROR_SECTION_GRATER_OR_EQUALS_LINE_DISTANCE);
        }
    }

    private void checkCreateValidation(Line line) {
        boolean duplicated = lineRepository.findAll().contains(line);
        if (duplicated) {
            throw new IllegalArgumentException(ERROR_DUPLICATED_LINE_NAME);
        }

    }
}
