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

    private final SectionDao sectionDao;
    private final StationDao stationDao;
    private final LineDao lineDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao, final LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
        this.lineDao = lineDao;
    }

    public void addSection(final Long lineId, final Long upStationId, final Long downStationId) {
        if (isMiddleSection(lineId, upStationId, downStationId)){
            addMiddleSection(lineId, upStationId, downStationId);
            return;
        }

        if(isFrontSection(lineId, downStationId) ){
            sectionDao.save(lineId, upStationId, downStationId, 0);
            lineDao.updateUpStation(lineId, upStationId);
            return;
        }

        if(isBackSection(lineId, upStationId)){
            sectionDao.save(lineId, upStationId, downStationId, 0);
            lineDao.updateDownStation(lineId, downStationId);
            return;
        }

        throw new LineException("잘못된 구간 입력입니다.");
    }

    private void addMiddleSection(final Long lineId, final Long upStationId, final Long downStationId){
        if(sectionDao.isExistingUpStation(lineId, upStationId)){
            updateUpStation(lineId, upStationId, downStationId);
            return;
        }

        if(sectionDao.isExistingDownStation(lineId, downStationId)){
            updateDownStation(lineId, upStationId, downStationId);
            return;
        }
    }

    private void updateUpStation(final Long lineId, final Long upStationId, final Long downStationId) {
        final Long beforeDownStation = sectionDao.getDownStationId(lineId, upStationId);

        sectionDao.updateDownStation(lineId, beforeDownStation, downStationId);
        sectionDao.save(lineId, downStationId, beforeDownStation, 0);
    }

    private void updateDownStation(final Long lineId, final Long upStationId, final Long downStationId) {
        final Long beforeUpStation = sectionDao.getUpStationId(lineId, downStationId);

        sectionDao.updateUpStation(lineId, beforeUpStation, upStationId);
        sectionDao.save(lineId, beforeUpStation, upStationId, 0);
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

    // finalUpStationId를 구하는 방법은? -> 테이블 이용?
    public List<Station> findAllSectionInLine(final Long lineId, final Long finalUpStationId) {
        final List<Station> stations = new LinkedList<>();

        Long upStationId = finalUpStationId;
        do {
            final Station station = stationDao.findById(upStationId).get();
            stations.add(station);
            upStationId = sectionDao.getDownStationId(lineId, upStationId);
        } while (sectionDao.isExistingUpStation(lineId, upStationId));

        stations.add(stationDao.findById(upStationId).get());

        return stations;
    }
}
