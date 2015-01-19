/**
 * Copyright © 2015 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Learning.
 *
 * FenixEdu Learning is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Learning is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Learning.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.learning.domain;

import static java.util.stream.Collectors.groupingBy;
import static org.fenixedu.academic.domain.ExecutionYear.REVERSE_COMPARATOR_BY_YEAR;
import static org.fenixedu.academic.domain.thesis.ThesisState.APPROVED;
import static org.fenixedu.academic.domain.thesis.ThesisState.CONFIRMED;
import static org.fenixedu.academic.domain.thesis.ThesisState.DRAFT;
import static org.fenixedu.academic.domain.thesis.ThesisState.EVALUATED;
import static org.fenixedu.academic.domain.thesis.ThesisState.REVISION;
import static org.fenixedu.academic.domain.thesis.ThesisState.SUBMITTED;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.thesis.Thesis;
import org.fenixedu.academic.domain.thesis.ThesisState;

import com.google.common.collect.Maps;

/**
 * Created by borgez on 25-11-2014.
 */
public class DissertationsUtils {

    private static Map<ThesisState, String> states;

    public static Map<ThesisState, String> getThesisStateMapping() {
        if (states == null) {
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
