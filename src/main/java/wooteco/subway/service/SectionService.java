package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Service
public class SectionService {
    private static final String NO_STATION_ID_ERROR_MESSAGE = "해당 아이디의 역을 찾을 수 없습니다.";
    private static final String NO_LINE_ID_ERROR_MESSAGE = "해당 아이디의 노선을 찾을 수 없습니다.";
    private static final String NO_SECTION_ID_ERROR_MESSAGE = "해당 아이디의 구간을 찾을 수 없습니다.";

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public Section save(Section section) {
        checkLineExist(section.getLineId());
        checkStationsExist(section);
        Sections sections = new Sections(sectionDao.findAllByLineId(section.getLineId()));
        sections.validateSave(section);
        if (sections.isMiddle(section)) {
            Section base = sections.findMiddleBase(section);
            sectionDao.save(base.calculateRemainSection(section));
            sectionDao.delete(base.getId());
        }
        return sectionDao.findById(sectionDao.save(section));
    }

    public Section findById(Long id) {
        checkSectionExist(id);
        return sectionDao.findById(id);
    }

    public List<Station> findStationsOfLine(Long lineId) {
        checkLineExist(lineId);
        return new Sections(sectionDao.findAllByLineId(lineId)).calculateStations();
    }

    public void delete(Long lineId, Long stationId) {
        checkLineExist(lineId);
        checkStationExist(stationId);
        Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
        sections.validateDelete();
        Station station = stationDao.findById(stationId);
        if (sections.isSide(station)) {
            sectionDao.delete(sections.findSide(station).getId());
            return;
        }
        List<Section> linkedSections = sections.findByStation(station);
        sectionDao.save(new Sections(linkedSections).calculateCombinedSection(station));
        sectionDao.deleteAllBySections(linkedSections);
    }

    private void checkSectionExist(Long id) {
        if (!sectionDao.hasSection(id)) {
            throw new IllegalArgumentException(NO_SECTION_ID_ERROR_MESSAGE);
        }
    }

    private void checkLineExist(Long lineId) {
        if (!lineDao.hasLine(lineId)) {
            throw new IllegalArgumentException(NO_LINE_ID_ERROR_MESSAGE);
        }
    }

    private void checkStationExist(Long stationId) {
        if (!stationDao.hasStation(stationId)) {
            throw new IllegalArgumentException(NO_STATION_ID_ERROR_MESSAGE);
        }
    }

    private void checkStationsExist(Section section) {
        if (!stationDao.hasValidStations(section)) {
            throw new IllegalArgumentException(NO_STATION_ID_ERROR_MESSAGE);
        }
    }
}
