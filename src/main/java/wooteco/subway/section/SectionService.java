package wooteco.subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public void addSection(final Long lineId, final Long upStationId, final Long downStationId) {
        validateDuplicated(lineId, upStationId, downStationId);

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

    private void validateDuplicated(final Long lineId, final Long upStation, final Long downStation) {
        final boolean existingUpStation1 = sectionDao.isExistingUpStation(lineId, upStation);
        final boolean existingDownStation1 = sectionDao.isExistingDownStation(lineId, downStation);

        if (existingUpStation1 == existingDownStation1) {
            throw new IllegalArgumentException("1 두 역이 모두 이미 포함되거나, 두 역이 모두 포함되지 않은 구간");
        }

//        final boolean existingUpStation2 = sectionDao.isExistingUpStation(lineId, downStation);
//        final boolean existingDownStation2 = sectionDao.isExistingDownStation(lineId, upStation);
//
//        if (existingUpStation2 == existingDownStation2) {
//            throw new IllegalArgumentException("2 두 역이 모두 이미 포함되거나, 두 역이 모두 포함되지 않은 구간");
//        }
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
