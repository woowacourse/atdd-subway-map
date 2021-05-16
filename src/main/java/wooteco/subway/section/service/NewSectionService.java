package wooteco.subway.section.service;

import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.FinalStations;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.domain.Sections;

import java.util.LinkedList;
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
        // TODO :: 유효성 검증 :: 존재하는 노선인지

        final FinalStations finalStations = lineDao.finalStations(lineId);

        if (finalStations.isFinalSection(section)) {
            insertFinalSection(lineId, section);
            return;
        }

        insertMiddleSection(lineId, section);
    }

    private void insertMiddleSection(final Long lineId, final Section section) {
        final Sections sectionsInLine = new Sections(sectionDao.findSections(lineId));
        final Section insertingSection = sectionsInLine.findInsertingSection(section);

        for(Section sectionToSave : insertingSection.devide(section)){
            sectionDao.save(lineId, sectionToSave);
        }
        sectionDao.delete(insertingSection);
    }

    private void insertFinalSection(final Long lineId, final Section section){
        final FinalStations finalStations = lineDao.finalStations(lineId);

        sectionDao.save(lineId, section);
        lineDao.updateFinalStations(lineId, finalStations.addStations(section));
    }

    @Override
    public void addSection(Long lineId, Long front, Long back, int distance) {
        // TODO :: 변경을 위해 남겨둠
        addSection(lineId, new Section(front, back, distance));
    }

    @Override
    public void deleteSection(Long lineId, Long stationId) {

    }

    public List<Long> orders(final Long lineId) {
        Long frontStationId = lineDao.findUpStationId(lineId);
        Long downStationId = lineDao.findDownStationId(lineId);

        final List<Long> stations = new LinkedList<>();
        while (!frontStationId.equals(downStationId)) {
            stations.add(frontStationId);
            Section next = sectionDao.findSectionByFrontStation(lineId, frontStationId);
            frontStationId = next.backStationId();
        }
        stations.add(frontStationId);
        return stations;
    }
}
