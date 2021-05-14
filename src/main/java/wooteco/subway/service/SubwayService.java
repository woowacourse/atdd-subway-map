package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.section.Section;
import wooteco.subway.domain.section.Sections;
import wooteco.subway.domain.station.Station;
import wooteco.subway.dto.*;

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

    public LineResponse createLine(LineRequest lineRequest) {
        Line line = lineRequest.createLine();
        SectionRequest sectionRequest = lineRequest.createSectionRequest();
        Long lineId = lineDao.insert(line);
        // TODO: 추후SectionService로 이동
        createSection(lineId, sectionRequest);
        return new LineResponse(lineId, line);
    }

    public List<LineResponse> findLines() {
        List<Line> lines = lineDao.selectAll();
        return lines.stream()
                .map(LineResponse::new)
                .collect(Collectors.toList());
    }

    public LineResponse findLineWithStations(Long id) {
        Line line = lineDao.select(id);
        List<StationResponse> stationResponses = getStationsInLine(id).stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return new LineResponse(line, stationResponses);
    }

    private List<Station> getStationsInLine(Long id) {
        Sections sections = new Sections(sectionDao.selectAll(id));
        Line line = lineDao.select(id);
        List<Long> stationIds = sections.getStationIds(line.getUpwardTerminalId(), line.getDownwardTerminalId());

        return stationIds.stream()
                .map(stationId -> stationDao.select(stationId))
                .collect(Collectors.toList());
    }

    public void modifyLine(Long id, LineRequest lineRequest) {
        lineDao.update(id, lineRequest.createLine());
    }

    public void deleteLine(Long id) {
        lineDao.delete(id);
    }

    public StationResponse createStation(StationRequest stationRequest) {
        Station station = stationRequest.createStation();
        long stationId = stationDao.insert(station);
        return new StationResponse(stationId, station);
    }

    public List<StationResponse> findsStations() {
        List<Station> stations = stationDao.selectAll();
        return stations.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
    }

    public void deleteStation(Long id) {
        stationDao.delete(id);
    }

    public SectionResponse createSection(Long lineId, SectionRequest sectionRequest) {
        Section section = sectionRequest.createSection();
        updateSection(lineId, sectionRequest);
        Long sectionId = sectionDao.insert(lineId, section);
        return new SectionResponse(sectionId, lineId, section);
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

    private void updateSection(Long lineId, SectionRequest sectionRequest) {
        Section section = sectionRequest.createSection();
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
