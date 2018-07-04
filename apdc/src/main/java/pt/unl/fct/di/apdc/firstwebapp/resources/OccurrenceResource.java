package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
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
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.ListOccurrenceData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceDeleteData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceEditData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceMediaData;
import pt.unl.fct.di.apdc.firstwebapp.util.CursorList;
import pt.unl.fct.di.apdc.firstwebapp.util.GcsManager;
import pt.unl.fct.di.apdc.firstwebapp.util.ListIds;
import pt.unl.fct.di.apdc.firstwebapp.util.SecurityManager;
import pt.unl.fct.di.apdc.firstwebapp.util.geocalc.BoundingArea;
import pt.unl.fct.di.apdc.firstwebapp.util.geocalc.Coordinate;
import pt.unl.fct.di.apdc.firstwebapp.util.geocalc.EarthCalc;
import pt.unl.fct.di.apdc.firstwebapp.util.geocalc.Point;

@Path("/occurrence")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class OccurrenceResource {

	private static final int QUERY_LIMIT = 100;
	private static final Logger LOG = Logger.getLogger(OccurrenceResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public OccurrenceResource() { } //Nothing to be done here...
	
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerOccurrence(OccurrenceData data) {
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		LOG.fine("Attempt to register ocurrence: " + data.title + " by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to register occurrence, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {
			Key userKey = KeyFactory.createKey("User", data.token.userID);
			
			// Save occurrence
			Entity occurrenceEntity = new Entity("UserOccurrence", userKey);
			occurrenceEntity.setProperty("user_occurrence_title", data.title);
			occurrenceEntity.setProperty("user_occurrence_description", data.description);
			occurrenceEntity.setProperty("user_occurrence_date", new Date());
			occurrenceEntity.setProperty("user_occurrence_type", data.type);
			occurrenceEntity.setProperty("user_occurrence_level", data.level);
			occurrenceEntity.setProperty("user_occurrence_visibility", data.visibility);
			occurrenceEntity.setProperty("user_occurrence_lat", data.lat);
			occurrenceEntity.setProperty("user_occurrence_lon", data.lon);
			occurrenceEntity.setProperty("user_occurrence_notification_on_resolve", data.notificationOnResolve);
			occurrenceEntity.setProperty("user_occurrence_status", "OPEN");
			datastore.put(txn, occurrenceEntity);
			
			// Create occurrence media entities
			if(data.uploadMedia) {
				List<Entity> mediaEntities = new LinkedList<Entity>();
				List<Entity> uploadEntities = new LinkedList<Entity>();
				List<Long> uploadMediaIDs = new LinkedList<Long>();
				Entity occurrenceMediaEntity;
				long occurrenceID = occurrenceEntity.getKey().getId();
				for(int i = 0; i < data.nUploads; i++) {
					occurrenceMediaEntity = new Entity("UserOccurrenceMedia", occurrenceEntity.getKey());
					mediaEntities.add(occurrenceMediaEntity);
				}
				datastore.put(txn, mediaEntities);
				txn.commit();
				txn = datastore.beginTransaction(options);
				for(Entity mediaEntity : mediaEntities) {
					Entity fileUpload = UploadResource.newUploadFileEntity(
							"user/" + data.token.userID + "/occurrence/" + occurrenceID + "/", 
							String.valueOf(mediaEntity.getKey().getId()), 
							"IMAGE&VIDEO",
							data.visibility,
							false
							);
					uploadEntities.add(fileUpload);
				}
				datastore.put(txn, uploadEntities);
				txn.commit();
				for(Entity uploadEntity: uploadEntities) {
					uploadMediaIDs.add(uploadEntity.getKey().getId());
				}
				LOG.info("User " + data.token.username + " registered occurrence " + data.title);
				return Response.ok(g.toJson(new ListIds(uploadMediaIDs))).build();
			}
			else {
				txn.commit();
				LOG.info("User " + data.token.username + " registered occurrence " + data.title);
				return Response.ok().build();
			}
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/list")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listOccurrences(ListOccurrenceData data) {
		LOG.fine("Attempt to send list of occurrences");
		if(data == null) {
			return Response.status(Status.FORBIDDEN).build();
		}
		if(data.token != null && !data.token.isTokenValid()) {
			LOG.warning("Failed to send list of occurrences, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		
		Query ctrQuery = new Query("UserOccurrence");
		if(data.username != null) {
			FilterPredicate filter = new FilterPredicate("user_username", FilterOperator.EQUAL, data.username);
			Query userQuery = new Query("User").setFilter(filter);
			List<Entity> results = datastore.prepare(userQuery).asList(FetchOptions.Builder.withDefaults());
			if(results.isEmpty()) {
				// Username does not exist
				LOG.warning("Searched username does not exist: " + data.username);
				return Response.status(Status.BAD_REQUEST).build();
			}
			Entity user = results.get(0);
			Key userKey = user.getKey();
			ctrQuery.setAncestor(userKey);
		}
		FilterPredicate visibilityFilter = null;
		if(!data.showPrivate) {
			visibilityFilter = new FilterPredicate("user_occurrence_visibility", FilterOperator.EQUAL, true);
			ctrQuery.setFilter(visibilityFilter);
		}
		else if(!data.token.username.equals(data.username) && !SecurityManager.userHasAccess("see_private_occurrences", data.token.userID)) {
			return Response.status(Status.FORBIDDEN).build();
		}
		Point nw = null;
		Point se = null;
		if(data.lat != null && data.lon != null) {
			Point geoPoint = Point.at(Coordinate.fromDegrees(data.lat), Coordinate.fromDegrees(data.lon));
			BoundingArea area = EarthCalc.around(geoPoint, data.radius);
			nw = area.northWest;
			se = area.southEast;
			FilterPredicate geoFilterUpperLat = new FilterPredicate("user_occurrence_lat", FilterOperator.LESS_THAN_OR_EQUAL, nw.latitude);
			FilterPredicate geoFilterLowerLat = new FilterPredicate("user_occurrence_lat", FilterOperator.GREATER_THAN_OR_EQUAL, se.latitude);
			//FilterPredicate geoFilterLeftmostLon = new FilterPredicate("user_occurrence_lon", FilterOperator.GREATER_THAN_OR_EQUAL, nw.longitude);
			//FilterPredicate geoFilterRightmostLon = new FilterPredicate("user_occurrence_lon", FilterOperator.LESS_THAN_OR_EQUAL, se.longitude);
			CompositeFilter compositeFilter;
			if(visibilityFilter != null) {
				compositeFilter = CompositeFilterOperator.and(visibilityFilter, geoFilterUpperLat, geoFilterLowerLat/*, geoFilterLeftmostLon, geoFilterRightmostLon*/);
			}
			else {
				compositeFilter = CompositeFilterOperator.and(geoFilterUpperLat, geoFilterLowerLat/*, geoFilterLeftmostLon, geoFilterRightmostLon*/);
			}
			ctrQuery.setFilter(compositeFilter);
		}
		else {
			ctrQuery.addSort("user_occurrence_date", SortDirection.DESCENDING);
		}
		
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(QUERY_LIMIT);
		if(data.cursor != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(data.cursor));
		}
		QueryResultList<Entity> results = datastore.prepare(ctrQuery).asQueryResultList(fetchOptions);
		
		List<Map<String, Object>> occurrences = new LinkedList<Map<String, Object>>();
		Query ctrQueryMedia;
		Map<String, Object> occurrenceMap;
		List<Entity> mediaResults;
		List<String> mediaIDs;
		for(Entity occurrenceEntity: results) {
			if(data.lat != null && data.lon != null) {
				if((double)(occurrenceEntity.getProperty("user_occurrence_lon")) < nw.longitude && (double)(occurrenceEntity.getProperty("user_occurrence_lon")) > se.longitude) {
					continue;
				}
			}
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
		}
		CursorList cursorList = new CursorList(results.getCursor().toWebSafeString(), occurrences);
		LOG.info("List of occurrences sent");
		return Response.ok(g.toJson(cursorList)).build();
	}
	
	@GET
	@Path("/list")
	public Response listOccurrences() {
		return listOccurrences(new ListOccurrenceData());
	}
	
	@POST
	@Path("/edit")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response editOccurrence(OccurrenceEditData data) {
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {
			LOG.fine("Attempt to edit ocurrence with id: " + data.occurrenceID + " by user: " + data.token.username);
			if(!data.valid()) {
				return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
			}
			if(!data.token.isTokenValid()) {
				LOG.warning("Failed to edit occurrence, token for user: " + data.token.username + "is invalid");
				return Response.status(Status.FORBIDDEN).build();
			}
			if(!(data.token.userID == data.userID) && !SecurityManager.userHasAccess("edit_user_occurrence", data.token.userID)) {
				LOG.warning("Failed to edit occurrence, user: " + data.token.username + " does not have the rights to do it");
				return Response.status(Status.FORBIDDEN).build();
			}
		
			Key userKey = KeyFactory.createKey("User", data.userID);
			Key occurrenceKey = KeyFactory.createKey(userKey, "UserOccurrence", data.occurrenceID);
			
			// Save occurrence
			Entity occurrenceEntity = datastore.get(txn, occurrenceKey);
			if(data.title != null && !data.title.isEmpty()) {
				occurrenceEntity.setProperty("user_occurrence_title", data.title);
			}
			if(data.description != null && !data.description.isEmpty()) {
				occurrenceEntity.setProperty("user_occurrence_description", data.description);
			}
			if(data.visibility != null) {
				occurrenceEntity.setProperty("user_occurrence_visibility", data.visibility);
			}
			if(data.notificationOnResolve != null) {
				occurrenceEntity.setProperty("user_occurrence_notification_on_resolve", data.notificationOnResolve);
			}
			datastore.put(txn, occurrenceEntity);
			
			// Create occurrence media entities
			if(data.uploadMedia) {
				List<Entity> mediaEntities = new LinkedList<Entity>();
				List<Entity> uploadEntities = new LinkedList<Entity>();
				List<Long> uploadMediaIDs = new LinkedList<Long>();
				Entity occurrenceMediaEntity;
				long occurrenceID = occurrenceEntity.getKey().getId();
				for(int i = 0; i < data.nUploads; i++) {
					occurrenceMediaEntity = new Entity("UserOccurrenceMedia", occurrenceEntity.getKey());
					mediaEntities.add(occurrenceMediaEntity);
				}
				datastore.put(txn, mediaEntities);
				txn.commit();
				txn = datastore.beginTransaction(options);
				for(Entity mediaEntity : mediaEntities) {
					Entity fileUpload = UploadResource.newUploadFileEntity(
							"user/" + data.token.userID + "/occurrence/" + occurrenceID + "/", 
							String.valueOf(mediaEntity.getKey().getId()), 
							"IMAGE&VIDEO",
							data.visibility,
							false
							);
					uploadEntities.add(fileUpload);
				}
				datastore.put(txn, uploadEntities);
				txn.commit();
				for(Entity uploadEntity: uploadEntities) {
					uploadMediaIDs.add(uploadEntity.getKey().getId());
				}
				LOG.info("User " + data.token.username + " edited occurrence with id: " + data.occurrenceID);
				return Response.ok(g.toJson(new ListIds(uploadMediaIDs))).build();
			}
			else {
				txn.commit();
				LOG.info("User " + data.token.username + " edited occurrence with id: " + data.occurrenceID);
				return Response.ok().build();
			}
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("Occurrence not found.").build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteOccurrence(OccurrenceDeleteData data) {
		LOG.fine("Attempt to delete ocurrence with id: " + data.occurrenceID + " by user: " + data.token.username);
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to delete occurrence, token for user: " + data.token.username + " is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(!(data.token.userID == data.userID) && !SecurityManager.userHasAccess("delete_user_occurrence", data.token.userID)) {
			LOG.warning("Failed to delete occurrence, user: " + data.token.username + " does not have the rights to do it");
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.beginTransaction();
		try {
			Key userKey = KeyFactory.createKey("User", data.userID);
			Key occurrenceKey = KeyFactory.createKey(userKey, "UserOccurrence", data.occurrenceID);
			
			// Delete occurrence
			datastore.delete(txn, occurrenceKey);
			
			txn.commit();
			LOG.info("User " + data.token.username + " deleted occurrence with id: " + data.occurrenceID);
			return Response.ok().build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
		/**TODO
		 * delete media, comments, likes, etc
		 */
	}
	
	@POST
	@Path("/media")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response occurrenceMedia(@Context HttpServletRequest req, @Context HttpServletResponse resp, OccurrenceMediaData data) {
		try {
			LOG.fine("Attempt to get media with id " + data.mediaID + " for ocurrence with id " + data.occurrenceID + " by user: " + data.token.username);
			if(!data.valid()) {
				return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
			}
			if(!data.token.isTokenValid()) {
				LOG.warning("Failed to get occurrence media, token for user: " + data.token.username + "is invalid");
				return Response.status(Status.FORBIDDEN).build();
			}
			if(!(data.token.userID == data.userID) && !SecurityManager.userHasAccess("see_private_occurrences", data.token.userID)) {
				LOG.warning("Failed to get occurrence media, user: " + data.token.username + " does not have the rights to do it");
				return Response.status(Status.FORBIDDEN).build();
			}
		
			Key userKey = KeyFactory.createKey("User", data.userID);
			Key occurrenceKey = KeyFactory.createKey(userKey, "UserOccurrence", data.occurrenceID);
			Key mediaKey = KeyFactory.createKey(occurrenceKey, "UserOccurrenceMedia", data.mediaID);
			
			// Check media existence
			datastore.get(mediaKey);
			
			if(GcsManager.gcsGet(resp, "user/" + data.userID + "/occurrence/" + data.occurrenceID + "/" + data.mediaID)) {
				LOG.info("User " + data.token.username + " got media with id " + data.mediaID + " for occurrence with id " + data.occurrenceID);
				return Response.created(null).status(HttpServletResponse.SC_OK).build();
			}
			else {
				LOG.warning("Failed to get occurrence media, couldn't retrieve media from storage");
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("Media not found.").build();
		}
	}
	
}
