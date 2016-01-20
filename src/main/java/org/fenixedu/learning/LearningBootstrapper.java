package org.fenixedu.learning;

import org.fenixedu.bennu.CmsBootstrapper;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrap;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.cms.domain.DefaultRoles;
import org.fenixedu.cms.domain.PermissionsArray;
import org.fenixedu.cms.domain.RoleTemplate;
import org.fenixedu.commons.i18n.LocalizedString;

import java.util.EnumSet;

/**
 * Created by diutsu on 21/03/16.
 */
@Bootstrapper(bundle = "resources.CmsResources", name = "application.title.cms.bootstrapper", after = CmsBootstrapper.class,
        sections = {})
public class LearningBootstrapper {

    private static final String DEFAULT_BUNDLE = "FenixEduLearningResources";

    private static final String TEACHER_ROLE = "teacher.role";

    @Bootstrap
    public static void bootstrapLearning() {
        RoleTemplate template = new RoleTemplate();

        EnumSet<PermissionsArray.Permission> permissions = DefaultRoles.getInstance().getAdminRole().getPermissions().get();
        permissions.remove(PermissionsArray.Permission.CHOOSE_PATH_AND_FOLDER);

        template.setPermissions(new PermissionsArray(permissions));
        template.setDescription(BundleUtil.getLocalizedString(DEFAULT_BUNDLE, TEACHER_ROLE));
    }
}
