package wooteco.subway.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.dto.SectionRequest;

@Service
public class SectionService {

    private final SectionDao sectionDao;
    private final LineDao lineDao;

    public SectionService(SectionDao sectionDao, LineDao lineDao) {
        this.sectionDao = sectionDao;
        this.lineDao = lineDao;
    }

    public Section createSection(Long lineId, SectionRequest sectionRequest) {
        Optional<Line> upStation = lineDao.findById(sectionRequest.getUpStationId());
        Optional<Line> downStation = lineDao.findById(sectionRequest.getDownStationId());

        // 상행 종점 등록
        if (upStation.isEmpty() && downStation.isPresent()) {
            // TODO: 갈래길 방지 및 거리 validate
        }

        // 하행 종점 등록
        if (upStation.isPresent() && downStation.isEmpty()) {
            // TODO: 갈래길 방지 및 거리 valdiate
        }

        if (upStation.isPresent() && downStation.isPresent()) {
            // 모두 등록되어 있으므로 추가 불가능 상황
        }

        if (upStation.isEmpty() && downStation.isEmpty()) {
            // 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음
        }

        Section section = new Section(lineId, sectionRequest.getUpStationId(), sectionRequest.getDownStationId(),
                sectionRequest.getDistance());
        Section saveSection = sectionDao.save(section);
        return saveSection;
    }
}
