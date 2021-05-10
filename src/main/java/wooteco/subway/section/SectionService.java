package wooteco.subway.section;

import org.springframework.stereotype.Service;

@Service
public class SectionService {
    private SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section save(SectonDto sectonDto) {
        return sectionDao.save(sectonDto.getLineId(), sectonDto.getUpStationId(),
                sectonDto.getDownStationId(), sectonDto.getDistance());
    }
}
