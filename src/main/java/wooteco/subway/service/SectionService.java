package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final JdbcSectionDao jdbcSectionDao;

    public SectionService(JdbcSectionDao jdbcSectionDao) {
        this.jdbcSectionDao = jdbcSectionDao;
    }

    public Section save(Long lineId, SectionRequest sectionRequest) {
        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();
        int distance = sectionRequest.getDistance();
        Long id = jdbcSectionDao.save(lineId, new Section(upStationId, downStationId, distance));

        return new Section(id, upStationId, downStationId, distance);
    }

    public List<Section> getSectionsByLineId(long lineId) {
        return jdbcSectionDao.findSectionsByLineId(lineId);
    }
}
