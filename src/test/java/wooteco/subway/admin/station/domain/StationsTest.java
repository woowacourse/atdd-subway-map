package wooteco.subway.admin.station.domain;

import com.google.common.collect.Sets;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.admin.common.exception.SubwayException;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StationsTest {

    @DisplayName("역 id로 구간 생성 불가능시 Exception")
    @Test
    void checkCreatableEdge() {
        //given
        Stations stations = new Stations(Sets.newHashSet(new Station(1L, "", LocalDateTime.now(), LocalDateTime.now())));
        List<Long> list = Lists.list(1L, 2L);

        //then
        assertThatThrownBy(() -> stations.checkCreatableEdge(list))
                .isInstanceOf(SubwayException.class)
                .hasMessage("%s : 생성할수 없는 구간 값 입니다.", list);
    }
}