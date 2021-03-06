package com.assertteam.rest;

import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.List;

import com.assertteam.rest.utils.Auth;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import com.atlassian.jira.scheme.Scheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import com.atlassian.jira.workflow.AssignableWorkflowScheme;
import org.springframework.beans.factory.annotation.Autowired;

@Component
@Path("/workflowscheme")
public class WorkflowSchemeResource {
    private Auth auth;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public WorkflowSchemeResource(Auth auth) {
        this.auth = auth;
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/")
    public Response gcNotDefault() {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.logger.info("GC - REST - Delete - WorkflowScheme - / - Started");
        ArrayList<DeleteModel> deleted = new ArrayList<>();
        WorkflowSchemeManager schemeManager = ComponentAccessor.getWorkflowSchemeManager();

        List<Scheme> schemeObjects = schemeManager.getSchemeObjects();

        schemeObjects.forEach(scheme -> {
            AssignableWorkflowScheme wf = schemeManager.getWorkflowSchemeObj(scheme.getId());
            if(wf != null) {
                if(schemeManager.getProjectsUsing(wf).size() == 0 && !wf.isDefault()) {
                    deleted.add(new DeleteModel("WorkflowScheme", String.format("ID: '%d', Name: '%s' deleted", wf.getId(), wf.getName())));
                    schemeManager.deleteWorkflowScheme(wf);
                }
            }
        });

        this.logger.info("GC - REST - Delete - WorkflowScheme - / - Deleted");
        return Response.ok(deleted).build();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response gcForKey(@PathParam("id") int id) {
        if (!auth.canAccess()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        this.logger.info(String.format("GC - REST - Delete - WorkflowScheme - /%d - Started", id));
        WorkflowSchemeManager schemeManager = ComponentAccessor.getWorkflowSchemeManager();
        AssignableWorkflowScheme workflowScheme = schemeManager.getWorkflowSchemeObj(id);

        if(workflowScheme == null) {
            return Response.status(404).build();
        }
        schemeManager.deleteWorkflowScheme(workflowScheme);

        this.logger.info(String.format("GC - REST - Delete - WorkflowScheme - /%d - Deleted", id));
        return Response.ok(new DeleteModel("WorkflowScheme", String.format("ID: '%d', Name: '%s' deleted", workflowScheme.getId(), workflowScheme.getName()))).build();
    }
}
