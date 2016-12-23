package org.fenixedu.learning.ui;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.cms.exceptions.CmsDomainException;
import org.fenixedu.learning.domain.degree.DegreeSiteListener;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by diutsu on 27/09/16.
 */
@Controller
@RequestMapping("/learning/sites/")
public class SpecificSitesAdmin {
    
    @RequestMapping(value = "/degree", method = RequestMethod.POST)
    public RedirectView getDegreeSite(@RequestParam("dsigla") String degreeSigla) throws URISyntaxException {
        Degree degree = Degree.readBySigla(degreeSigla);
        if(degree==null){
            throw CmsDomainException.notFound();
        }
        if(degree.getSite()!=null){
            Site site = degree.getSite();
            return new RedirectView(site.getEditUrl());
        }
        Site site = DegreeSiteListener.create(degree);
        return new RedirectView(site.getEditUrl());
        
    }


    @RequestMapping(value = "/executionCourse", method = RequestMethod.POST)
    public RedirectView getExecutionCourseSite(@RequestParam("ecsigla") String executionCourseSigla) throws URISyntaxException {
        ExecutionCourse ec = ExecutionCourse.readLastBySigla(executionCourseSigla);
        if(ec==null){
            throw CmsDomainException.notFound();
        }
        if(ec.getSite()!=null){
            Site site = ec.getSite();
            return new RedirectView(site.getEditUrl());
        }
        Site site = ExecutionCourseListener.create(ec);
        return new RedirectView(site.getEditUrl());
    }
}
