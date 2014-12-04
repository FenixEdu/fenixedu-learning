package org.fenixedu.cms.domain.degree;

import net.sourceforge.fenixedu.domain.Degree;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.commons.i18n.LocalizedString;
import pt.ist.fenixframework.DomainObject;

import java.util.Optional;

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
}
