package wooteco.subway.section;

import org.springframework.stereotype.Service;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(long lineId, long upStationId, long downStationId, int distance) {
        sectionDao.save(lineId, upStationId, downStationId, distance);
    }
}
