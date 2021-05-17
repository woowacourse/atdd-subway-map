package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
import wooteco.subway.section.domain.StationIdsInLine;
import wooteco.subway.section.dto.SectionRequest;

@Service
public class SectionServiceTemp {
    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public SectionServiceTemp(SectionDao sectionDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        final Section section = sectionRequest.toSection(lineId);

        final Sections sectionsInLine = sections(lineId);
        sectionsInLine.validateAbleToAdd(section);

        final StationIdsInLine ids = sectionsInLine.sort(lineDao.findUpStationId(lineId));
        if(ids.isFinalSection(section)){
            insertFinalSection(lineId, section, ids);
            return;
        }

        insertMiddleSection(sectionsInLine, section);
    }

    // TODO :: StationsORderInLine 불변으로 만들기
    private void insertFinalSection(final Long lineId, final Section section, final StationIdsInLine ids) {
        ids.addSection(section);
        sectionDao.save(section);
        lineDao.updateFinalStations(lineId, ids.firstStationId(), ids.lastStationId());
    }

    private void insertMiddleSection(final Sections sectionsInLine, final Section section) {
        final Section insertingSection = sectionsInLine.findSectionInclude(section);
        for (final Section sectionToSave : insertingSection.divide(section)) {
            sectionDao.save(sectionToSave);
        }
        sectionDao.delete(insertingSection);
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        final Sections sections = sections(lineId);
        sections.validateAbleToDelete(stationId);

        final StationIdsInLine ids = sections.sort(lineDao.findUpStationId(lineId));
        if(ids.isFinalStation(stationId)){
            deleteFinalStation(lineId, stationId, sections, ids);
            return;
        }

        deleteMiddleStation(sections, stationId);
    }

    private Sections sections(Long lineId) {
        validateLineExist(lineId);
        return new Sections(sectionDao.findSections(lineId));
    }

    private void deleteFinalStation(final Long lineId, final Long stationId, final Sections sectionsInLine, final StationIdsInLine ids) {
        final Section sectionToDelete = sectionsInLine.finalSectionInclude(stationId);
        sectionDao.delete(sectionToDelete);
        lineDao.updateFinalStations(lineId, ids.firstStationId(), ids.lastStationId());
    }

    private void deleteMiddleStation(final Sections sectionsInLine, final Long stationId) {
        final Section frontSection = sectionsInLine.findSectionByBackStation(stationId);
        final Section backSection = sectionsInLine.findSectionByFrontStation(stationId);

        sectionDao.delete(frontSection);
        sectionDao.delete(backSection);
        sectionDao.save(frontSection.combine(backSection));
    }

    private void validateLineExist(final Long lineId){
        if(lineDao.isNotExist(lineId)){
            throw new LineException("존재하지 않는 노선입니다.");
        }
    }
}

