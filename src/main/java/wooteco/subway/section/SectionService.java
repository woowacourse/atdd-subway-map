package wooteco.subway.section;

import org.springframework.stereotype.Service;

@Service
public class SectionService {

    private final SectionH2Dao sectionH2Dao; //TODO 인터페이스 추출

    private SectionService(SectionH2Dao sectionH2Dao) {
        this.sectionH2Dao = sectionH2Dao;
    }

    public Section add(Long lineId, String upStationId, String downStationId, Long distance) {
        Section section = new Section(upStationId, downStationId, distance);
        return sectionH2Dao.save(lineId, section);
    }
}
