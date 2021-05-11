package wooteco.subway.section.service;

import org.springframework.stereotype.Service;
import wooteco.subway.exception.SubWayException;
import wooteco.subway.section.Section;
import wooteco.subway.section.Sections;
import wooteco.subway.section.dto.SectionRequest;
import wooteco.subway.section.dto.SectionResponse;
import wooteco.subway.section.repository.JdbcSectionDao;
import wooteco.subway.station.Station;

import java.util.List;

@Service
public class SectionService {
    private JdbcSectionDao jdbcSectionDao;

    public SectionService(JdbcSectionDao jdbcSectionDao) {
        this.jdbcSectionDao = jdbcSectionDao;
    }

    public SectionResponse save(Long lineId, SectionRequest sectionReq) {
        validateExistStation(sectionReq);

        Section savedSection = jdbcSectionDao.save(lineId, sectionReq);
        return new SectionResponse(savedSection);
    }

    public SectionResponse add(Long lineId, SectionRequest sectionReq) {
        validateExistStation(sectionReq);
        Sections sections = validateSavable(lineId, sectionReq);

        Section savedSection = jdbcSectionDao.save(lineId, sectionReq);
        return new SectionResponse(savedSection);
    }

    private Sections validateSavable(Long lineId, SectionRequest sectionReq) {
        Sections sections =  new Sections(jdbcSectionDao.findAllByLineId(lineId));
        sections.validateSavableSection(sectionReq);
        return sections;
    }

    private void validateExistStation(SectionRequest sectionReq) {
        List<Station> stations = jdbcSectionDao.findStationsBy(sectionReq.getUpStationId(), sectionReq.getDownStationId());
        if (stations.size() != 2) {
            throw new SubWayException("등록되지 않은 역은 상행 혹은 하행역으로 추가할 수 없습니다.");
        }
    }
}
