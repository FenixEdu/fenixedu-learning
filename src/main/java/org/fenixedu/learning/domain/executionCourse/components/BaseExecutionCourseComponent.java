package org.fenixedu.learning.domain.executionCourse.components;

import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseSite;

public abstract class BaseExecutionCourseComponent implements CMSComponent {

    public static boolean supportsSite(Site site) {
        return site instanceof ExecutionCourseSite;
    }

}
