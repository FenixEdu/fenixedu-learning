package org.fenixedu.learning.ui;

import org.fenixedu.academic.domain.Degree;
import org.fenixedu.academic.domain.ExecutionCourse;
import org.fenixedu.cms.domain.Site;
import org.fenixedu.learning.domain.degree.DegreeSiteListener;
import org.fenixedu.learning.domain.executionCourse.ExecutionCourseListener;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by diutsu on 27/09/16.
 */
@Path("/learning/sites")
public class SpecificSitesAdmin {

    @POST
    @Path("/degree")
    public Response getDegreeSite(@FormParam("dsigla") String degreeSigla) throws URISyntaxException {
        Degree degree = Degree.readBySigla(degreeSigla);
        if(degree==null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if(degree.getSite()!=null){
            URI uri = new URI(degree.getSite().getEditUrl());
            return Response.temporaryRedirect(uri).build();
        }
        Site site = DegreeSiteListener.create(degree);
        URI uri = new URI(site.getEditUrl());
        return Response.temporaryRedirect(uri).build();
    }


    @POST
    @Path("/executionCourse")
    public Response getExecutionCourseSite(@FormParam("ecsigla") String executionCourseSigla) throws URISyntaxException {
        ExecutionCourse ec = ExecutionCourse.readLastBySigla(executionCourseSigla);
        if(ec==null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if(ec.getSite()!=null){
            URI uri = new URI(ec.getSite().getEditUrl());
            return Response.temporaryRedirect(uri).build();
        }
        Site site = ExecutionCourseListener.create(ec);
        URI uri = new URI(site.getEditUrl());
        return Response.temporaryRedirect(uri).build();
    }
}
