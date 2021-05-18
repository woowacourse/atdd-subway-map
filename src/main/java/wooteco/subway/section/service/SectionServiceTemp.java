package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.FinalStations;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;
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
        validateLineExist(lineId);
        final Section section = sectionRequest.toSection(lineId);

        final Sections sectionsInLine = new Sections(sectionDao.findSections(lineId));
        sectionsInLine.validateAbleToAdd(section);

        final FinalStations finalStations = lineDao.finalStations(lineId);
        if (finalStations.isFinalSection(section)) {
            insertFinalSection(lineId, section, finalStations.addSection(section));
            return;
        }

        insertMiddleSection(sectionsInLine, section);
    }

    private void insertFinalSection(final Long lineId, final Section section, final FinalStations finalStations) {
        sectionDao.save(section);
        lineDao.updateFinalStations(lineId, finalStations);
    }

    private void insertMiddleSection(final Sections sectionsInLine, final Section section) {
        final Section insertingSection = sectionsInLine.findSectionInclude(section);
        for (final Section sectionToSave : insertingSection.divide(section)) {
            sectionDao.save(sectionToSave);
        }
        sectionDao.delete(insertingSection);
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        validateLineExist(lineId);

        final Sections sections = new Sections(sectionDao.findSections(lineId));
        sections.validateAbleToDelete(stationId);

        final FinalStations finalStations = lineDao.finalStations(lineId);
        if (finalStations.isFinalStation(stationId)) {
            final Section sectionToDelete = sections.finalSectionInclude(stationId);
            deleteFinalStation(lineId, sectionToDelete, finalStations);
            return;
        }

        deleteMiddleStation(sections, stationId);
    }

    private void deleteFinalStation(final Long lineId, final Section section, final FinalStations finalStations) {
        sectionDao.delete(section);
        lineDao.updateFinalStations(lineId, finalStations.deleteSection(section));
    }

    private void deleteMiddleStation(final Sections sectionsInLine, final Long stationId) {
        final Section frontSection = sectionsInLine.findSectionByBackStation(stationId);
        final Section backSection = sectionsInLine.findSectionByFrontStation(stationId);

        sectionDao.delete(frontSection);
        sectionDao.delete(backSection);
        sectionDao.save(frontSection.combine(backSection));
    }

    private void validateLineExist(final Long lineId) {
        if (lineDao.isNotExist(lineId)) {
            throw new LineException("존재하지 않는 노선입니다.");
        }
    }
}

