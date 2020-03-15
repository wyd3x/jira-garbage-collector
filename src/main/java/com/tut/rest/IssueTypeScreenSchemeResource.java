package com.tut.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenScheme;
import com.atlassian.jira.issue.fields.screen.issuetype.IssueTypeScreenSchemeManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/issuetypescreenscheme")
public class IssueTypeScreenSchemeResource {

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
        IssueTypeScreenSchemeManager schemeManager = ComponentAccessor.getIssueTypeScreenSchemeManager();
        IssueTypeScreenScheme defaultScheme = schemeManager.getDefaultScheme();

        schemeManager.getIssueTypeScreenSchemes().forEach(screen -> {
            if(screen.getId() == defaultScheme.getId()) {
                // default, we will skip it
                return;
            }

            if(screen.getProjects().size() == 0) {
                System.out.println(String.format("Gonna delete '%s' scheme with id '%d'", screen.getName(), screen.getId()));
                // no associated projects, lets remove it
                //schemeManager.removeIssueTypeSchemeEntities(screen);
                //schemeManager.removeIssueTypeScreenScheme(screen);
            }
        });

        return Response.ok(new IssueTypeScreenSchemeResourceModel("id", "testMsg")).build();
    }

    @DELETE
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") long id) {
        IssueTypeScreenSchemeManager schemeManager = ComponentAccessor.getIssueTypeScreenSchemeManager();
        IssueTypeScreenScheme screen = schemeManager.getIssueTypeScreenScheme(id);
        //screen.remove();
        //TODO: add exception catch for not found
        System.out.println(String.format("Gonna delete '%s' scheme with id '%d'", screen.getName(), screen.getId()));

        return Response.ok(new IssueTypeScreenSchemeResourceModel("id", "testMsg")).build();
    }
}