package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.ListOccurrenceCommentData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceCommentData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceCommentDeleteData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceCommentEditData;
import pt.unl.fct.di.apdc.firstwebapp.util.CursorList;
import pt.unl.fct.di.apdc.firstwebapp.util.SecurityManager;

@Path("/occurrence/comment")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class OccurrenceCommentResource {

	private static final int QUERY_LIMIT = 20;
	private static final Logger LOG = Logger.getLogger(OccurrenceCommentResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public OccurrenceCommentResource() { } //Nothing to be done here...
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response commentOccurrence(OccurrenceCommentData data) {
		LOG.fine("Attempt to comment ocurrence with id: " + data.occurrenceID + " by user: " + data.token.username);
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to comment occurrence, token for user: " + data.token.username + " is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.beginTransaction();
		try {
			Key userKey = KeyFactory.createKey("User", data.userID);
			Key occurrenceKey = KeyFactory.createKey(userKey, "UserOccurrence", data.occurrenceID);
			
			// Check occurrence existence
			datastore.get(txn, occurrenceKey);
			
			Entity commentEntity = new Entity("UserOccurrenceComment", occurrenceKey);
			commentEntity.setProperty("comment_text", data.comment);
			commentEntity.setProperty("comment_date", new Date());
			commentEntity.setProperty("comment_userID", data.token.userID);
			
			txn.commit();
			LOG.info("User " + data.token.username + " commented occurrence with id: " + data.occurrenceID);
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("Occurrence does not exist.").build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ListOccurrenceComments(ListOccurrenceCommentData data) {
		LOG.fine("Attempt to list comments from occurrence: " + data.occurrenceID + " by user: " + data.token.username);
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to list comments from occurrence, token for user: " + data.token.username + " is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		Key userKey = KeyFactory.createKey("User", data.userID);
		Key occurrenceKey = KeyFactory.createKey(userKey, "UserOccurrence", data.occurrenceID);
		
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(QUERY_LIMIT);
		if(data.cursor != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(data.cursor));
		}
		Query ctrQuery = new Query("UserOccurrenceComment").setAncestor(occurrenceKey).addSort("comment_date", SortDirection.DESCENDING);
		QueryResultList<Entity> results = datastore.prepare(ctrQuery).asQueryResultList(fetchOptions);
		
		List<Map<String, Object>> comments = new LinkedList<Map<String, Object>>();
		Map<String, Object> commentMap;
		for(Entity commentEntity: results) {
			commentMap = new HashMap<String, Object>();
			commentMap.putAll(commentEntity.getProperties());
			commentMap.put("commentID", String.valueOf(commentEntity.getKey().getId()));
		}
		
		CursorList cursorList = new CursorList(results.getCursor().toWebSafeString(), comments);
		LOG.info("User " + data.token.username + " listed comments from occurrence with id: " + data.occurrenceID);
		return Response.ok(g.toJson(cursorList)).build();
	}
	
	@POST
	@Path("/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editCommentOccurrence(OccurrenceCommentEditData data) {
		LOG.fine("Attempt to edit comment with id: " + data.commentID + " from ocurrence with id: " + data.occurrenceID + " by user: " + data.token.username);
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to edit comment, token for user: " + data.token.username + " is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.beginTransaction();
		try {
			Key userKey = KeyFactory.createKey("User", data.userID);
			Key occurrenceKey = KeyFactory.createKey(userKey, "UserOccurrence", data.occurrenceID);
			Key commentKey = KeyFactory.createKey(occurrenceKey, "UserOccurrenceComment", data.commentID);
			
			// Check comment existence
			Entity commentEntity = datastore.get(txn, commentKey);
			
			if(!(data.token.userID == ((Long)(commentEntity.getProperty("comment_userID")))) && !SecurityManager.userHasAccess("edit_user_occurrence_comment", data.token.userID)) {
				LOG.warning("Failed to edit comment, user: " + data.token.username + " does not have the rights to do it");
				return Response.status(Status.FORBIDDEN).build();
			}
			
			commentEntity.setProperty("comment_text", data.comment);
			
			txn.commit();
			LOG.info("User " + data.token.username + " edited comment with id: " + data.commentID);
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("Comment does not exist.").build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteCommentOccurrence(OccurrenceCommentDeleteData data) {
		LOG.fine("Attempt to delete comment with id: " + data.commentID + " from ocurrence with id: " + data.occurrenceID + " by user: " + data.token.username);
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to delete comment, token for user: " + data.token.username + " is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.beginTransaction();
		try {
			Key userKey = KeyFactory.createKey("User", data.userID);
			Key occurrenceKey = KeyFactory.createKey(userKey, "UserOccurrence", data.occurrenceID);
			Key commentKey = KeyFactory.createKey(occurrenceKey, "UserOccurrenceComment", data.commentID);
			
			// Check comment existence
			Entity commentEntity = datastore.get(txn, commentKey);
			
			if(!(data.token.userID == ((Long)(commentEntity.getProperty("comment_userID")))) && !SecurityManager.userHasAccess("edit_user_occurrence_comment", data.token.userID)) {
				LOG.warning("Failed to delete comment, user: " + data.token.username + " does not have the rights to do it");
				return Response.status(Status.FORBIDDEN).build();
			}
			
			datastore.delete(txn, commentKey);
			
			txn.commit();
			LOG.info("User " + data.token.username + " deleted comment with id: " + data.commentID);
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("Comment does not exist.").build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}

}
