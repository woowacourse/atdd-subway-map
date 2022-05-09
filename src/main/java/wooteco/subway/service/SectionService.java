package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(final SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(final Long lineNumber, final SectionRequest sectionRequest) {
        sectionDao.save(
                lineNumber,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance()
        );
    }

    public void deleteById(final Long lineNumber, final Long stationId) {
        sectionDao.deleteById(lineNumber, stationId);
    }
}
