package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(final SectionDao sectionDao, final StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public List<Station> findUniqueStationsByLineId(final Long lineId) {
        return sectionDao.findAllByLineId(lineId)
            .stream()
            .flatMap(section -> Stream.of(section.getUpStationId(), section.getDownStationId()))
            .distinct()
            .map(id -> stationDao.findById(id).get())
            .sorted()
            .collect(Collectors.toList());
    }

    public void addSection(final Long lineId, final SectionRequest sectionRequest) {
        final Section targetSection = sectionRequest.toEntity(lineId);
        //객체 vs List객체 비교를 위해, 일급컬렉션 생성
        //final List<Section> sectionList = sectionDao.findAllByLineId(lineId);
        final Sections sections = new Sections(sectionDao.findAllByLineId(lineId));
    }
}
