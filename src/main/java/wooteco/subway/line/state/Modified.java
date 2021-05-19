package wooteco.subway.line.state;

import wooteco.subway.common.exception.AlreadyDeletedException;
import wooteco.subway.common.exception.AlreadyModifiedException;
import wooteco.subway.line.domain.Section;
import wooteco.subway.line.domain.Sections;
import wooteco.subway.station.domain.Station;

public class Modified extends Change {
    protected Modified(Sections sections) {
        super(sections);
    }

    @Override
    public State addSection(Section targetSection) {
        throw new AlreadyModifiedException("이미 변경되었음!!");
    }

    @Override
    public State deleteStation(Station station) {
        throw new AlreadyDeletedException("이미 삭제되었음!!");
    }
}
