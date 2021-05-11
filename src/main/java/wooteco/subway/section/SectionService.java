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

    public void addSection(final Section section) {
        addSection(section.lineId(), section.front(), section.back(), section.distance());
    }

    public void addSection(long lineId, long front, long back, int distance) {
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

    private void addFinalSection(Long lineId, Long front, Long back, int distance) {
        final FinalStations before = new FinalStations(lineDao.findUpStationId(lineId), lineDao.findDownStationId(lineId));
        final FinalStations after = before.addStations(front, back);

        lineDao.update(lineId, after.getUpStationId(), after.getDownStationId());
        sectionDao.save(lineId, front, back, distance);
    }

    private void addMiddleSection(Long lineId, Long front, Long back, int distance) {
        if (sectionDao.isExistingFrontStation(lineId, front)) {
            updateSectionFromFront(lineId, front, back, distance);
            return;
        }
        updateSectionFromBack(lineId, front, back, distance);
    }

    private void updateSectionFromFront(Long lineId, Long front, Long back, int distance){
        final Section update = sectionDao.findSectionByFrontStation(lineId, front);
        final Distance subDistance = new Distance(update.distance()).sub(distance);

        sectionDao.deleteSection(update.id());
        sectionDao.save(lineId, back, update.back(), subDistance.value());
        sectionDao.save(lineId, front, back, distance);
    }

    private void updateSectionFromBack(Long lineId, Long front, Long back, int distance){
        final Section update = sectionDao.findSectionByBackStation(lineId, back);
        final Distance subDistance = new Distance(update.distance()).sub(distance);

        sectionDao.deleteSection(update.id());
        sectionDao.save(lineId, update.front(), front, subDistance.value());
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

    private void deleteMiddleSection(Long lineId, Long stationId) {
        final Section frontSection = sectionDao.findSectionByBackStation(lineId, stationId);
        final Section backSection = sectionDao.findSectionByFrontStation(lineId, stationId);
        final Distance sumDistance = new Distance(frontSection.distance() + backSection.distance());

        sectionDao.deleteSection(frontSection.id());
        sectionDao.deleteSection(backSection.id());
        sectionDao.save(lineId, frontSection.front(), backSection.back(), sumDistance.value());
    }

    private void deleteDownStation(Long lineId, Long stationId) {
        if (lineDao.isDownStation(lineId, stationId)) {
            Section sectionToDelete = sectionDao.findSectionByBackStation(lineId, stationId);
            lineDao.update(lineId, lineDao.findUpStationId(lineId), sectionToDelete.front());
            sectionDao.deleteSection(sectionToDelete.id());
        }
    }

    private void deleteUpStation(Long lineId, Long stationId) {
        if (lineDao.isUpStation(lineId, stationId)) {
            Section sectionToDelete = sectionDao.findSectionByFrontStation(lineId, stationId);
            lineDao.update(lineId, sectionToDelete.back(), lineDao.findDownStationId(lineId));
            sectionDao.deleteSection(sectionToDelete.id());
        }
    }

    public Distance distance(final Long lineId, final Long front, final Long back) {
        return new Distance(sectionDao.findDistance(lineId, front, back));
    }

    public Distance distance(final Long lineId, final Long front, final Long middle, final Long back) {
        return distance(lineId, front, middle).add(distance(lineId, middle, back));
    }

    // TODO :: 두개 뺼 수 있는 방법 
    private void validateDeleteSection(final Long lineId, final Long stationId) {
        if (sectionDao.stationCountInLine(lineId) <= LIMIT_NUMBER_OF_STATION_IN_LINE) {
            throw new LineException("종점 뿐인 노선의 역을 삭제할 수 없습니다.");
        }

        if (!sectionDao.isExistingStation(lineId, stationId)) {
            throw new LineException("노선에 존재하지 않는 역을 삭제할 수 없습니다.");
        }
    }
}
