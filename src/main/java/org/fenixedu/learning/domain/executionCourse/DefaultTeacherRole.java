package org.fenixedu.learning.domain.executionCourse;


import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.cms.domain.PermissionsArray;
import org.fenixedu.cms.domain.RoleTemplate;

import java.util.*;

public class DefaultTeacherRole  {

    private static final String TEACHER_ROLE_NAME = "teacher.role";
    private static final String DEFAULT_BUNDLE = "FenixEduLearningResources";
    private static DefaultTeacherRole instance;
    private RoleTemplate teacherRole;

    private DefaultTeacherRole() {
        init();
    }

    public static DefaultTeacherRole getInstance() {
        if (instance == null) {
            instance = new DefaultTeacherRole();
        }
        return instance;
    }

    public void init() {
        this.teacherRole = getOrCreateTemplateWithPermissions(getTeacherPermissions(), TEACHER_ROLE_NAME);
    }

    public RoleTemplate getOrCreateTemplateWithPermissions(Set<PermissionsArray.Permission> permissions, String description) {
        return getTemplateWithPermissions(permissions)
                .orElseGet(() -> createTemplate(permissions, description));
    }

    public Optional<RoleTemplate> getTemplateWithPermissions(Set<PermissionsArray.Permission> permissions) {
        Collection<RoleTemplate> templates = Bennu.getInstance().getRoleTemplatesSet();
        return templates.stream().filter(template -> template.getPermissions().get().equals(permissions)).findAny();
    }

    private RoleTemplate createTemplate(Set<PermissionsArray.Permission> permissions, String description) {
        RoleTemplate template = new RoleTemplate();
        template.setPermissions(new PermissionsArray(EnumSet.copyOf(permissions)));
        template.setDescription(BundleUtil.getLocalizedString(DEFAULT_BUNDLE, description));
        return template;
    }

    private Set<PermissionsArray.Permission> getTeacherPermissions() {
        Set<PermissionsArray.Permission> permissions = new HashSet<>();
        permissions.add(PermissionsArray.Permission.CREATE_POST);
        permissions.add(PermissionsArray.Permission.CREATE_PAGE);
        permissions.add(PermissionsArray.Permission.SEE_PAGES);
        permissions.add(PermissionsArray.Permission.SEE_PAGE_COMPONENTS);
        permissions.add(PermissionsArray.Permission.DELETE_OTHERS_POSTS);
        permissions.add(PermissionsArray.Permission.DELETE_PAGE);
        permissions.add(PermissionsArray.Permission.DELETE_POSTS);
        permissions.add(PermissionsArray.Permission.DELETE_PRIVATE_POSTS);
        permissions.add(PermissionsArray.Permission.DELETE_POSTS_PUBLISHED);
        permissions.add(PermissionsArray.Permission.EDIT_OTHERS_POSTS);
        permissions.add(PermissionsArray.Permission.EDIT_PAGE);
        permissions.add(PermissionsArray.Permission.EDIT_POSTS);
        permissions.add(PermissionsArray.Permission.EDIT_POSTS_PUBLISHED);
        permissions.add(PermissionsArray.Permission.LIST_CATEGORIES);
        permissions.add(PermissionsArray.Permission.EDIT_CATEGORY);
        permissions.add(PermissionsArray.Permission.DELETE_CATEGORY);
        permissions.add(PermissionsArray.Permission.CREATE_CATEGORY);
        permissions.add(PermissionsArray.Permission.MANAGE_ANALYTICS);
        permissions.add(PermissionsArray.Permission.MANAGE_ROLES);
        permissions.add(PermissionsArray.Permission.PUBLISH_PAGES);
        permissions.add(PermissionsArray.Permission.PUBLISH_POSTS);
        permissions.add(PermissionsArray.Permission.PUBLISH_SITE);
        permissions.add(PermissionsArray.Permission.CREATE_MENU);
        permissions.add(PermissionsArray.Permission.DELETE_MENU);
        permissions.add(PermissionsArray.Permission.LIST_MENUS);
        permissions.add(PermissionsArray.Permission.EDIT_MENU);
        permissions.add(PermissionsArray.Permission.CREATE_MENU_ITEM);
        permissions.add(PermissionsArray.Permission.DELETE_MENU_ITEM);
        permissions.add(PermissionsArray.Permission.EDIT_MENU_ITEM);
        permissions.add(PermissionsArray.Permission.CHANGE_PATH_PAGES);
        return permissions;
    }

    public RoleTemplate getTeacherRole() {
        return teacherRole;
    }
}