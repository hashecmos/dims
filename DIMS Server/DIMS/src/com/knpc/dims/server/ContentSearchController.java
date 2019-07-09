package com.knpc.dims.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.knpc.dims.filenet.beans.SearchResultBean;
import com.knpc.dims.filenet.service.ContentSearchService;
import com.knpc.dims.response.ResponseObject;

@Path("/FilenetSearchService")
@ApplicationPath("resources")
public class ContentSearchController {

	// http://localhost:9080/DIMS/resources/FilenetSearchService/simpleSearch
	@POST
	@Path("/simpleSearch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)//@Produces(MediaType.APPLICATION_XML)	
	public Response simpleSearch(String jsonString,@Context HttpServletRequest req,@Context HttpServletResponse resp) throws Exception{
		SearchResultBean searchResultBean = null;
		try{
			ContentSearchService css = new ContentSearchService(req,resp);
			searchResultBean = css.simpleSearch(jsonString);
			//return searchResultBean;
		}catch (Exception e) {
			ResponseObject responseObject = ResponseObject.getResponseObject(e.getMessage());
			return Response.status(Status.BAD_REQUEST).entity(responseObject).build();
		}
		return Response.ok().entity(searchResultBean).build();
		
	}
}	
	
