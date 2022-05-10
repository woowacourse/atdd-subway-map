package wooteco.subway.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private static final int DELETE_FAIL = 0;

    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    @Transactional
    public void save(Long lineId, SectionRequest request) {
        final Sections sections = new Sections(sectionDao.findByLineId(lineId));
        final Section section = new Section(lineId, request.getUpStationId(), request.getDownStationId(),
                request.getDistance());

        List<Section> result = sections.add(section);
        updateSection(lineId, result);
    }

    @Transactional
    public void delete(Long lineId, Long stationId) {
        final Sections sections = new Sections(sectionDao.findByLineId(lineId));

        List<Section> result = sections.delete(stationId);
        updateSection(lineId, result);
    }

    private void updateSection(Long lineId, List<Section> result) {
        sectionDao.deleteByLineId(lineId);

        for (Section saveSection : result) {
            sectionDao.save(saveSection);
        }
    }
}
