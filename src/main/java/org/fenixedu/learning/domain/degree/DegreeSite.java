package org.fenixedu.learning.domain.degree;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.cms.domain.Category;
import org.fenixedu.cms.domain.wraps.Wrap;
import org.fenixedu.commons.i18n.LocalizedString;

import pt.ist.fenixframework.DomainObject;

public class DegreeSite extends DegreeSite_Base {

    public DegreeSite(Degree degree) {
        super();
        setDegree(degree);
        setBennu(Bennu.getInstance());
    }

    @Override
    public LocalizedString getName() {
        return Optional.ofNullable(super.getName()).orElse(getDegree().getNameI18N().toLocalizedString());
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
