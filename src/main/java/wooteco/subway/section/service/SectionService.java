package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.StationsMap;
import wooteco.subway.section.dao.SectionDao;
import wooteco.subway.section.domain.Section;
import wooteco.subway.section.dto.SectionRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void save(Long lineId, SectionRequest sectionRequest) {
        Section section = new Section(lineId,
                sectionRequest.getUpStationId(),
                sectionRequest.getDownStationId(),
                sectionRequest.getDistance(),
                0
                );
        List<Section> sectionsByLineId = sectionDao.findAllByLineId(section.getLineId());
        System.out.println("0000000000");
        Set<Long> sectionsIds = new HashSet<>();
        for(Section sec : sectionsByLineId){
            sectionsIds.add(sec.getDownStationId());
            sectionsIds.add(sec.getUpStationId());
        }
        validateIfSectionContainsOnlyOneStationInLine(sectionsIds, section);
        StationsMap lineStations = StationsMap.from(sectionsByLineId);
        System.out.println("1111111111");
        if(lineStations.isDownStation(section) || lineStations.isUpStation(section)){
            System.out.println("222222");
            sectionDao.save(section);
            return;
        }
        //update
        if(sectionsIds.contains(section.getDownStationId())){
            //todo거리검증해야됨.
            System.out.println("333333");
            sectionDao.updateByDownStationId(section.getLineId(), section.getDownStationId(), section.getUpStationId());
            sectionDao.save(section);
            return;
        }
        if(sectionsIds.contains(section.getUpStationId())){
            System.out.println("44444444");
            //todo거리검증해야됨.
            sectionDao.updateByUpStationId(section.getLineId(), section.getUpStationId(), section.getDownStationId());
            sectionDao.save(section);
            return;
        }
    }

    private void validateIfSectionContainsOnlyOneStationInLine(Set<Long> sectionsIds, Section section) {
        for(Long id : sectionsIds){
            System.out.println("fuckfuck");
            System.out.println(id);
        }
        System.out.println(section.getDownStationId());
        System.out.println(section.getUpStationId());
        int count = 0;
        if(sectionsIds.contains(section.getDownStationId())){
            ++count;
        }
        if(sectionsIds.contains(section.getUpStationId())){
            ++count;
        }
        if(count != 1){
            throw new IllegalArgumentException("구간의 역 중에서 한개의 역만은 노선에 존재하여야 합니다.");
        }
    }
}
