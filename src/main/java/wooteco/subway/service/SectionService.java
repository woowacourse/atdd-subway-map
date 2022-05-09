package wooteco.subway.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public Section createSection(Long lineId, SectionRequest sectionRequest) {
        Optional<Line> upStation = lineDao.findById(sectionRequest.getUpStationId());
        Optional<Line> downStation = lineDao.findById(sectionRequest.getDownStationId());

        Section section = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        Section saveSection = sectionDao.save(section);
        return saveSection;
    }
}
