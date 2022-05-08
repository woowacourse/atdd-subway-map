package wooteco.subway.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.SectionWithStation;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Service
public class SectionService {
    private static final String SECTION_LENGTH_ERROR_MESSAGE = "새 구간의 길이가 기존 역 사이 길이보다 작아야 합니다.";
    private static final String DUPLICATED_SECTION_ERROR_MESSAGE = "중복된 구간입니다.";
    private static final String INVALID_STATION_ID_ERROR_MESSAGE = "구간 안에 존재하지 않는 아이디의 역이 있습니다.";
    private static final String LINK_FAILURE_ERROR_MESSAGE = "해당 구간은 역과 연결될 수 없습니다.";
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(SectionDao sectionDao, StationDao stationDao) {
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Section save(Section section) {
        Sections sections = getSections(section.getLineId());
        //상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
        validateUniqueSection(section);
        //상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
        validateExistStation(section);
        if (sections.isMiddleUpAttachSection(section)) {
            //상행기준 새구간 추가
            Section upStationSection = sectionDao.findByUpStationId(section.getLineId(), section.getUpStationId());
            // 기존 역사이 길이보다 크거나 같으면 등록불가 : 검증
            validateDistance(upStationSection.getDistance(), section.getDistance());
            // 기존 상행 구간 삭제
            sectionDao.delete(upStationSection.getId());
            // 파라미터 구간 저장
            Long id = sectionDao.save(section);
            // 파라미터 구간의 하행-기존 상행 구간의 하행 저장
            sectionDao.save(upStationSection.calculateDownRemainSection(section));
            return sectionDao.findById(id);
        }
        if (sections.isMiddleDownAttachSection(section)) {
            //하행기준 새구간 추가
            Section downStationSection = sectionDao.findByDownStationId(section.getLineId(),
                    section.getDownStationId());
            // 기존 역 사이 길이보다 크거나 같으면 등록불가 : 검증
            validateDistance(downStationSection.getDistance(), section.getDistance());
            // 기존 하행 구간 삭제
            sectionDao.delete(downStationSection.getId());
            // 파라미터 구간 저장
            Long id = sectionDao.save(section);
            // 기존 상행 구간의 상행-파라미터 구간의 상행 저장
            sectionDao.save(downStationSection.calculateUpRemainSection(section));
            return sectionDao.findById(id);
        }
        Long id = sectionDao.save(section);
        return sectionDao.findById(id);
    }

    private void validateExistStation(Section section) {
        if (!stationDao.hasStation(section.getUpStationId()) || !stationDao.hasStation(section.getDownStationId())) {
            throw new IllegalArgumentException(INVALID_STATION_ID_ERROR_MESSAGE);
        }
        if (sectionDao.findAllByLineId(section.getLineId()).isEmpty()) {
            return;
        }
        if (hasNoStationId(section) && hasNoStationId(getReverseSection(section))) {
            throw new IllegalArgumentException(LINK_FAILURE_ERROR_MESSAGE);
        }
    }

    private Section getReverseSection(Section section) {
        return new Section(section.getLineId(), section.getDownStationId(), section.getUpStationId(),
                section.getDistance());
    }

    private boolean hasNoStationId(Section section) {
        return !sectionDao.hasUpStationId(section) && !sectionDao.hasDownStationId(section);
    }

    private void validateUniqueSection(Section section) {
        if (sectionDao.hasUpStationId(section) && sectionDao.hasDownStationId(section)) {
            throw new IllegalArgumentException(DUPLICATED_SECTION_ERROR_MESSAGE);
        }
    }

    private void validateDistance(int existingDistance, int sectionDistance) {
        if (sectionDistance >= existingDistance) {
            throw new IllegalArgumentException(SECTION_LENGTH_ERROR_MESSAGE);
        }
    }

    public List<Station> findStationsOfLine(Long lineId) {
        return getSections(lineId).calculateStations();
    }

    private Sections getSections(Long lineId) {
        return new Sections(
                sectionDao.findAllByLineId(lineId)
                        .stream()
                        .map(getSectionWithStation())
                        .collect(Collectors.toList())
        );
    }

    private Function<Section, SectionWithStation> getSectionWithStation() {
        return section -> new SectionWithStation(
                section.getId(),
                section.getLineId(),
                stationDao.findById(section.getUpStationId()),
                stationDao.findById(section.getDownStationId()),
                section.getDistance()
        );
    }

    public void deleteSection(Long lineId, Long stationId) {
        //TODO 구간제거 기능 추가
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        boolean isUpStation = sections.stream().anyMatch(section -> section.getUpStationId().equals(stationId));
        boolean isDownStation = sections.stream().anyMatch(section -> section.getDownStationId().equals(stationId));
        //

    }
}
