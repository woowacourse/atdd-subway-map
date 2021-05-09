package wooteco.subway.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public SectionResponse createSection(SectionRequest sectionRequest) {
        SectionEntity sectionEntity = new SectionEntity(sectionRequest.getLineId(),
            sectionRequest.getUpStationId(),
            sectionRequest.getDownStationId(),
            sectionRequest.getDistance());

        SectionEntity newSectionEntity = sectionDao.save(sectionEntity);

        return new SectionResponse(newSectionEntity);
    }
}
