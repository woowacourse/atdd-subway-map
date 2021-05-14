package wooteco.subway.line;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.section.Section;
import wooteco.subway.section.SectionDao;
import wooteco.subway.section.SectionDbDto;
import wooteco.subway.section.Sections;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    @Autowired
    public LineService(LineDao lineDao, StationDao stationDao, SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public LineResponse createLine(long upStationId, long downStationId, String lineName, String lineColor, int distance) {
        validateDuplicateName(lineName);
        Line line = lineDao.save(lineName, lineColor);

        final Station upStation = findStationById(upStationId);
        final Station downStation = findStationById(downStationId);
        final SectionDbDto sectionDbDto = sectionDao.save(line.getId(), upStation.getId(), downStation.getId(), distance);
        final Section section = generateSection(sectionDbDto);
        final Sections sections = new Sections(line.getId(), Collections.singletonList(section));

        line.setSections(sections);
        return LineResponse.from(line);
    }

    private void validateDuplicateName(String lineName) {
        final Optional<Line> lineWithSameName = lineDao.findByName(lineName);
        if (lineWithSameName.isPresent()) {
            throw new IllegalArgumentException("노선 이름이 중복됩니다.");
        }
    }

    private Station findStationById(long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
    }

    public Section generateSection(SectionDbDto sectionDbDto) {
        final Station upStation = findStationById(sectionDbDto.getUpStationId());
        final Station downStation = findStationById(sectionDbDto.getDownStationId());
        return new Section(sectionDbDto.getLineId(), upStation, downStation, sectionDbDto.getDistance());
    }

    public List<LineResponse> showLines() {
        final List<Line> lines = lineDao.findAll();
        for (Line line : lines) {
            final Sections sections = findSectionsInLine(line.getId());
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
        final Sections sections = findSectionsInLine(lineId);
        line.setSections(sections);
        return line;
    }

    private Sections findSectionsInLine(long lineId) {
        final List<Section> sectionList = new ArrayList<>();
        final List<SectionDbDto> sectionDbDtoList = sectionDao.findByLineId(lineId);
        for (SectionDbDto sectionDbDto : sectionDbDtoList) {
            final Section section = generateSection(sectionDbDto);
            sectionList.add(section);
        }
        return new Sections(lineId, sectionList);
    }

    public void updateLine(long lineId, String lineName, String lineColor) {
        final Line line = findLineById(lineId);
        lineDao.update(line.getId(), lineName, lineColor);
    }

    public void deleteLine(long lineId) {
        final Line line = findLineById(lineId);
        lineDao.delete(line.getId());
        sectionDao.deleteLine(line.getId());
    }

    public void createSection(long lineId, long upStationId, long downStationId, int distance) {
        final Line line = findLineById(lineId);
        final Station upStation = findStationById(upStationId);
        final Station downStation = findStationById(downStationId);
        final Section newSection = new Section(lineId, upStation, downStation, distance);

        if (line.checkSectionAtEdge(newSection)) {
            line.insertSectionAtEdge(newSection);
            sectionDao.save(lineId, upStationId, downStationId, distance);
            return;
        }
        createSectionInBetween(lineId, line, newSection);
    }

    private void createSectionInBetween(long lineId, Line line, Section newSection) {
        Map<Section, Section> changedSections = line.insertSectionInBetween(newSection);
        final Section upperSection = changedSections.keySet().iterator().next();
        saveSection(upperSection);
        final Section lowerSection = changedSections.get(upperSection);
        saveSection(lowerSection);
        sectionDao.deleteSection(lineId, upperSection.getUpStation().getId(), lowerSection.getDownStation().getId());
    }

    private void saveSection(Section section) {
        final Long lineId = section.getLineId();
        final Long upStationId = section.getUpStation().getId();
        final Long downStationId = section.getDownStation().getId();
        final int distance = section.getDistance();
        sectionDao.save(lineId, upStationId, downStationId, distance);
    }

    public void deleteSection(long lineId, long stationId) {
        final Line line = findLineById(lineId);
        final Station station = findStationById(stationId);
        if (line.checkSectionAtEdge(station)) {
            Section section = line.removeSectionAtEdge(station);
            sectionDao.deleteSection(lineId, section.getUpStation().getId(), section.getDownStation().getId());
            return;
        }
        deleteSectionInBetween(line, station);
    }

    private void deleteSectionInBetween(Line line, Station station) {
        Map<Section, Map<Section, Section>> sectionsToRemove = line.removeSectionInBetween(station);
        final Section sectionToSave = sectionsToRemove.keySet().iterator().next();
        saveSection(sectionToSave);

        final Map<Section, Section> sectionsToDelete = sectionsToRemove.get(sectionToSave);
        final Section upperSectionToDelete = sectionsToDelete.keySet().iterator().next();
        final Section lowerSectionToDelete = sectionsToDelete.get(upperSectionToDelete);
        deleteSectionFromDB(upperSectionToDelete);
        deleteSectionFromDB(lowerSectionToDelete);
    }

    private void deleteSectionFromDB(Section section) {
        final Long lineId = section.getLineId();
        final Long upStationId = section.getUpStation().getId();
        final Long downStationId = section.getDownStation().getId();
        sectionDao.deleteSection(lineId, upStationId, downStationId);
    }
}
