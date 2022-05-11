package wooteco.subway.service; import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.CommonStationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.repository.StationRepository;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public Station save(final StationRequest stationRequest) {
        final Station station = new Station(stationRequest.getName());
        return stationRepository.save(station);
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public void deleteById(final Long id) {
        stationRepository.deleteById(id);
    }

    //    private static final int NO_ROW_AFFECTED = 0;
//    private static final String STATION_DUPLICATED = "이미 존재하는 지하철역입니다. ";
//    private static final String STATION_NOT_FOUND = "요청한 지하철 역이 존재하지 않습니다. ";
//
//    private final CommonStationDao stationDao;
//
//    public StationService(final CommonStationDao stationDao) {
//        this.stationDao = stationDao;
//    }
//
//    @Transactional
//    public Station save(final StationRequest stationRequest) {
//        final Station station = new Station(stationRequest.getName());
//        try {
//            return stationDao.save(station);
//        } catch (
//        DuplicateKeyException e) {
//            throw new IllegalStateException(STATION_DUPLICATED + station);
//        }
//    }
//
//
//    @Transactional(readOnly = true)
//    public List<Station> findAll() {
//        return stationDao.findAll();
//    }
//
//
//    @Transactional
//    public void deleteById(final Long id) {
//        final int theNumberOfAffectedRow = stationDao.deleteById(id);
//        if (theNumberOfAffectedRow == NO_ROW_AFFECTED) {
//            throw new IllegalStateException(STATION_NOT_FOUND + "id=" + id);
//        }
//    }
}
