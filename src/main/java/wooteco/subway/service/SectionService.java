package wooteco.subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.JdbcSectionDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationResponse;

@Service
public class SectionService {

    private final StationService stationService;
    private final JdbcSectionDao jdbcSectionDao;

    public SectionService(StationService stationService, JdbcSectionDao jdbcSectionDao) {
        this.stationService = stationService;
        this.jdbcSectionDao = jdbcSectionDao;
    }

    public Long createSection(SectionRequest sectionRequest, Long lineId) {
        Long upStationId = sectionRequest.getUpStationId();
        Long downStationId = sectionRequest.getDownStationId();
        int distance = sectionRequest.getDistance();

        if (jdbcSectionDao.isExistByUpStationIdAndDownStationId(upStationId, downStationId)) {
            throw new IllegalArgumentException("이미 존재하기 때문에 구간을 등록할 수 없습니다.");
        }

        //2. up이 있으면 down이 있어야 하고 down이 있으면 up이 있어야한다.

        //3. 갈래길
        //  상행선이 이미 db에 있는 경우.(db에 있는 상행선 = 들어오는 값의 상행선)

        // 하행선이 이미 db에 있는 경우. (db에 있는 하행선 = 들어오는 값의 하행선)

        // -> db에 있는 거리보다 들어오는 값의 거리가 작으면 갈래길 처리해줘야 한다.
        //

        Section newSection = new Section(lineId, upStationId, downStationId, distance);
        return jdbcSectionDao.save(newSection);
    }

    public List<StationResponse> getStationsByLineId(Long lineId) {
        return jdbcSectionDao.findByLineId(lineId)
                .getStationIds()
                .stream()
                .map(stationService::getStation)
                .collect(Collectors.toUnmodifiableList());
    }

    public boolean deleteSection(Long lineId, Long stationId) {
        Sections sections = jdbcSectionDao.findByLineIdAndStationId(lineId, stationId);
        sections.validateLengthToDeletion();
        Section upStationSection = sections.getSectionStationIdEqualsUpStationId(stationId);
        Section downStationSection = sections.getSectionStationIdEqualsDownStationId(stationId);
        jdbcSectionDao.deleteByLineIdAndUpStationId(lineId, stationId);
        jdbcSectionDao.updateDownStationIdByLineIdAndUpStationId(lineId, downStationSection.getUpStationId(),
                upStationSection.getDownStationId());

        return true;
    }
}
