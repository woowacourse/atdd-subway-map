package wooteco.subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.LineDao;
import wooteco.subway.line.LineException;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class SectionService {
    private final int LIMIT_NUMBER_OF_STATION_IN_LINE = 2;

    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public SectionService(final SectionDao sectionDao, final LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public void addSection(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        if (isMiddleSection(lineId, upStationId, downStationId)){
            addMiddleSection(lineId, upStationId, downStationId, distance);
            return;
        }

        if(isFinalUpStation(lineId, downStationId)){
            addFinalUpStation(lineId, upStationId, downStationId, distance);
            return;
        }

        if(isFinalDownStation(lineId, upStationId)){
            addFinalDownStation(lineId, upStationId, downStationId, distance);
            return;
        }

        throw new LineException("잘못된 구간 입력입니다.");
    }

    private boolean isMiddleSection(final Long lineId, final Long upStation, final Long downStation) {
        final boolean existingUpStation = sectionDao.isExistingUpStation(lineId, upStation);
        final boolean existingDownStation = sectionDao.isExistingDownStation(lineId, downStation);

        return existingUpStation != existingDownStation;
    }

    private void addMiddleSection(final Long lineId, final Long upStationId, final Long downStationId, final int distance){
        if(sectionDao.isExistingUpStation(lineId, upStationId)){
            updateSectionFromUpStation(lineId, upStationId, downStationId, distance);
            return;
        }

        if(sectionDao.isExistingDownStation(lineId, downStationId)){
            updateSectionFromDownStation(lineId, upStationId, downStationId, distance);
            return;
        }
    }

    private void updateSectionFromUpStation(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        final Long beforeDownStation = sectionDao.downStationIdOf(lineId, upStationId);
        final int beforeDistance = distance(lineId, upStationId, beforeDownStation);

        validateAdditionDistance(beforeDistance, distance);

        sectionDao.updateDownStation(lineId, beforeDownStation, downStationId, distance);
        sectionDao.save(lineId, downStationId, beforeDownStation, beforeDistance - distance);
    }

    private void updateSectionFromDownStation(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        final Long beforeUpStation = sectionDao.upStationIdOf(lineId, downStationId);
        final int beforeDistance = distance(lineId, beforeUpStation, downStationId);

        validateAdditionDistance(beforeDistance, distance);

        sectionDao.updateUpStation(lineId, beforeUpStation, upStationId,  distance);
        sectionDao.save(lineId, beforeUpStation, upStationId, beforeDistance - distance);
    }

    private void validateAdditionDistance(final int beforeDistance, final int distance){
        if(distance >= beforeDistance){
            throw new LineException("기존의 구간 길이보다 같거나 큰 길이의 삽입이 불가능합니다.");
        }
    }

    private void addFinalDownStation(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        sectionDao.save(lineId, upStationId, downStationId, distance);
        lineDao.updateDownStation(lineId, downStationId);
    }

    private void addFinalUpStation(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        sectionDao.save(lineId, upStationId, downStationId, distance);
        lineDao.updateUpStation(lineId, upStationId);
    }

    private boolean isFinalUpStation(final Long lineId, final Long stationId){
        final Long finalUpStation = lineDao.findUpStationId(lineId);
        return finalUpStation.equals(stationId);
    }

    private boolean isFinalDownStation(final Long lineId, final Long stationId){
        final Long finalDownStation = lineDao.findDownStationId(lineId);
        return finalDownStation.equals(stationId);
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        validateDeleteSection(lineId, stationId);

        if(isFinalUpStation(lineId, stationId)){
            deleteFinalUpStation(lineId, stationId);
            return;
        }

        if(isFinalDownStation(lineId, stationId)){
            deleteFinalDownStation(lineId, stationId);
            return;
        }

        deleteMiddleStation(lineId, stationId);
    }

    private void deleteFinalUpStation(final Long lineId, final Long stationId) {
        final Long downStationId = sectionDao.downStationIdOf(lineId, stationId);

        lineDao.updateUpStation(lineId, downStationId);
        sectionDao.deleteSection(lineId, stationId, downStationId);
    }

    private void deleteFinalDownStation(final Long lineId, final Long stationId) {
        final Long upStationId = sectionDao.upStationIdOf(lineId, stationId);

        lineDao.updateDownStation(lineId, upStationId);
        sectionDao.deleteSection(lineId, upStationId, stationId);
    }

    private void deleteMiddleStation(final Long lineId, final Long stationId){
        final Long upStationId = sectionDao.upStationIdOf(lineId, stationId);
        final Long downStationId = sectionDao.downStationIdOf(lineId, stationId);

        sectionDao.updateDownStation(lineId, stationId, downStationId, distance(lineId, upStationId, stationId, downStationId));
        sectionDao.deleteSection(lineId, stationId, downStationId);
    }

    private void validateDeleteSection(final Long lineId, final Long stationId){
        if(sectionDao.stationCountInLine(lineId) <= LIMIT_NUMBER_OF_STATION_IN_LINE){
            throw new LineException("종점 뿐인 노선의 역을 삭제할 수 없습니다.");
        }

        if(!sectionDao.isExistingStation(lineId, stationId)){
            throw new LineException("노선에 존재하지 않는 역을 삭제할 수 없습니다.");
        }
    }

    public int distance(final Long lineId, final Long upStationId, final Long downStationId) {
        if(sectionDao.isExistingSection(lineId, upStationId, downStationId)){
            return sectionDao.findDistance(lineId, upStationId, downStationId);
        }
        throw new LineException("존재하지 않는 구간입니다.");
    }

    public int distance(final Long lineId, final Long upStationId, final Long middleStationId, final Long downStationId) {
        return distance(lineId, upStationId, middleStationId) + distance(lineId, middleStationId, downStationId);
    }

    public List<Long> allStationIdInLine(final Long lineId) {
        final List<Long> stations = new LinkedList<>();

        Long stationId = lineDao.findUpStationId(lineId);
        do {
            stations.add(stationId);
            stationId = sectionDao.downStationIdOf(lineId, stationId);
        } while (sectionDao.isExistingUpStation(lineId, stationId));

        stations.add(stationId);

        return stations;
    }
}
