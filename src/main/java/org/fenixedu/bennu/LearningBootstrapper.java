package org.fenixedu.bennu;

import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrap;
import org.fenixedu.bennu.core.bootstrap.annotations.Bootstrapper;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.cms.domain.PermissionsArray;
import org.fenixedu.cms.domain.RoleTemplate;
import org.fenixedu.learning.domain.degree.DegreeSiteBuilder;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSiteBuilder;

import java.util.EnumSet;


/**
 * Created by diutsu on 21/03/16.
 */
@Bootstrapper(bundle = "resources.CmsResources", name = "application.title.cms.bootstrapper", after = CmsBootstrapper.class,
        sections = {})
public class LearningBootstrapper {

    private static final String DEFAULT_BUNDLE = "FenixEduLearningResources";

    private static final String TEACHER_ROLE = "teacher.role";
    private static final String DEPARTMENT_ROLE = "department.manager.role";

    @Bootstrap
    public static void bootstrapLearning() {
        
        //   ExecutionCourse
        RoleTemplate template = new RoleTemplate();
        template.setPermissions(getTeacherPermissions());
        template.setName(BundleUtil.getLocalizedString(DEFAULT_BUNDLE, TEACHER_ROLE));
        ExecutionCourseSiteBuilder ecBuilder = ExecutionCourseSiteBuilder.getInstance();
        ecBuilder.addRoleTemplate(template);
    
        // Department
        RoleTemplate departmentRoleTemplate = new RoleTemplate();
        departmentRoleTemplate.setPermissions(getTeacherPermissions());
        departmentRoleTemplate.setName(BundleUtil.getLocalizedString(DEFAULT_BUNDLE, DEPARTMENT_ROLE));
    
        DegreeSiteBuilder dpBuilder = DegreeSiteBuilder.getInstance();
        dpBuilder.addRoleTemplate(departmentRoleTemplate);

    }
    
    
    private static PermissionsArray getTeacherPermissions() {
        return new PermissionsArray(EnumSet.of(
        PermissionsArray.Permission.CREATE_POST,
        PermissionsArray.Permission.CREATE_PAGE,
        PermissionsArray.Permission.SEE_PAGES,
        PermissionsArray.Permission.SEE_POSTS,
        PermissionsArray.Permission.SEE_PAGE_COMPONENTS,
        PermissionsArray.Permission.DELETE_OTHERS_POSTS,
        PermissionsArray.Permission.DELETE_PAGE,
        PermissionsArray.Permission.DELETE_POSTS,
        PermissionsArray.Permission.DELETE_PRIVATE_POSTS,
        PermissionsArray.Permission.DELETE_POSTS_PUBLISHED,
        PermissionsArray.Permission.EDIT_OTHERS_POSTS,
        PermissionsArray.Permission.EDIT_PAGE,
        PermissionsArray.Permission.EDIT_POSTS,
        PermissionsArray.Permission.EDIT_POSTS_PUBLISHED,
        PermissionsArray.Permission.LIST_CATEGORIES,
        PermissionsArray.Permission.EDIT_CATEGORY,
        PermissionsArray.Permission.DELETE_CATEGORY,
        PermissionsArray.Permission.CREATE_CATEGORY,
        PermissionsArray.Permission.MANAGE_ANALYTICS,
        PermissionsArray.Permission.MANAGE_ROLES,
        PermissionsArray.Permission.PUBLISH_PAGES,
        PermissionsArray.Permission.PUBLISH_POSTS,
        PermissionsArray.Permission.PUBLISH_SITE,
        PermissionsArray.Permission.CREATE_MENU,
        PermissionsArray.Permission.DELETE_MENU,
        PermissionsArray.Permission.LIST_MENUS,
        PermissionsArray.Permission.EDIT_MENU,
        PermissionsArray.Permission.CREATE_MENU_ITEM,
        PermissionsArray.Permission.DELETE_MENU_ITEM,
        PermissionsArray.Permission.EDIT_MENU_ITEM,
        PermissionsArray.Permission.CHANGE_PATH_PAGES));
    }
    
    
}
