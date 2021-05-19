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

    public long findExistStation(Section section) {
        long existStation = -1;
        if (sectionDao.isExistStation(section.getLine().getId(), section.getUpStation().getId())) {
            existStation = section.getUpStation().getId();
        }

        if (sectionDao.isExistStation(section.getLine().getId(), section.getDownStation().getId())) {
            checkDuplicateSection(existStation);
            existStation = section.getDownStation().getId();
        }
        return existStation;
    }

    public List<Long> findDownStationId(long lineId, long upStationId) {
        return sectionDao.findDownStation(lineId, upStationId);
    }

    public List<Long> findUpStationId(long lineId, long downStationId) {
        return sectionDao.findUpStation(lineId, downStationId);
    }


    public boolean checkAndAddSection(Section newSection, long existStationId, Line line) {
        if (existStationId == newSection.getUpStation().getId()) {
            List<Long> downStationIds = sectionDao.findDownStation(line.getId(), newSection.getUpStation().getId());
            addDownStation(newSection, downStationIds, line);
            return true;
        }

        if (existStationId == newSection.getDownStation().getId()) {
            List<Long> upStationIds = sectionDao.findUpStation(line.getId(), newSection.getDownStation().getId());
            addUpStation(newSection, upStationIds, line);
            return true;
        }
        return false;
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

    private void addDownStation(Section newSection, List<Long> beforeDownStations, Line line) {
        if (beforeDownStations.isEmpty()) {
            sectionDao.save(newSection);
            return;
        }
        Section beforeSection = new Section(line, newSection.getUpStation(), new Station(beforeDownStations.get(0)));
        int distance = newSection.isAppropriateDistance(sectionDao.distance(beforeSection));

        Section newDownSection = new Section(line, newSection.getDownStation(), new Station(beforeDownStations.get(0)),
                distance);
        addSection(beforeSection, newSection, newDownSection);
    }

    private void addUpStation(Section newDownSection, List<Long> beforeUpStations, Line line) {
        if (beforeUpStations.isEmpty()) {
            sectionDao.save(newDownSection);
            return;
        }
        long beforeUpStationId = beforeUpStations.get(0);
        Section beforeSection = new Section(line, new Station(beforeUpStationId), newDownSection.getDownStation());
        int distance = newDownSection.isAppropriateDistance(sectionDao.distance(beforeSection));
        Section newUpSection = new Section(line, new Station(beforeUpStationId), newDownSection.getUpStation(), distance);
        addSection(beforeSection, newUpSection, newDownSection);
    }

    private void addSection(Section beforeSection, Section newUpSection, Section newDownSection) {
        sectionDao.delete(beforeSection);
        sectionDao.save(newUpSection);
        sectionDao.save(newDownSection);
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

    private void checkDuplicateSection(long existStation) {
        if (existStation != -1) {
            throw new IllegalArgumentException("이미 존재하는 구간입니다.");
        }
    }
}
