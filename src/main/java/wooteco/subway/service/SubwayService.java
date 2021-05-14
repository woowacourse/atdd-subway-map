package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubwayService {
    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SubwayService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Long createLine(Line line) {
        return lineDao.insert(line);
    }

    public List<Line> showLines() {
        return lineDao.selectAll();
    }

    public Line showLineDetail(Long id) {
        Line line = lineDao.select(id);
        return line;
    }

    public List<Station> getStationsInLine(Long id) {
        Sections sections = new Sections(sectionDao.selectAll(id));
        Line line = lineDao.select(id);
        List<Long> stationIds = sections.getStationIds(line.getUpwardTerminalId(), line.getDownwardTerminalId());

        return stationIds.stream()
                .map(stationId -> stationDao.select(stationId))
                .collect(Collectors.toList());
    }

    public void modifyLine(Long id, Line line) {
        lineDao.update(id, line);
    }

    public void deleteLine(Long id) {
        lineDao.delete(id);
    }

    public Long createStation(Station station) {
        return stationDao.insert(station);
    }

    public List<Station> showStations() {
        return stationDao.selectAll();
    }

    public void deleteStation(Long id) {
        stationDao.delete(id);
    }

    public Long insertSection(Long lineId, Section section) {
        return sectionDao.insert(lineId, section);
    }

    public void deleteAdjacentSectionByStationId(Long lineId, Long stationId) {
        Sections sections = new Sections(sectionDao.selectAll(lineId));
        sections.validateIfPossibleToDelete();
        Optional<Section> downwardSection = sectionDao.selectDownwardSection(lineId, stationId);
        Optional<Section> upwardSection = sectionDao.selectUpwardSection(lineId, stationId);

        if (upwardSection.isPresent() && downwardSection.isPresent()) {
            sectionDao.delete(lineId, stationId);
            int newSectionDistance = upwardSection.get().getDistance() + downwardSection.get().getDistance();
            sectionDao.insert(lineId, new Section(upwardSection.get().getUpStationId(), downwardSection.get().getDownStationId(), newSectionDistance));
            return;
        }

        if (upwardSection.isPresent()) {
            sectionDao.deleteBottomSection(lineId, upwardSection.get());
            lineDao.updateDownwardTerminalId(lineId, upwardSection.get().getUpStationId());
        }

        if (downwardSection.isPresent()) {
            sectionDao.deleteTopSection(lineId, downwardSection.get());
            lineDao.updateUpwardTerminalId(lineId, downwardSection.get().getDownStationId());
        }
    }

    public void updateSection(Long lineId, Section section) {
        Sections sections = new Sections(sectionDao.selectAll(lineId));
        Line line = lineDao.select(lineId);
        sections.validateIfPossibleToInsert(section, line.getUpwardTerminalId(), line.getDownwardTerminalId());

        if (isSideInsertion(section, line)) {
            processSideInsertion(lineId, line, section);
            return;
        }

        if (sections.isNewStationDownward(section)) {
            sectionDao.updateWhenNewStationDownward(lineId, section);
        }
        sectionDao.updateWhenNewStationUpward(lineId, section);
    }

    private boolean isSideInsertion(Section section, Line line) {
        if (section.getDownStationId() == line.getUpwardTerminalId()) {
            return true;
        }
        return section.getUpStationId() == line.getDownwardTerminalId();
    }

    private void processSideInsertion(Long lineId, Line line, Section section) {
        if (section.getDownStationId() == line.getUpwardTerminalId()) {
            lineDao.updateUpwardTerminalId(lineId, section.getUpStationId());
        }

        if (section.getUpStationId() == line.getDownwardTerminalId()) {
            lineDao.updateDownwardTerminalId(lineId, section.getDownStationId());
        }
    }
}
