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

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(SectionDao dao, StationDao stationDao, LineDao lineDao) {
        this.sectionDao = dao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    @Transactional
    public void insert(SectionRequest sectionRequest, Long lineId) {
        checkSectionAndLineExist(sectionRequest, lineId);

        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Section inputSection = Section.of(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        sections.checkSection(inputSection);

        sections.getTargetSection(inputSection)
                .ifPresent(targetSection -> processTargetSection(lineId, inputSection, targetSection));

        sectionDao.insert(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance(), lineId);
    }

    private void checkSectionAndLineExist(SectionRequest sectionRequest, Long lineId) {
        lineDao.findById(lineId)
                .orElseThrow(() -> new NotFoundException(LINE_NOT_FOUND));

        stationDao.findById(sectionRequest.getUpStationId())
                .orElseThrow(() -> new NotFoundException(STATION_NOT_FOUND));

        stationDao.findById(sectionRequest.getDownStationId())
                .orElseThrow(() -> new NotFoundException(STATION_NOT_FOUND));
    }

    private void processTargetSection(Long lineId, Section inputSection, Section targetSection) {
        targetSection.checkDistanceIsLongerThan(inputSection);

        Section newSection = getNewSection(inputSection, targetSection);
        sectionDao.deleteById(targetSection.getId());
        sectionDao.insert(newSection, lineId);
    }

    private Section getNewSection(Section inputSection, Section targetSection) {
        int newDistance = targetSection.getDistance() - inputSection.getDistance();

        if (targetSection.isSameUpStationId(inputSection)) {
            return Section.of(inputSection.getDownStationId(), targetSection.getDownStationId(), newDistance);
        }
        return Section.of(targetSection.getUpStationId(), inputSection.getUpStationId(), newDistance);
    }
}
