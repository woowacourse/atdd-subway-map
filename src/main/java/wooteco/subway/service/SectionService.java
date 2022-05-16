package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionDeleteRequest;
import wooteco.subway.dto.SectionSaveRequest;
import wooteco.subway.entity.SectionEntity;
import wooteco.subway.exception.notfound.LineNotFoundException;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public void save(SectionSaveRequest request) {
        lineDao.findById(request.getLineId())
                .orElseThrow(LineNotFoundException::new);
        Section sectionForSave = new Section(request.getLineId(), request.getUpStationId(),
                request.getDownStationId(), request.getDistance());
        Sections sections = new Sections(findByLineId(request.getLineId()));
        sections.add(sectionForSave);

        sectionDao.deleteByLineId(request.getLineId());
        sectionDao.saveAll(toEntities(sections));
    }

    private List<SectionEntity> toEntities(Sections sections) {
        return sections.getValue().stream()
                .map(SectionEntity::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Section> findByLineId(Long lineId) {
        return sectionDao.findByLineId(lineId)
                .stream().map(this::toSection)
                .collect(Collectors.toList());
    }

    private Section toSection(SectionEntity entity) {
        return new Section(entity.getId(), entity.getLine_id(), entity.getUpStationId(), entity.getDownStationId(),
                entity.getDistance());
    }

    public void delete(SectionDeleteRequest request) {
        List<Section> sectionList = findByLineId(request.getLineId());
        Sections sections = new Sections(sectionList);
        sections.deleteSectionsByStationId(request.getStationId());
        sectionDao.deleteByLineId(request.getLineId());
        sectionDao.saveAll(toEntities(sections));
    }

    public int deleteByLineId(Long lineId) {
        return sectionDao.deleteByLineId(lineId);
    }
}
