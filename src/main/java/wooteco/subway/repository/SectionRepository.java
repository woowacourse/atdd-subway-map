package wooteco.subway.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import wooteco.subway.dao.section.SectionDao;
import wooteco.subway.domain.Section;

@Repository
@RequiredArgsConstructor
public class SectionRepository {

    private final SectionDao sectionDao;

    public void save(Section section, Long lineId) {
        sectionDao.save(section, lineId);
    }

    public void update(Section section) {
        sectionDao.update(section);
    }

    public void removeByStationId(Long lineId, Long stationId) {
        sectionDao.removeByStationId(lineId, stationId);
    }

    public List<Section> findAllByLineId(Long lineId) {
        return sectionDao.findAllByLineId(lineId);
    }
}
