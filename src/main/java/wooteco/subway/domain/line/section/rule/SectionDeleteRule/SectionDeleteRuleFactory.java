package wooteco.subway.domain.line.section.rule.SectionDeleteRule;

import java.util.Arrays;
import java.util.List;

public class SectionDeleteRuleFactory {

    public static List<SectionDeleteRule> create() {
        return Arrays.asList(
                new DownTerminalDelete(),
                new MiddleStationDelete(),
                new UpTerminalDelete()
        );
    }

}
