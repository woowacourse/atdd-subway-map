package wooteco.subway.section.service;

import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.FinalStations;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;

import java.util.List;

public class NewSectionService implements ISectionService {
    private static final int LIMIT_NUMBER_OF_STATION_IN_LINE = 2;

    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public NewSectionService(SectionDao sectionDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    @Override
    public void addSection(final Long lineId, final Section section) {
        final FinalStations finalStations = lineDao.finalStations(lineId);

        if (finalStations.isFinalSection(section)) {
            insertFinalSection(lineId, section, finalStations);
            return;
        }

        insertMiddleSection(lineId, section);
    }

    private void insertFinalSection(final Long lineId, final Section section, final FinalStations finalStations) {
        sectionDao.save(section);
        lineDao.updateFinalStations(lineId, finalStations.addStations(section));
    }

    private void insertMiddleSection(final Long lineId, final Section section) {
        final Sections sectionsInLine = new Sections(sectionDao.findSections(lineId));
        final Section insertingSection = sectionsInLine.findSectionInclude(section);

        for (final Section sectionToSave : insertingSection.divide(section)) {
            sectionDao.save(sectionToSave);
        }
        sectionDao.delete(insertingSection);
    }

    @Override
    public void deleteSection(final Long lineId, final Long stationId) {
        final FinalStations finalStations = lineDao.finalStations(lineId);

        if (finalStations.isFinalStation(stationId)) {
            deleteFinalSection(lineId, stationId, finalStations);
            return;
        }

        deleteMiddleSection(lineId, stationId);
    }

    private void deleteFinalSection(final Long lineId, final Long stationId, final FinalStations finalStations) {
        if (finalStations.isUpStation(stationId)) {
            final Section section = sectionDao.findSectionByFrontStation(lineId, stationId);
            lineDao.updateFinalStations(lineId, section.getOther(stationId), finalStations.downStationId());
            sectionDao.delete(section);
            return;
        }

        if (finalStations.isDownStation(stationId)) {
            final Section section = sectionDao.findSectionByBackStation(lineId, stationId);
            lineDao.updateFinalStations(lineId, finalStations.upStationId(), section.getOther(stationId));
            sectionDao.delete(section);
            return;
        }
    }

    private void deleteMiddleSection(final Long lineId, final Long stationId) {
        final Sections sectionsInLine = new Sections(sectionDao.findSections(lineId));
        final List<Section> sections = sectionsInLine.sectionsIncludeStation(stationId);

        sectionDao.delete(sections.get(0));
        sectionDao.delete(sections.get(1));
        sectionDao.save(sections.get(0).combine(sections.get(1)));
    }
}
