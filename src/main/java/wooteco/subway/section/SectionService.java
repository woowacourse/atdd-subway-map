package wooteco.subway.section;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import wooteco.subway.line.Line;
import wooteco.subway.station.Station;

import java.util.List;

@Service
public class SectionService {

    private final SectionDao sectionDao;

    public SectionService(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public List<Long> findDownStationId(long lineId, long upStationId) {
        return sectionDao.findDownStation(lineId, upStationId);
    }

    public List<Long> findUpStationId(long lineId, long downStationId) {
        return sectionDao.findUpStation(lineId, downStationId);
    }

    public void checkAndAddSection(Section newSection, Line line) {
        List<Section> beforeSections = sectionDao.findBeforeSection(newSection);
        if( beforeSections.size() > 1) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }

        if (beforeSections.size() == 0) {
            throw new IllegalArgumentException("존재하지 않는 역입니다.");
        }
        Section beforeSection = beforeSections.get(0);
        List<Section> newSections = beforeSection.update(newSection, line);
        sectionDao.delete(beforeSection);
        sectionDao.save(newSections.get(0));
        sectionDao.save(newSections.get(1));
    }

    public ResponseEntity<String> checkUpDownAndDelete(long lineId, List<Long> upStationIds, List<Long> downStationIds, long stationId) {
        if (upStationIds.isEmpty()) {
            sectionDao.delete(new Section(lineId, stationId, downStationIds.get(0)));
            return ResponseEntity.ok().build();
        }

        if (downStationIds.isEmpty()) {
            sectionDao.delete(new Section(lineId, upStationIds.get(0), stationId));
            return ResponseEntity.ok().build();
        }
        return deleteSection(new Section(lineId, upStationIds.get(0), stationId),
                new Section(lineId, stationId, downStationIds.get(0)),
                new Section(lineId, upStationIds.get(0), downStationIds.get(0)));

    }

    public void checkSectionCount(long lineId) {
        if (sectionDao.count(lineId) == 1) {
            throw new IllegalArgumentException("구간이 하나뿐이라 더이상 지울 수 없습니다.");
        }
    }

    private ResponseEntity<String> deleteSection(Section beforeUpSection, Section beforeDownSection, Section section) {
        int firstDistance = sectionDao.distance(beforeUpSection);
        int secondDistance = sectionDao.distance(beforeDownSection);
        Section newSection = new Section(section, firstDistance + secondDistance);

        sectionDao.delete(beforeUpSection);
        sectionDao.delete(beforeDownSection);
        sectionDao.save(newSection);
        return ResponseEntity.ok().build();
    }
}
