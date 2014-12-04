package org.fenixedu.cms.domain;

import com.google.common.collect.Maps;
import net.sourceforge.fenixedu.domain.Degree;
import net.sourceforge.fenixedu.domain.ExecutionYear;
import net.sourceforge.fenixedu.domain.thesis.Thesis;
import net.sourceforge.fenixedu.domain.thesis.ThesisState;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static net.sourceforge.fenixedu.domain.ExecutionYear.REVERSE_COMPARATOR_BY_YEAR;
import static net.sourceforge.fenixedu.domain.thesis.ThesisState.*;
import static net.sourceforge.fenixedu.domain.thesis.ThesisState.REVISION;
import static net.sourceforge.fenixedu.domain.thesis.ThesisState.SUBMITTED;

/**
 * Created by borgez on 25-11-2014.
 */
public class DissertationsUtils {

    private static Map<ThesisState, String> states;

    public static Map<ThesisState, String> getThesisStateMapping() {
        if(states == null) {
            states = Maps.newHashMap();
            states.put(EVALUATED, "success");
            states.put(CONFIRMED, "primary");
            states.put(DRAFT, "default");
            states.put(APPROVED, "info");
            states.put(REVISION, "warning");
            states.put(SUBMITTED, "primary");
        }
        return states;
    }

    public static SortedMap<ExecutionYear, List<Thesis>> allThesesByYear(Collection<Degree> degrees) {
        TreeMap<ExecutionYear, List<Thesis>> thesesByYear = Maps.newTreeMap(REVERSE_COMPARATOR_BY_YEAR);
        Stream<Thesis> allTheses = degrees.stream().flatMap(degree -> degree.getThesisSet().stream());
        thesesByYear.putAll(allTheses.collect(groupingBy(Thesis::getExecutionYear)));
        return thesesByYear;
    }
}
