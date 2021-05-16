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

    // TODO :: Ordered sections만 만들어 준다면 코드가 더 줄겠다.
    private void insertMiddleSection(final Long lineId, final Section section) {
        final Sections sectionsInLine = new Sections(sectionDao.findSections(lineId));
        final Section insertingSection = sectionsInLine.findSectionInclude(section);

        // TODO :: Dao에서 배열로 save, delete해도 될까?
        // sectionDao.save(sectionsToDelete);

        for(Section sectionToSave : insertingSection.devide(section)){
            sectionDao.save(sectionToSave);
        }
        sectionDao.delete(insertingSection);
    }

    private void insertFinalSection(final Long lineId, final Section section){
        final FinalStations finalStations = lineDao.finalStations(lineId);

        sectionDao.save(section);
        lineDao.updateFinalStations(lineId, finalStations.addStations(section));
    }

    @Override
    public void deleteSection(final Long lineId, final Long stationId) {
        // TODO :: 유효성 검증 :: 존재하는 노선인지, 노선 위에 존재하는 역인지

        final Sections sectionsInLine = new Sections(sectionDao.findSections(lineId));
        final List<Section> sections = sectionsInLine.sectionsIncludeStation(stationId);
        final FinalStations finalStations = lineDao.finalStations(lineId);

        if(finalStations.isFinalSection())
        if(sections.size() == 1){

            final FinalStations updatedFinalStations = finalStations.changeFinalStation(sections.get(0), stationId);

            lineDao.updateFinalStations(lineId, updatedFinalStations);
            sectionDao.delete(sections.get(0));
            return;
        }

        if(sections.size() == 2){
            final Section combine = Section.combine(sections.get(0), sections.get(1));
            sectionDao.delete(sections.get(0));
            sectionDao.delete(sections.get(1));
            sectionDao.save(combine);
        }
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

    @Override
    public void addSection(Long lineId, Long front, Long back, int distance) {
        // TODO :: 변경을 위해 남겨둠
        addSection(lineId, new Section(front, back, distance));
    }
}
