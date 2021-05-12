package wooteco.subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.FinalStations;
import wooteco.subway.line.LineDao;
import wooteco.subway.line.LineException;

@Service
@Transactional
public class SectionService {
    private static final int LIMIT_NUMBER_OF_STATION_IN_LINE = 2;

    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public SectionService(final SectionDao sectionDao, final LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public void addSection(final Long lineId, final Section section){
        addSection(lineId, section.front(), section.back(), section.distance());
    }

    public void addSection(final Long lineId, final Long front, final Long back, final int distance) {
        if (isFinalSection(lineId, front, back)) {
            addFinalSection(lineId, front, back, distance);
            return;
        }

        if (isMiddleSection(lineId, front, back)) {
            addMiddleSection(lineId, front, back, distance);
            return;
        }

        throw new LineException("잘못된 구간 입력입니다.");
    }

    private boolean isFinalSection(final Long lineId, final Long station) {
        return isFinalSection(lineId, station, station);
    }

    private boolean isFinalSection(final Long lineId, final Long front, final Long back) {
        final FinalStations finalStations = new FinalStations(lineDao.findUpStationId(lineId), lineDao.findDownStationId(lineId));
        return finalStations.isFinalSection(front, back);
    }

    private boolean isMiddleSection(final Long lineId, final Long front, final Long back) {
        return sectionDao.isExistingFrontStation(lineId, front) != sectionDao.isExistingBackStation(lineId, back);
    }

    private void addFinalSection(final Long lineId, final Long front, final Long back, final int distance) {
        final FinalStations before = new FinalStations(lineDao.findUpStationId(lineId), lineDao.findDownStationId(lineId));
        lineDao.updateFinalStations(lineId, before.addStations(front, back));
        sectionDao.save(lineId, front, back, distance);
    }

    private void addMiddleSection(final Long lineId, final Long front, final Long back, final int distance) {
        if (sectionDao.isExistingFrontStation(lineId, front)) {
            updateSectionFromFront(lineId, front, back, distance);
            return;
        }
        updateSectionFromBack(lineId, front, back, distance);
    }

    private void updateSectionFromFront(final Long lineId, final Long front, final Long back, final int distance){
        final Section sectionToUpdate = sectionDao.findSectionByFrontStation(lineId, front);
        final Distance subDistance = new Distance(sectionToUpdate.distance()).sub(distance);

        sectionDao.deleteSection(sectionToUpdate);
        sectionDao.save(lineId, back, sectionToUpdate.back(), subDistance.value());
        sectionDao.save(lineId, front, back, distance);
    }

    private void updateSectionFromBack(final Long lineId, final Long front, final Long back, final int distance){
        final Section sectionToUpdate = sectionDao.findSectionByBackStation(lineId, back);
        final Distance subDistance = new Distance(sectionToUpdate.distance()).sub(distance);

        sectionDao.deleteSection(sectionToUpdate);
        sectionDao.save(lineId, sectionToUpdate.front(), front, subDistance.value());
        sectionDao.save(lineId, front, back, distance);
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        validateDeleteSection(lineId, stationId);

        if (isFinalSection(lineId, stationId)) {
            deleteUpStation(lineId, stationId);
            deleteDownStation(lineId, stationId);
            return;
        }

        deleteMiddleSection(lineId, stationId);
    }

    private void deleteMiddleSection(final Long lineId, final Long stationId) {
        final Section frontSection = sectionDao.findSectionByBackStation(lineId, stationId);
        final Section backSection = sectionDao.findSectionByFrontStation(lineId, stationId);
        final Distance sumDistance = new Distance(frontSection.distance() + backSection.distance());

        sectionDao.deleteSection(frontSection);
        sectionDao.deleteSection(backSection);
        sectionDao.save(lineId, frontSection.front(), backSection.back(), sumDistance.value());
    }

    private void deleteDownStation(final Long lineId, final Long stationId) {
        if (lineDao.isDownStation(lineId, stationId)) {
            Section sectionToDelete = sectionDao.findSectionByBackStation(lineId, stationId);
            lineDao.updateFinalStations(lineId, lineDao.findUpStationId(lineId), sectionToDelete.front());
            sectionDao.deleteSection(sectionToDelete);
        }
    }

    private void deleteUpStation(final Long lineId, final Long stationId) {
        if (lineDao.isUpStation(lineId, stationId)) {
            Section sectionToDelete = sectionDao.findSectionByFrontStation(lineId, stationId);
            lineDao.updateFinalStations(lineId, sectionToDelete.back(), lineDao.findDownStationId(lineId));
            sectionDao.deleteSection(sectionToDelete);
        }
    }

    public Distance distance(final Long lineId, final Long front, final Long back) {
        return new Distance(sectionDao.findDistance(lineId, front, back));
    }

    private void validateDeleteSection(final Long lineId, final Long stationId) {
        if (sectionDao.stationCountInLine(lineId) <= LIMIT_NUMBER_OF_STATION_IN_LINE) {
            throw new LineException("종점 뿐인 노선의 역을 삭제할 수 없습니다.");
        }

        if (!sectionDao.isExistingStationInLine(lineId, stationId)) {
            throw new LineException("노선에 존재하지 않는 역을 삭제할 수 없습니다.");
        }
    }
}
