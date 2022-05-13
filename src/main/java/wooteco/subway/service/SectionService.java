package wooteco.subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.NotFoundException;

@Service
public class SectionService {

    private static final String LINE_NOT_FOUND = "존재하지 않는 노선입니다.";
    private static final String STATION_NOT_FOUND = "존재하지 않는 지하철역입니다.";
    private static final int TWO = 2;

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @Transactional
    public void insert(SectionRequest sectionRequest, Long lineId) {
        checkLineExist(lineId);
        checkStationExist(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());

        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Section inputSection = Section.of(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        sections.checkSection(inputSection);
        sections.getTargetSectionToInsert(inputSection)
                .ifPresent(targetSection -> processTargetSection(lineId, inputSection, targetSection));

        Section section = Section.of(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        sectionDao.insert(section, lineId);
    }

    private void processTargetSection(Long lineId, Section inputSection, Section targetSection) {
        Section section = targetSection.splitSection(inputSection);
        sectionDao.deleteById(targetSection.getId());
        sectionDao.insert(section, lineId);
    }

    public void delete(Long lineId, Long stationId) {
        checkLineExist(lineId);
        checkStationExist(stationId);

        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.checkCanDelete();

        int sectionDeletedNum = sectionDao.deleteByLineIdAndStationId(lineId, stationId);

        if (sectionDeletedNum == TWO) {
            Section section = sections.getMergedTargetSectionToDelete(stationId);
            sectionDao.insert(section, lineId);
        }
    }

    private void checkStationExist(Long... stationIds) {
        for (Long stationId : stationIds) {
            stationDao.findById(stationId)
                    .orElseThrow(() -> new NotFoundException(STATION_NOT_FOUND));
        }
    }

    private void checkLineExist(Long lineId) {
        lineDao.findById(lineId)
                .orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND));
    }
}
