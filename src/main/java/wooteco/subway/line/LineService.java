package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionRepository;
import wooteco.subway.section.Sections;
import wooteco.subway.station.Station;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final SectionRepository sectionRepository;

    @Autowired
    public LineService(LineDao lineDao, SectionRepository sectionRepository) {
        this.lineDao = lineDao;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse createLine(long upStationId, long downStationId, String lineName, String lineColor, int distance) {
        validateStationId(upStationId, downStationId);
        validateDuplicateName(lineName);
        final Line line = lineDao.save(lineName, lineColor);
        final Sections sections = sectionRepository.save(line.getId(), upStationId, downStationId, distance);
        line.setSections(sections);
        return LineResponse.from(line);
    }

    private void validateStationId(long upStationId, long downStationId) {
        if (upStationId == downStationId) {
            throw new IllegalArgumentException("상행역과 하행역은 같은 역이 될 수 없습니다.");
        }
    }

    private void validateDuplicateName(String lineName) {
        final Optional<Line> lineWithSameName = lineDao.findByName(lineName);
        if (lineWithSameName.isPresent()) {
            throw new IllegalArgumentException("노선 이름이 중복됩니다.");
        }
    }

    public List<LineResponse> showLines() {
        final List<Line> lines = lineDao.findAll();
        for (Line line : lines) {
            final Sections sections = sectionRepository.findByLineId(line.getId());
            line.setSections(sections);
        }
        return lines.stream()
                .map(LineResponse::from)
                .collect(Collectors.toList());
    }

    public LineResponse showLine(long lineId) {
        final Line line = findLineById(lineId);
        return LineResponse.from(line);
    }

    private Line findLineById(long lineId) {
        final Line line = lineDao.findById(lineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 id에 대응하는 노선이 없습니다."));
        final Sections sections = sectionRepository.findByLineId(lineId);
        line.setSections(sections);
        return line;
    }

    public void updateLine(long lineId, String lineName, String lineColor) {
        final Line line = findLineById(lineId);
        lineDao.update(line.getId(), lineName, lineColor);
    }

    public void deleteLine(long lineId) {
        final Line line = findLineById(lineId);
        lineDao.delete(line.getId());
        sectionRepository.deleteLine(line.getId());
    }

    public void createSection(long lineId, long upStationId, long downStationId, int distance) {
        final Line line = findLineById(lineId);
        final Section newSection = sectionRepository.createSection(line.getId(), upStationId, downStationId, distance);
        if (line.checkSectionAtEdge(newSection)) {
            line.insertSectionAtEdge(newSection);
            saveSection(newSection);
            return;
        }
        createSectionInBetween(lineId, line, newSection);
    }

    private void saveSection(Section section) {
        final Long lineId = section.getLineId();
        final Long upStationId = section.getUpStation().getId();
        final Long downStationId = section.getDownStation().getId();
        final int distance = section.getDistance();
        sectionRepository.save(lineId, upStationId, downStationId, distance);
    }

    private void createSectionInBetween(long lineId, Line line, Section newSection) {
        Map<Section, Section> changedSections = line.insertSectionInBetween(newSection);
        final Section upperSection = changedSections.keySet().iterator().next();
        final Section lowerSection = changedSections.get(upperSection);

        saveSection(upperSection);
        saveSection(lowerSection);
        sectionRepository.deleteSection(lineId, upperSection.getUpStation().getId(), lowerSection.getDownStation().getId());
    }

    public void deleteSection(long lineId, long stationId) {
        final Line line = findLineById(lineId);
        final Station station = sectionRepository.findStationById(stationId);
        if (line.checkSectionAtEdge(station)) {
            Section section = line.removeSectionAtEdge(station);
            deleteSection(section);
            return;
        }
        deleteSectionInBetween(line, station);
    }

    private void deleteSectionInBetween(Line line, Station station) {
        Map<Section, Map<Section, Section>> sectionsToRemove = line.removeSectionInBetween(station);
        final Section sectionToSave = sectionsToRemove.keySet().iterator().next();

        final Map<Section, Section> sectionsToDelete = sectionsToRemove.get(sectionToSave);
        final Section upperSectionToDelete = sectionsToDelete.keySet().iterator().next();
        final Section lowerSectionToDelete = sectionsToDelete.get(upperSectionToDelete);

        saveSection(sectionToSave);
        deleteSection(upperSectionToDelete);
        deleteSection(lowerSectionToDelete);
    }

    private void deleteSection(Section section) {
        final Long lineId = section.getLineId();
        final Long upStationId = section.getUpStation().getId();
        final Long downStationId = section.getDownStation().getId();
        sectionRepository.deleteSection(lineId, upStationId, downStationId);
    }
}
