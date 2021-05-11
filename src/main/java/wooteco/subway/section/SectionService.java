//package wooteco.subway.section;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import wooteco.subway.line.FinalStations;
//import wooteco.subway.line.LineDao;
//import wooteco.subway.line.LineException;
//
//import java.util.Arrays;
//import java.util.Optional;
//
//@Service
//@Transactional
//public class SectionServiceTemp {
//    private final int LIMIT_NUMBER_OF_STATION_IN_LINE = 2;
//
//    private final SectionDao sectionDao;
//    private final LineDao lineDao;
//
//    public SectionServiceTemp(final SectionDao sectionDao, final LineDao lineDao) {
//        this.sectionDao = sectionDao;
//        this.lineDao = lineDao;
//    }
//
//    public void addSection(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
//        if (isFinalSection(lineId, upStationId, downStationId)) {
//            addFinalSection(lineId, upStationId, downStationId, distance);
//            return;
//        }
//
//        if (isMiddleSection(lineId, upStationId, downStationId)) {
//            addMiddleSection(lineId, upStationId, downStationId, distance);
//            return;
//        }
//
//        throw new LineException("잘못된 구간 입력입니다.");
//    }
//
//    private boolean isFinalSection(final Long lineId, final Long upStationId, final Long downStationId) {
//        final FinalStations finalStations = new FinalStations(lineDao.findUpStationId(lineId), lineDao.findDownStationId(lineId));
//        return finalStations.isFinalSection(upStationId, downStationId);
//    }
//
//    private boolean isMiddleSection(final Long lineId, final Long upStation, final Long downStation) {
//        return sectionDao.isExistingUpStation(lineId, upStation) != sectionDao.isExistingDownStation(lineId, downStation);
//    }
//
//    private void addFinalSection(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
//        final FinalStations before = new FinalStations(lineDao.findUpStationId(lineId), lineDao.findDownStationId(lineId));
//        final FinalStations after = before.addStations(upStationId, downStationId);
//
//        lineDao.update(lineId, after.getUpStationId(), after.getDownStationId());
//        sectionDao.save(lineId, upStationId, downStationId, distance);
//    }
//
//    private void addMiddleSection(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
//        if(sectionDao.isExistingUpStation(lineId, upStationId)){
//            final Long beforeDownStation = sectionDao.downStationIdOf(lineId, upStationId);
//            final Distance beforeDistance = distance(lineId, upStationId, beforeDownStation);
//            sectionDao.updateDownStation(lineId,  beforeDownStation, downStationId, distance);
//            sectionDao.save(lineId, downStationId,  beforeDownStation, beforeDistance.sub(distance).value());
//            return;
//        }
//
//        final Long beforeUpStation = sectionDao.upStationIdOf(lineId, downStationId);
//        final Distance beforeDistance = distance(lineId, beforeUpStation, downStationId);
//        sectionDao.updateUpStation(lineId, beforeUpStation, upStationId, distance);
//        sectionDao.save(lineId, beforeUpStation, upStationId, beforeDistance.sub(distance).value());
//    }
//
//    public void deleteSection(final Long lineId, final Long stationId) {
//        validateDeleteSection(lineId, stationId);
//
//        if(isFinalSection(lineId, stationId, stationId)){
//            deleteFinalStation(lineId, stationId);
//            return;
//        }
//
//        deleteMiddleSection(lineId, stationId);
//    }
//
//    private void deleteMiddleSection(Long lineId, Long stationId) {
//        final Long upStationId = sectionDao.upStationIdOf(lineId, stationId);
//        final Long downStationId = sectionDao.downStationIdOf(lineId, stationId);
//
//        final Distance distance = distance(lineId, upStationId, stationId, downStationId);
//        sectionDao.updateDownStation(lineId, stationId, downStationId, distance.value());
//        sectionDao.deleteSection(lineId, stationId, downStationId);
//    }
//
//    private void deleteFinalStation(Long lineId, Long stationId) {
//        if (lineDao.isUpStation(lineId, stationId)) {
//            final Long downStationId = sectionDao.downStationIdOf(lineId, stationId);
//            lineDao.updateUpStation(lineId, downStationId);
//            sectionDao.deleteSection(lineId, stationId, downStationId);
//        }
//
//        if (lineDao.isDownStation(lineId, stationId)) {
//            final Long upStationId = sectionDao.upStationIdOf(lineId, stationId);
//            lineDao.updateDownStation(lineId, upStationId);
//            sectionDao.deleteSection(lineId, upStationId, stationId);
//        }
//    }
//
//    public Distance distance(final Long lineId, final Long upStationId, final Long downStationId) {
//        return new Distance(sectionDao.findDistance(lineId, upStationId, downStationId));
//    }
//
//    public Distance distance(final Long lineId, final Long upStationId, final Long middleStationId, final Long downStationId) {
//        final Distance frontDistance = distance(lineId, upStationId, middleStationId);
//        final Distance backDistance = distance(lineId, middleStationId, downStationId);
//        return frontDistance.add(backDistance);
//    }
//
//    private void validateDeleteSection(final Long lineId, final Long stationId) {
//        if (sectionDao.stationCountInLine(lineId) <= LIMIT_NUMBER_OF_STATION_IN_LINE) {
//            throw new LineException("종점 뿐인 노선의 역을 삭제할 수 없습니다.");
//        }
//
//        if (!sectionDao.isExistingStation(lineId, stationId)) {
//            throw new LineException("노선에 존재하지 않는 역을 삭제할 수 없습니다.");
//        }
//    }
//}
