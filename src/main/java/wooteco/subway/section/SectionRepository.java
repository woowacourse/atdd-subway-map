package wooteco.subway.section;

import org.springframework.stereotype.Repository;
import wooteco.subway.station.Station;
import wooteco.subway.station.StationDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class SectionRepository {
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionRepository(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Sections save(long lineId, long upStationId, long downStationId, int distance) {
        final Station upStation = findStationById(upStationId);
        final Station downStation = findStationById(downStationId);
        final SectionDbDto sectionDbDto = sectionDao.save(lineId, upStation.getId(), downStation.getId(), distance);
        final Section section = generateSection(sectionDbDto);
        return new Sections(lineId, Collections.singletonList(section));
    }

    public Station findStationById(long stationId) {
        return stationDao.findById(stationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역입니다."));
    }

    private Section generateSection(SectionDbDto sectionDbDto) {
        final Station upStation = findStationById(sectionDbDto.getUpStationId());
        final Station downStation = findStationById(sectionDbDto.getDownStationId());
        return new Section(sectionDbDto.getLineId(), upStation, downStation, sectionDbDto.getDistance());
    }

    public Sections findByLineId(long lineId) {
        final List<Section> sectionList = new ArrayList<>();
        final List<SectionDbDto> sectionDbDtoList = sectionDao.findByLineId(lineId);
        for (SectionDbDto sectionDbDto : sectionDbDtoList) {
            final Section section = generateSection(sectionDbDto);
            sectionList.add(section);
        }
        return new Sections(lineId, sectionList);
    }

    public  List<Long> findLineIdsContains(long stationId) {
        return sectionDao.findLineIdsContains(stationId);
    }

    public Section createSection(long lineId, long upStationId, long downStationId, int distance) {
        final Station upStation = findStationById(upStationId);
        final Station downStation = findStationById(downStationId);
        return new Section(lineId, upStation, downStation, distance);
    }

    public void deleteLine(long lineId) {
        sectionDao.deleteLine(lineId);
    }

    public void deleteSection(long lineId, long upStationId, long downStationId) {
        sectionDao.deleteSection(lineId, upStationId, downStationId);
    }
}
