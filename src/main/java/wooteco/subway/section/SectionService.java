package wooteco.subway.section;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(long lineId, long upStationId, long downStationId, int distance) {
        sectionDao.save(lineId, upStationId, downStationId, distance);
    }

    public void delete(long lineId, long upStationId, long downStationId) {
        sectionDao.delete(lineId, upStationId, downStationId);
    }

    public long findExistStation(long lineId, long upStationId, long downStationId) {

        long existStation = -1;
        if(sectionDao.isExistStation(lineId, upStationId)) {
            existStation = upStationId;
        }

        if(sectionDao.isExistStation(lineId, downStationId)) {
            checkDuplicateSection(existStation);
            existStation = downStationId;
        }
        return existStation;

    }

    public List<Long> findBeforeDownStationId(long lineId, long upStationId) {
        return sectionDao.findDownStationIdByUpStationId(lineId, upStationId);
    }

    public List<Long> findBeforeUpStationId(long lineId, long downStationId) {
        return sectionDao.findUpStationIdByDownStationId(lineId, downStationId);
    }

    public int findBeforeDistance(long lineId, long upStationId, long beforeStationId) {
        return sectionDao.distance(lineId, upStationId, beforeStationId);
    }

    private void checkDuplicateSection(long existStation) {
        if(existStation != -1) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }
    }
}
