package wooteco.subway.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import wooteco.subway.dao.LineDao;
import wooteco.subway.dao.SectionDao;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;

@Service
public class SectionService {
    private static final String ALREADY_IN_LINE_ERROR_MESSAGE = "이미 해당 이름의 노선이 있습니다.";
    private static final String NO_ID_ERROR_MESSAGE = "해당 아이디의 노선이 없습니다.";

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public SectionService(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Section saveSection(Section section) {
        Long id = sectionDao.save(section);
        return sectionDao.findById(id);
    }

    public List<Station> findStationsOfLine(Line line) {
        List<Station> result = new ArrayList<>();
        Sections sections = new Sections(sectionDao.findAllByLineId(line.getId()));

        return result;
    }

    public void deleteSection(Long lineId, Long stationId) {
        List<Section> sections = sectionDao.findAllByLineId(lineId);
        boolean isUpStation = sections.stream().anyMatch(section -> section.getUpStationId().equals(stationId));
        boolean isDownStation = sections.stream().anyMatch(section -> section.getDownStationId().equals(stationId));
        //

    }
}
