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
package org.fenixedu.learning.domain.degree;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenixframework.DomainObject;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class DegreeSite extends DegreeSite_Base {

    public DegreeSite(Degree degree) {
        super();
        setDegree(degree);
        setBennu(Bennu.getInstance());
    }

    @Override
    public LocalizedString getName() {
        if (super.getName() == null) {
            if (getDegree().getPhdProgram() != null) {
                return new LocalizedString().with(Locale.getDefault(), getDegree().getPhdProgram().getPresentationName());
            } else {
                return new LocalizedString().with(Locale.getDefault(), getDegree().getPresentationName());
            }
        } else {
            return super.getName();
        }
    }

    @Override
    public LocalizedString getDescription() {
        return Optional.ofNullable(super.getDescription()).orElse(getName());
    }

    @Override
    public DomainObject getObject() {
        return getDegree();
    }

    @Override
    public void delete() {
        this.setDegree(null);
        this.setBennu(null);
        super.delete();
    }

    public Stream<Wrap> getCategoriesToShow() {
        return Stream.of(categoryForSlug("announcement")).filter(Objects::nonNull).map(Category::makeWrap);
    }

}
