package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.ListOccurrenceData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceDeleteData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceEditData;
import pt.unl.fct.di.apdc.firstwebapp.util.CursorList;
import pt.unl.fct.di.apdc.firstwebapp.util.SecurityManager;

@Path("/occurrence")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class OccurrenceResource {

	private static final int QUERY_LIMIT = 100;
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
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
			Key userKey = KeyFactory.createKey("User", data.token.username);
			
			// Save occurrence
			Entity occurrenceEntity = new Entity("UserOccurrence", userKey);
			occurrenceEntity.setProperty("user_occurrence_title", data.title);
			occurrenceEntity.setProperty("user_occurrence_description", data.description);
			occurrenceEntity.setProperty("user_occurrence_data", new Date());
			occurrenceEntity.setProperty("user_occurrence_type", data.type);
			occurrenceEntity.setProperty("user_occurrence_level", data.level);
			occurrenceEntity.setProperty("user_occurrence_visibility", data.visibility);
			occurrenceEntity.setProperty("user_occurrence_lat", data.lat);
			occurrenceEntity.setProperty("user_occurrence_lon", data.lon);
			datastore.put(txn, occurrenceEntity);
			
			// Create occurrence media entities
			if(data.uploadMedia) {
				List<Entity> mediaEntities = new LinkedList<Entity>();
				List<Long> uploadMediaIDs = new LinkedList<Long>();
				Entity occurrenceMediaEntity;
				long occurrenceID = occurrenceEntity.getKey().getId();
				for(int i = 0; i < data.nUploads; i++) {
					occurrenceMediaEntity = new Entity("UserOccurrenceMedia", occurrenceEntity.getKey());
					mediaEntities.add(occurrenceMediaEntity);
					Entity fileUpload = UploadResource.newUploadFileEntity(
							"user/" + data.token.username + "/occurrence/" + occurrenceID + "/", 
							String.valueOf(occurrenceMediaEntity.getKey().getId()), 
							"IMAGE&VIDEO",
							data.visibility,
							false
							);
					uploadMediaIDs.add(fileUpload.getKey().getId());
					mediaEntities.add(fileUpload);
				}
				datastore.put(txn, mediaEntities);
				txn.commit();
				LOG.info("User " + data.token.username + " registered occurrence " + data.title);
				return Response.ok(g.toJson(uploadMediaIDs)).build();
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
	

	/**@POST
	@Path("/register")
	@Consumes(MediaType.MULTIPART_FORM_DATA + ";charset=utf-8")
	public Response registerOccurrence(@FormDataParam("data") FormDataBodyPart rawData, @FormDataParam("file") FormDataBodyPart file) {
		rawData.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		OccurrenceData data = rawData.getValueAs(OccurrenceData.class);
		LOG.fine("Attempt to register ocurrence: " + data.title + " by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to register occurrence, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		String fileContentType = file.getMediaType().toString();
		if(fileContentType != "image/png" && fileContentType != "image/jpeg" && fileContentType != "image/svg+xml" && fileContentType != "video/x-flv" && fileContentType != "video/mp4" && fileContentType != "video/3gpp" && fileContentType != "video/quicktime" && fileContentType != "video/x-msvideo" && fileContentType != "video/x-ms-wmv" && fileContentType != "video/webm") {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		Transaction txn = datastore.beginTransaction();
		try {
			Key userKey = KeyFactory.createKey("User", data.token.username);
			
			// Save occurrence
			Entity occurrenceEntity = new Entity("UserOccurrence", userKey);
			occurrenceEntity.setProperty("user_occurrence_title", data.title);
			occurrenceEntity.setProperty("user_occurrence_data", new Date());
			occurrenceEntity.setProperty("user_occurrence_type", data.type);
			occurrenceEntity.setProperty("user_occurrence_level", data.level);
			occurrenceEntity.setProperty("user_occurrence_visibility", data.visibility);
			// TODO occurrenceEntity.setProperty("user_occurrence_media", ????);
			datastore.put(occurrenceEntity);
			
			txn.commit();
			LOG.info("User " + data.token.username + " registered occurrence " + data.title);
			return Response.ok().build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}**/
	
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
		List<Map<String, Object>> occurrences = new LinkedList<Map<String, Object>>();
		Query ctrQuery = new Query("UserOccurrence");
		if(data.username != null) {
			Key userKey = KeyFactory.createKey("User", data.username);
			ctrQuery.setAncestor(userKey);
		}
		if(!data.showPrivate) {
			Filter propertyFilter = new FilterPredicate("user_occurrence_visibility", FilterOperator.EQUAL, true);
			ctrQuery.setFilter(propertyFilter);
		}
		else if(!data.token.username.equals(data.username) && !SecurityManager.userHasAccess("see_private_occurrences", data.token.username)) {
			return Response.status(Status.FORBIDDEN).build();
		}
		Map<String, Object> occurrenceMap;
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(QUERY_LIMIT);
		if(data.cursor != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(data.cursor));
		}
		QueryResultList<Entity> results = datastore.prepare(ctrQuery).asQueryResultList(fetchOptions);
		Query ctrQueryMedia;
		List<Entity> mediaResults;
		List<Long> mediaIDs;
		for(Entity occurrenceEntity: results) {
			occurrenceMap = new HashMap<String, Object>();
			occurrenceMap.putAll(occurrenceEntity.getProperties());
			occurrenceMap.put("username", occurrenceEntity.getParent().getName());
			occurrenceMap.put("occurrenceID", occurrenceEntity.getKey().getId());
			ctrQueryMedia = new Query("UserOccurrenceMedia").setAncestor(occurrenceEntity.getKey());
			mediaResults = datastore.prepare(ctrQueryMedia).asList(FetchOptions.Builder.withDefaults());
			mediaIDs = new LinkedList<Long>();
			for(Entity occurrenceMediaEntity: mediaResults) {
				mediaIDs.add(occurrenceMediaEntity.getKey().getId());
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
		Transaction txn = datastore.beginTransaction();
		try {
			LOG.fine("Attempt to edit ocurrence with id: " + data.id + " by user: " + data.token.username);
			if(!data.valid()) {
				return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
			}
			if(!data.token.isTokenValid()) {
				LOG.warning("Failed to edit occurrence, token for user: " + data.token.username + "is invalid");
				return Response.status(Status.FORBIDDEN).build();
			}
			if(!data.token.username.equals(data.username) && !SecurityManager.userHasAccess("edit_user_occurrence", data.token.username)) {
				LOG.warning("Failed to edit occurrence, user: " + data.token.username + " does not have the rights to do it");
				return Response.status(Status.FORBIDDEN).build();
			}
		
			Key userKey = KeyFactory.createKey("User", data.token.username);
			Key occurrenceKey = KeyFactory.createKey(userKey, "UserOccurrence", data.id);
			
			// Save occurrence
			Entity occurrenceEntity = datastore.get(txn, occurrenceKey);
			if(data.title != null && data.title != "") {
				occurrenceEntity.setProperty("user_occurrence_title", data.title);
			}
			if(data.description != null && data.description != "") {
				occurrenceEntity.setProperty("user_occurrence_description", data.description);
			}
			if(data.level != 0) {
				occurrenceEntity.setProperty("user_occurrence_level", data.level);
			}
			occurrenceEntity.setProperty("user_occurrence_visibility", data.visibility);
			datastore.put(txn, occurrenceEntity);
			
			txn.commit();
			LOG.info("User " + data.token.username + " edited occurrence with id: " + data.id);
			return Response.ok().build();
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
		Transaction txn = datastore.beginTransaction();
		try {
			LOG.fine("Attempt to delete ocurrence with id: " + data.id + " by user: " + data.token.username);
			if(!data.valid()) {
				return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
			}
			if(!data.token.isTokenValid()) {
				LOG.warning("Failed to delete occurrence, token for user: " + data.token.username + "is invalid");
				return Response.status(Status.FORBIDDEN).build();
			}
			if(!data.token.username.equals(data.username) && !SecurityManager.userHasAccess("delete_user_occurrence", data.token.username)) {
				LOG.warning("Failed to delete occurrence, user: " + data.token.username + " does not have the rights to do it");
				return Response.status(Status.FORBIDDEN).build();
			}
		
			Key userKey = KeyFactory.createKey("User", data.token.username);
			Key occurrenceKey = KeyFactory.createKey(userKey, "UserOccurrence", data.id);
			
			// Delete occurrence
			datastore.delete(txn, occurrenceKey);
			
			txn.commit();
			LOG.info("User " + data.token.username + " deleted occurrence with id: " + data.id);
			return Response.ok().build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}

}
