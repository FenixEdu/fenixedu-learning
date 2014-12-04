package org.fenixedu.cms.domain.executionCourse.components;

import org.fenixedu.cms.domain.Page;
import org.fenixedu.cms.domain.component.CMSComponent;
import org.fenixedu.cms.domain.component.ComponentType;
import org.fenixedu.cms.rendering.TemplateContext;

@ComponentType(name = "ContentSearch", description = "Search content of an Execution Course")
public class ContentSearchComponent implements CMSComponent {
    @Override
    public void handle(Page page, TemplateContext componentContext, TemplateContext globalContext) {
    }
}
