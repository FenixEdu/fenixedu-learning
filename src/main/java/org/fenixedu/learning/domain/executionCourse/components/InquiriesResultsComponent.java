package org.fenixedu.learning.domain.executionCourse.components;

import static java.lang.String.format;

import java.util.Map;
import java.util.stream.Stream;

import net.sourceforge.fenixedu.domain.*;
import net.sourceforge.fenixedu.domain.inquiries.TeacherInquiryTemplate;
import net.sourceforge.fenixedu.domain.organizationalStructure.Unit;

import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.domain.executionCourse.ExecutionCourseSite;
import org.fenixedu.cms.rendering.TemplateContext;
import org.joda.time.DateTime;

import com.google.common.collect.Maps;

@ComponentType(name = "InquiriesResults", description = "Inquirires Results of an Execution Course")
public class InquiriesResultsComponent implements CMSComponent {

    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
        ExecutionCourse executionCourse = ((ExecutionCourseSite) page.getSite()).getExecutionCourse();
        globalContext.put("hasAccess", Authenticate.isLogged() && Authenticate.getUser().getPerson() != null);
        globalContext.put("institutionAcronym", Unit.getInstitutionAcronym());
        globalContext.put("notAvailableMessage", notAvailableMessage(executionCourse));
        globalContext.put("executionCourse", executionCourse);
        globalContext.put("professorships", professorshipWraps(executionCourse));
        globalContext.put("curricularCourses", curricularCoursesWraps(executionCourse));
        globalContext.put("loginUrl", CoreConfiguration.getConfiguration().applicationUrl() + "/login");
    }

    private Stream<Map<String, Object>> curricularCoursesWraps(ExecutionCourse executionCourse) {
        return executionCourse.getCurricularCoursesSortedByDegreeAndCurricularCourseName().stream()
                .map(curricularCourse -> curricularCourseWrapper(executionCourse, curricularCourse));
    }

    private Map<String, Object> curricularCourseWrapper(ExecutionCourse executionCourse, CurricularCourse curricularCourse) {
        Map<String, Object> wrap = Maps.newHashMap();
        DegreeCurricularPlan curricularPlan = curricularCourse.getDegreeCurricularPlan();
        wrap.put("degreeAcronym", curricularCourse.getDegreeCurricularPlan().getDegree().getSigla());
        wrap.put("url", format(inquiriesResultPageUrl(), executionCourse.getExternalId(), curricularPlan.getExternalId()));
        return wrap;
    }

    private Stream<Map<String, Object>> professorshipWraps(ExecutionCourse executionCourse) {
        return executionCourse.getProfessorshipsSet().stream()
                .filter(professorship -> !professorship.getInquiryResultsSet().isEmpty())
                .map(professorship -> professorshipWrap(professorship));
    }

    private Map<String, Object> professorshipWrap(Professorship professorship) {
        Map<String, Object> wrap = Maps.newHashMap();
        wrap.put("personName", professorship.getPerson().getName());
        wrap.put("shifts", professorship.getInquiryResultsSet().stream().map(inquirieResult->inquirieResult.getShiftType())
                .map(shiftType -> shiftTypeWrap(professorship, professorship.getExecutionCourse(), shiftType)).distinct());
        return wrap;
    }

    private Map<String, Object> shiftTypeWrap(Professorship professorship, ExecutionCourse executionCourse, ShiftType shiftType) {
        Map<String, Object> wrap = Maps.newHashMap();
        wrap.put("name", shiftType.getName());
        wrap.put(
                "url",
                format(teacherInquiriesResultPageUrl(), professorship.getExternalId(), shiftType, professorship
                        .getExecutionCourse().getExternalId()));
        return wrap;
    }

    private String notAvailableMessage(ExecutionCourse executionCourse) {
        TeacherInquiryTemplate teacherInquiryTemplate =
                TeacherInquiryTemplate.getTemplateByExecutionPeriod(executionCourse.getExecutionPeriod());
        if (teacherInquiryTemplate == null || teacherInquiryTemplate.getResponsePeriodBegin().plusDays(7).isAfter(new DateTime())) {
            return "message.inquiries.publicResults.notAvailable.m1";
        }
        if (executionCourse.getInquiryResultsSet().isEmpty()) {
            return "message.inquiries.publicResults.notAvailable";
        }
        return null;
    }

    private String inquiriesResultPageUrl() {
        return CoreConfiguration.getConfiguration().applicationUrl()
                + "/publico/executionCourse.do?method=dispatchToInquiriesResultPage&executionCourseID=%s&degreeCurricularPlanOID=%s";
    }

    private String teacherInquiriesResultPageUrl() {
        return CoreConfiguration.getConfiguration().applicationUrl()
                + "/publico/executionCourse.do?method=dispatchToTeacherInquiriesResultPage&professorshipOID=%s&shiftType=%s&executionCourseID=%s";
    }
}
