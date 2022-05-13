package wooteco.subway.service;

import java.util.List;
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
        checkLineExist(lineId);
        checkStationExist(sectionRequest.getUpStationId(), sectionRequest.getDownStationId());

        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        Section inputSection = Section.of(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());

        sections.checkSection(inputSection);
        sections.getTargetSection(inputSection)
                .ifPresent(targetSection -> processTargetSection(lineId, inputSection, targetSection));

        Section section = Section.of(sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        sectionDao.insert(section, lineId);
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

    public void delete(Long lineId, Long stationId) {
        checkLineExist(lineId);
        checkStationExist(stationId);

        Sections sections = new Sections(sectionDao.findByLineId(lineId));
        sections.checkCanDelete();

        List<Section> targetStations = sections.findByStationId(stationId);

        sectionDao.deleteByLineIdAndStationId(lineId, stationId);

        if (targetStations.size() == 2) {
            Section combinedSection = combineSection(stationId, targetStations);
            sectionDao.insert(combinedSection, lineId);
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

    private Section combineSection(Long stationId, List<Section> targetStations) {
        int newDistance = targetStations.stream()
                .mapToInt(Section::getDistance)
                .sum();

        Section firstSection = targetStations.get(0);
        Section secondSection = targetStations.get(1);

        if (firstSection.getDownStationId().equals(stationId)) {
            return Section.of(firstSection.getUpStationId(), secondSection.getDownStationId(), newDistance);
        }
        return Section.of(secondSection.getUpStationId(), firstSection.getDownStationId(), newDistance);
    }
}
