package wooteco.subway.service;

import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.exception.NotExistException;

@Service
public class SectionService {

    private static final int DELETE_FAIL = 0;

    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public void save(Long lineId, SectionRequest request) {
        final Section section = new Section(lineId, request.getUpStationId(), request.getDownStationId(),
                request.getDistance());

        sectionDao.save(section);
    }

    public void delete(Long lineId, Long stationId) {
        final Sections sections = new Sections(sectionDao.findByLineId(lineId));

        sections.delete(stationId);

        final int isDeleted = sectionDao.deleteById(stationId);

        if (isDeleted == DELETE_FAIL) {
            throw new NotExistException("존재하지 않는 노선입니다.");
        }
    }
}
