//package wooteco.subway.section.service;
//
//import org.springframework.stereotype.Service;
//import wooteco.subway.line.dao.LineDao;
//import wooteco.subway.line.domain.FinalStations;
//import wooteco.subway.line.exception.LineException;
//import wooteco.subway.section.dao.SectionDao;
//import wooteco.subway.section.domain.Section;
//import wooteco.subway.section.domain.Sections;
//import wooteco.subway.section.dto.SectionRequest;
//
//import java.util.List;
//
//@Service
//public class NewSectionService {
//    private final SectionDao sectionDao;
//    private final LineDao lineDao;
//
//    public NewSectionService(SectionDao sectionDao, LineDao lineDao) {
//        this.sectionDao = sectionDao;
//        this.lineDao = lineDao;
//    }
//
//    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
//        validateLineExist(lineId);
//        final Section section = sectionRequest.toSection(lineId);
//
//        final Sections sectionsInLine = new Sections(sectionDao.findSections(lineId));
//        sectionsInLine.validateAbleToAdd(section);
//
//        final FinalStations finalStations = lineDao.finalStations(lineId);
//        if (finalStations.isFinalSection(section)) {
//            insertFinalSection(lineId, section, finalStations);
//            return;
//        }
//
//        insertMiddleSection(sectionsInLine, section);
//    }
//
//    private void insertFinalSection(final Long lineId, final Section section, final FinalStations finalStations) {
//        sectionDao.save(section);
//        lineDao.updateFinalStations(lineId, finalStations.addStations(section));
//    }
//
//    private void insertMiddleSection(final Sections sectionsInLine, final Section section) {
//        final Section insertingSection = sectionsInLine.findSectionInclude(section);
//        for (final Section sectionToSave : insertingSection.divide(section)) {
//            sectionDao.save(sectionToSave);
//        }
//        sectionDao.delete(insertingSection);
//    }
//
//    public void deleteSection(final Long lineId, final Long stationId) {
//        validateLineExist(lineId);
//
//        final Sections sectionsInLine = new Sections(sectionDao.findSections(lineId));
//        sectionsInLine.validateAbleToDelete(stationId);
//
//        final FinalStations finalStations = lineDao.finalStations(lineId);
//        if (finalStations.isFinalStation(stationId)) {
//            deleteFinalSection(lineId, stationId, finalStations);
//            return;
//        }
//
//        deleteMiddleSection(sectionsInLine, stationId);
//    }
//
//    private void deleteFinalSection(final Long lineId, final Long stationId, final FinalStations finalStations) {
//        if (finalStations.isUpStation(stationId)) {
//            deleteUpStation(lineId, stationId, finalStations);
//            return;
//        }
//
//        if (finalStations.isDownStation(stationId)) {
//            deleteDownStation(lineId, stationId, finalStations);
//            return;
//        }
//    }
//
//    private void deleteUpStation(final Long lineId, final Long stationId, final FinalStations finalStations) {
//        final Section section = sectionDao.findSectionByFrontStation(lineId, stationId);
//        lineDao.updateFinalStations(lineId, section.getOther(stationId), finalStations.downStationId());
//        sectionDao.delete(section);
//    }
//
//    private void deleteDownStation(final Long lineId, final Long stationId, final FinalStations finalStations) {
//        final Section section = sectionDao.findSectionByBackStation(lineId, stationId);
//        lineDao.updateFinalStations(lineId, finalStations.upStationId(), section.getOther(stationId));
//        sectionDao.delete(section);
//    }
//
//    private void deleteMiddleSection(final Sections sectionsInLine, final Long stationId) {
//        final Section frontSection = sectionsInLine.findSectionByBackStation(stationId);
//        final Section backSection = sectionsInLine.findSectionByFrontStation(stationId);
//
//        sectionDao.delete(frontSection);
//        sectionDao.delete(backSection);
//        sectionDao.save(frontSection.combine(backSection));
//    }
//
//    private void validateLineExist(final Long lineId){
//        if(lineDao.isNotExist(lineId)){
//            throw new LineException("존재하지 않는 노선입니다.");
//        }
//    }
//}
