package org.fenixedu.learning.servlets;

import org.fenixedu.bennu.rendering.annotations.BennuIntersection;
import org.fenixedu.bennu.rendering.annotations.BennuIntersections;

/**
 * Created by diutsu on 27/09/16.
 */

@BennuIntersections({
        @BennuIntersection(location = "sites.manage", position = "creation.modals",
                file= "/templates/fenixedu-learning/creationModals.html")
})
public class ExecutionCourseAndDegreeSiteModalIntersection {
}
