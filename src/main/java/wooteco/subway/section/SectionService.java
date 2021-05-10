package wooteco.subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.line.LineDao;
import wooteco.subway.line.LineException;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class SectionService {
    private final int LIMIT_NUMBER_OF_STATION_IN_LINE = 2;

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao, final LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public void addSection(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        if (isMiddleSection(lineId, upStationId, downStationId)){
            addMiddleSection(lineId, upStationId, downStationId, distance);
            return;
        }

        if(isFrontSection(lineId, downStationId) ){
            sectionDao.save(lineId, upStationId, downStationId, distance);
            lineDao.updateUpStation(lineId, upStationId);
            return;
        }

        if(isBackSection(lineId, upStationId)){
            sectionDao.save(lineId, upStationId, downStationId, distance);
            lineDao.updateDownStation(lineId, downStationId);
            return;
        }

        throw new LineException("잘못된 구간 입력입니다.");
    }

    private void addMiddleSection(final Long lineId, final Long upStationId, final Long downStationId, final int distance){
        if(sectionDao.isExistingUpStation(lineId, upStationId)){
            final Long beforeDownStation = sectionDao.downStationId(lineId, upStationId);
            final int beforeDistance = sectionDao.findDistance(lineId, upStationId, beforeDownStation);

            if(distance >= beforeDistance){
                throw new LineException("기존의 구간 길이보다 같거나 큰 길이의 삽입이 불가능합니다.");
            }

            updateSectionFromUpStation(lineId, upStationId, downStationId, distance);
            return;
        }

        if(sectionDao.isExistingDownStation(lineId, downStationId)){
            final Long beforeUpStation = sectionDao.upStationId(lineId, downStationId);
            final int beforeDistance = sectionDao.findDistance(lineId, beforeUpStation, downStationId);

            if(distance >= beforeDistance){
                throw new LineException("기존의 구간 길이보다 같거나 큰 길이의 삽입이 불가능합니다.");
            }

            updateSectionFromDownStation(lineId, upStationId, downStationId, distance);
            return;
        }
    }

    private void updateSectionFromUpStation(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        final Long beforeDownStation = sectionDao.downStationId(lineId, upStationId);
        final int beforeDistance = sectionDao.findDistance(lineId, upStationId, beforeDownStation);

        sectionDao.updateDownStation(lineId, beforeDownStation, downStationId, distance);
        sectionDao.save(lineId, downStationId, beforeDownStation, beforeDistance - distance);
    }

    private void updateSectionFromDownStation(final Long lineId, final Long upStationId, final Long downStationId, final int distance) {
        final Long beforeUpStation = sectionDao.upStationId(lineId, downStationId);
        final int beforeDistance = sectionDao.findDistance(lineId, beforeUpStation, downStationId);

        sectionDao.updateUpStation(lineId, beforeUpStation, upStationId,  distance);
        sectionDao.save(lineId, beforeUpStation, upStationId, beforeDistance - distance);
    }

    private boolean isMiddleSection(final Long lineId, final Long upStation, final Long downStation) {
        final boolean existingUpStation = sectionDao.isExistingUpStation(lineId, upStation);
        final boolean existingDownStation = sectionDao.isExistingDownStation(lineId, downStation);

        return existingUpStation != existingDownStation;
    }

    private boolean isFrontSection(final Long lineId, final Long downStation) {
        // downStation이 DB의 up에만 있어야하고, down에 있어선 안된다.

        final boolean existingUpStation = sectionDao.isExistingUpStation(lineId, downStation);
        final boolean existingDownStation = sectionDao.isExistingDownStation(lineId, downStation);

        return existingUpStation == true && existingDownStation == false;
    }

    private boolean isBackSection(final Long lineId, final Long upStation) {
        // upStation이 DB의 down에만 있어야하고, up에 있어선 안된다.

        final boolean existingUpStation = sectionDao.isExistingUpStation(lineId, upStation);
        final boolean existingDownStation = sectionDao.isExistingDownStation(lineId, upStation);

        return existingUpStation == false && existingDownStation == true;
    }

    public List<Station> findAllSectionInLine(final Long lineId) {
        final List<Station> stations = new LinkedList<>();

        Long upStationId = lineDao.findUpStationId(lineId);
        do {
            final Station station = stationDao.findById(upStationId).get();
            stations.add(station);
            upStationId = sectionDao.downStationId(lineId, upStationId);
        } while (sectionDao.isExistingUpStation(lineId, upStationId));

        stations.add(stationDao.findById(upStationId).get());

        return stations;
    }

    public void deleteSection(final Long lineId, final Long stationId) {
        final List<Station> allSectionInLine = findAllSectionInLine(lineId);
        if(allSectionInLine.size() <= LIMIT_NUMBER_OF_STATION_IN_LINE){
            throw new IllegalArgumentException("종점 뿐인 노선의 역을 삭제할 수 없습니다.");
        }

        final boolean existingUpStation = sectionDao.isExistingUpStation(lineId, stationId);
        final boolean existingDownStation = sectionDao.isExistingDownStation(lineId, stationId);

        if(existingUpStation == true && existingDownStation == true ){
            // 중간에 있는 역 삭제
            final Long upStationId = sectionDao.upStationId(lineId, stationId);
            final Long downStationId = sectionDao.downStationId(lineId, stationId);

            final int distanceFromUpStation = sectionDao.findDistance(lineId, upStationId, stationId);
            final int distanceFromDownStation = sectionDao.findDistance(lineId, stationId, downStationId);
            final int distance = distanceFromUpStation+distanceFromDownStation;

            sectionDao.updateDownStation(lineId, stationId, downStationId, distance);
            sectionDao.deleteSection(lineId, stationId, downStationId);
            return;
        }

        if(existingUpStation == true && existingDownStation == false){
            // 상행 종점 역 삭제
            final Long downStationId = sectionDao.downStationId(lineId, stationId);
            lineDao.updateUpStation(lineId, downStationId);
            sectionDao.deleteSection(lineId, stationId, downStationId);
            return;
        }

        if(existingUpStation == false && existingDownStation == true){
            // 하행 종점 역 삭제
            final Long upStationId = sectionDao.upStationId(lineId, stationId);
            lineDao.updateDownStation(lineId, upStationId);
            sectionDao.deleteSection(lineId, upStationId, stationId);
            return;
        }

        throw new IllegalArgumentException("노선에 존재하지 않는 역을 삭제할 수 없습니다.");
    }
}
