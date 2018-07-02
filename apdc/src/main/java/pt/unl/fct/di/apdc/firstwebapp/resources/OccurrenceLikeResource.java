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

import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.ListOccurrenceLikeData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceDeleteData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceLikeCountData;
import pt.unl.fct.di.apdc.firstwebapp.util.CursorList;
import pt.unl.fct.di.apdc.firstwebapp.util.SecurityManager;

@Path("/occurrence/like")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class OccurrenceLikeResource {

	private static final int QUERY_LIMIT = 20;
	private static final Logger LOG = Logger.getLogger(OccurrenceLikeResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public OccurrenceLikeResource() { } //Nothing to be done here...
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response commentOccurrence(OccurrenceDeleteData data) {
		LOG.fine("Attempt to like ocurrence with id: " + data.occurrenceID + " by user: " + data.token.username);
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to like occurrence, token for user: " + data.token.username + " is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.beginTransaction();
		try {
			Key userOccurrenceKey = KeyFactory.createKey("User", data.userID);
			Key occurrenceKey = KeyFactory.createKey(userOccurrenceKey, "UserOccurrence", data.occurrenceID);
			
			// Check occurrence existence
			datastore.get(txn, occurrenceKey);
			
			Key userKey = KeyFactory.createKey("User", data.token.userID);
			Key likeKey = KeyFactory.createKey(userKey, "UserOccurrenceLike", KeyFactory.keyToString(occurrenceKey));
			try {
				datastore.get(likeKey);
				datastore.delete(likeKey);
			} catch (EntityNotFoundException e) {
				Entity likeEntity = new Entity(likeKey);
				likeEntity.setProperty("like_date", new Date());
				datastore.put(likeEntity);
			}
			
			txn.commit();
			LOG.info("User " + data.token.username + " liked occurrence with id: " + data.occurrenceID);
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
	public Response ListOccurrenceComments(ListOccurrenceLikeData data) {
		LOG.fine("Attempt to list likes from user: " + data.userID + " by user: " + data.token.username);
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to list likes from user, token for user: " + data.token.username + " is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(!(data.token.userID == data.userID) && !SecurityManager.userHasAccess("see_liked_occurrences", data.token.userID)) {
			return Response.status(Status.FORBIDDEN).build();
		}
		
		Key userKey = KeyFactory.createKey("User", data.userID);
		
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(QUERY_LIMIT);
		if(data.cursor != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(data.cursor));
		}
		Query ctrQuery = new Query("UserOccurrenceLike").setAncestor(userKey).addSort("like_date", SortDirection.DESCENDING);
		QueryResultList<Entity> results = datastore.prepare(ctrQuery).asQueryResultList(fetchOptions);
		
		List<Map<String, Object>> occurrences = new LinkedList<Map<String, Object>>();
		Query ctrQueryMedia;
		Map<String, Object> occurrenceMap;
		List<Entity> mediaResults;
		List<String> mediaIDs;
		Entity occurrenceEntity;
		for(Entity likeEntity: results) {
			try {
				occurrenceEntity = datastore.get(KeyFactory.stringToKey(likeEntity.getKey().getName()));
				occurrenceMap = new HashMap<String, Object>();
				occurrenceMap.putAll(occurrenceEntity.getProperties());
				occurrenceMap.put("username", occurrenceEntity.getParent().getName());
				occurrenceMap.put("occurrenceID", String.valueOf(occurrenceEntity.getKey().getId()));
				occurrenceMap.put("userID", String.valueOf(occurrenceEntity.getParent().getId()));
				ctrQueryMedia = new Query("UserOccurrenceMedia").setAncestor(occurrenceEntity.getKey());
				mediaResults = datastore.prepare(ctrQueryMedia).asList(FetchOptions.Builder.withDefaults());
				mediaIDs = new LinkedList<String>();
				for(Entity occurrenceMediaEntity: mediaResults) {
					mediaIDs.add(String.valueOf(occurrenceMediaEntity.getKey().getId()));
				}
				occurrenceMap.put("mediaIDs", mediaIDs);
				occurrences.add(occurrenceMap);
			} catch (EntityNotFoundException e) {
				//continue
			}
		}
		
		CursorList cursorList = new CursorList(results.getCursor().toWebSafeString(), occurrences);
		LOG.info("User " + data.token.username + " listed occurrences with likes from user with id: " + data.userID);
		return Response.ok(g.toJson(cursorList)).build();
	}
	
	@SuppressWarnings("deprecation")
	@POST
	@Path("/count")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editCommentOccurrence(OccurrenceLikeCountData data) {
		LOG.fine("Attempt to count likes from occurrence with id: " + data.occurrenceID);
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		try {
			Key userKey = KeyFactory.createKey("User", data.userID);
			Key occurrenceKey = KeyFactory.createKey(userKey, "UserOccurrence", data.occurrenceID);
			
			// Check occurrence existence
			datastore.get(occurrenceKey);
			
			Query ctrQuery = new Query("UserOccurrenceLike").setKeysOnly();
			int count = datastore.prepare(ctrQuery).countEntities();
			
			LOG.info("Like count from occurrence with id " + data.occurrenceID + " sent");
			return Response.ok(g.toJson(count)).build();
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("Occurrence does not exist.").build();
		}
	}

}