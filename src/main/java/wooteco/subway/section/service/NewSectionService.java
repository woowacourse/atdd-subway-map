package wooteco.subway.section.service;

import wooteco.subway.line.dao.LineDao;
import wooteco.subway.line.domain.FinalStations;
import wooteco.subway.line.exception.LineException;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Order;
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

    // TODO :: Ordered sections만 만들어 준다면 코드가 더 줄겠다.

    @Override
    public void addSection(final Long lineId, final Section section) {
        // TODO :: 유효성 검증 :: 존재하는 노선인지
        if(lineDao.isNotExist(lineId)){
            throw new LineException("존재하지 않는 노선입니다.");
        }

        final FinalStations finalStations = lineDao.finalStations(lineId);
        if(finalStations.isFinalSection(section)){
            insertFinalSection(lineId, section, finalStations);
            return;
        }

        if(order.isInMiddle){
            insertMiddleSection(lineId, section);
        }

    }

    // TODO :: Dao에서 배열로 save, delete해도 될까?
    // sectionDao.save(sectionsToDelete);

    private void insertFinalSection(final Long lineId, final Section section, final FinalStations finalStations){
        sectionDao.save(section);
        lineDao.updateFinalStations(lineId, finalStations.addStations(section));
    }

    private void insertMiddleSection(final Long lineId, final Section section) {
        final Sections sectionsInLine = new Sections(sectionDao.findSections(lineId));
        final Section insertingSection = sectionsInLine.findSectionInclude(section);

        for(Section sectionToSave : insertingSection.devide(section)){
            sectionDao.save(sectionToSave);
        }
        sectionDao.delete(insertingSection);
    }



    @Override
    public void deleteSection(final Long lineId, final Long stationId) {
        // TODO :: 유효성 검증 :: 존재하는 노선인지, 노선 위에 존재하는 역인지

        final Sections sectionsInLine = new Sections(sectionDao.findSections(lineId));
        final List<Section> sections = sectionsInLine.sectionsIncludeStation(stationId);
        final FinalStations finalStations = lineDao.finalStations(lineId);

        if(finalStations.isFinalStation(stationId)){
            final Section sectionToDelete = sections.get(0);
            final FinalStations newFinalStations = finalStations.change(sectionToDelete.getOther(stationId), stationId);
            deleteFinalSection(lineId, sectionToDelete, newFinalStations);
            return;
        }

        deleteMiddleSection(sections.get(0), sections.get(1));
    }

    private void deleteFinalSection(final Long lineId, final Section section, final FinalStations finalStations) {
        lineDao.updateFinalStations(lineId, finalStations);
        sectionDao.delete(section);
    }

    // TODO :: 변수명 질문하기, section1, section2 이런식으로 변수명을 쓰기도 하나
    private void deleteMiddleSection(final Section section1, final Section section2) {
        sectionDao.delete(section1);
        sectionDao.delete(section2);
        sectionDao.save(section1.combine(section2));
    }

    @Override
    public void addSection(Long lineId, Long front, Long back, int distance) {
        // TODO :: 변경을 위해 남겨둠
        addSection(lineId, new Section(front, back, distance));
    }
}
