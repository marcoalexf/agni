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
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceAcceptData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceAcceptListData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceAcceptVerifyData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceResolveData;
import pt.unl.fct.di.apdc.firstwebapp.util.CursorList;
import pt.unl.fct.di.apdc.firstwebapp.util.ListIds;
import pt.unl.fct.di.apdc.firstwebapp.util.SecurityManager;

@Path("/backoffice")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class OccurrenceManagementResource {

	private static final Logger LOG = Logger.getLogger(OccurrenceManagementResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final int QUERY_LIMIT = 100;
	
	public OccurrenceManagementResource() { } //Nothing to be done here...
	
	@POST
	@Path("/accept")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response acceptOccurrence(OccurrenceAcceptData data) {
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		LOG.fine("Attempt to accept occurrence with id: " + data.occurrenceID + " by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to accept occurrence, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(!SecurityManager.userHasAccess("accept_user_occurrence", data.token.userID)) {
			LOG.warning("Failed to accept occurrence, user: " + data.token.username + " with id: " + data.token.userID + " does not have the rights to do it");
			return Response.status(Status.FORBIDDEN).build();
		}
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {
			Key userOccurrenceKey = KeyFactory.createKey("User", data.userID);
			Key occurrenceKey = KeyFactory.createKey(userOccurrenceKey, "UserOccurrence", data.occurrenceID);
			
			// Get occurrence
			Entity occurrenceEntity = datastore.get(txn, occurrenceKey);
			if(!((String)occurrenceEntity.getProperty("user_occurrence_status")).equals("OPEN")) {
				LOG.warning("Failed to accept occurrence, it is being resolved already");
				return Response.status(Status.FORBIDDEN).build();
			}
			occurrenceEntity.setProperty("user_occurrence_status", "ACCEPTED");
			datastore.put(txn, occurrenceEntity);
			
			Entity acceptedEntity = new Entity("AcceptedOccurrence", KeyFactory.keyToString(occurrenceKey));
			acceptedEntity.setProperty("accepted_occurrence_userID", data.token.userID);
			acceptedEntity.setProperty("accepted_occurrence_date", new Date());
			
			datastore.put(txn, acceptedEntity);
			txn.commit();
			LOG.info("User " + data.token.username + " with id " + data.token.userID + " accepted the occurrence with id: " + data.occurrenceID);
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("Occurrence not found.").build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/accept/verify")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response acceptVerifyOccurrence(OccurrenceAcceptVerifyData data) {
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		LOG.fine("Attempt to verify if occurrence with id: " + data.occurrenceID + " was accepted by user with id: " + data.userID);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to verify if occurrence was accepted, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(data.userID != data.token.userID && !SecurityManager.userHasAccess("verify_accepted_occurrence", data.token.userID)) {
			LOG.warning("Failed to verify if occurrence was accepted, user: " + data.token.username + " with id: " + data.token.userID + " does not have the rights to do it");
			return Response.status(Status.FORBIDDEN).build();
		}
		
		try {
			Key userOccurrenceKey = KeyFactory.createKey("User", data.occurrenceUserID);
			Key occurrenceKey = KeyFactory.createKey(userOccurrenceKey, "UserOccurrence", data.occurrenceID);
			Key acceptKey = KeyFactory.createKey("AcceptedOccurrence", KeyFactory.keyToString(occurrenceKey));
			
			// Get occurrence
			Entity acceptEntity = datastore.get(acceptKey);
			
			LOG.info("Occurrence verified if was accepted by user with id " + data.userID);
			return Response.ok(g.toJson(((long)(acceptEntity.getProperty("accepted_occurrence_userID"))) == data.userID)).build();
		} catch (EntityNotFoundException e) {
			return Response.ok(g.toJson(false)).build();
		}
	}
	
	@POST
	@Path("/resolve")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resolveOccurrence(OccurrenceResolveData data) {
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		LOG.fine("Attempt to resolve occurrence with id: " + data.occurrenceID + " by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to resolve occurrence, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(!SecurityManager.userHasAccess("resolve_user_occurrence", data.token.userID)) {
			LOG.warning("Failed to resolve occurrence, user: " + data.token.username + " with id: " + data.token.userID + " does not have the rights to do it");
			return Response.status(Status.FORBIDDEN).build();
		}
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {
			Key userOccurrenceKey = KeyFactory.createKey("User", data.userID);
			Key occurrenceKey = KeyFactory.createKey(userOccurrenceKey, "UserOccurrence", data.occurrenceID);
			Key acceptedKey = KeyFactory.createKey("AcceptedOccurrence", KeyFactory.keyToString(occurrenceKey));
			
			// Check accepted occurrence existence
			Entity acceptedEntity = datastore.get(txn, acceptedKey);
			
			// Check occurrence was accepted by the user
			if(!((long)(acceptedEntity.getProperty("accepted_occurrence_userID")) == data.token.userID)) {
				LOG.warning("Failed to resolve occurrence, user: " + data.token.username + " didn't accept this occurrence.");
				return Response.status(Status.FORBIDDEN).build();
			}
			
			datastore.delete(txn, acceptedKey);
			
			// Get occurrence
			Entity occurrenceEntity = datastore.get(txn, occurrenceKey);
			occurrenceEntity.setProperty("user_occurrence_status", "RESOLVED");
			datastore.put(txn, occurrenceEntity);
			
			Entity resolvedEntity = new Entity("ResolvedOccurrence", KeyFactory.keyToString(occurrenceKey));
			resolvedEntity.setIndexedProperty("resolved_occurrence_userID", data.token.userID);
			resolvedEntity.setIndexedProperty("resolved_occurrence_date", new Date());
			datastore.put(txn, resolvedEntity);
			
			// Create occurrence media entities
			if(data.uploadMedia) {
				List<Entity> mediaEntities = new LinkedList<Entity>();
				List<Entity> uploadEntities = new LinkedList<Entity>();
				List<Long> uploadMediaIDs = new LinkedList<Long>();
				Entity occurrenceMediaEntity;
				long occurrenceID = occurrenceEntity.getKey().getId();
				for(int i = 0; i < data.nUploads; i++) {
					occurrenceMediaEntity = new Entity("UserResolvedOccurrenceMedia", occurrenceKey);
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
							(boolean)occurrenceEntity.getProperty("user_occurrence_visibility"),
							false
							);
					uploadEntities.add(fileUpload);
				}
				datastore.put(txn, uploadEntities);
				txn.commit();
				for(Entity uploadEntity: uploadEntities) {
					uploadMediaIDs.add(uploadEntity.getKey().getId());
				}
				LOG.info("User " + data.token.username + " resolved occurrence with id: " + data.occurrenceID);
				return Response.ok(g.toJson(new ListIds(uploadMediaIDs))).build();
			}
			else {
				txn.commit();
				LOG.info("User " + data.token.username + " resolved occurrence with id: " + data.occurrenceID);
				return Response.ok().build();
			}
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("Accepted occurrence not found.").build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/conclude")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response concludeOccurrence(OccurrenceAcceptData data) {
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		LOG.fine("Attempt to conclude occurrence with id: " + data.occurrenceID + " by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to conclude occurrence, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(!SecurityManager.userHasAccess("conclude_user_occurrence", data.token.userID)) {
			LOG.warning("Failed to conclude occurrence, user: " + data.token.username + " with id: " + data.token.userID + " does not have the rights to do it");
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.beginTransaction();
		try {
			Key userOccurrenceKey = KeyFactory.createKey("User", data.userID);
			Key occurrenceKey = KeyFactory.createKey(userOccurrenceKey, "UserOccurrence", data.occurrenceID);
			Key resolvedKey = KeyFactory.createKey("ResolvedOccurrence", KeyFactory.keyToString(occurrenceKey));
			
			// Check resolved occurrence existence
			Entity resolveEntity = datastore.get(txn, resolvedKey);
			long resolverUserID = (long)resolveEntity.getProperty("resolved_occurrence_userID");
			
			datastore.delete(txn, resolvedKey);
			
			// Get occurrence
			Entity occurrenceEntity = datastore.get(txn, occurrenceKey);
			occurrenceEntity.setProperty("user_occurrence_status", "CONCLUDED");
			datastore.put(txn, occurrenceEntity);
			
			Entity concludedEntity = new Entity("ConcludedOccurrence", KeyFactory.keyToString(occurrenceKey));
			concludedEntity.setIndexedProperty("concluded_occurrence_resolver_userID", resolverUserID);
			concludedEntity.setIndexedProperty("concluded_occurrence_conclude_userID", data.token.userID);
			concludedEntity.setIndexedProperty("concluded_occurrence_date", new Date());
			datastore.put(txn, concludedEntity);
			
			txn.commit();
			LOG.info("User " + data.token.username + " concluded occurrence with id: " + data.occurrenceID);
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("Resolved occurrence not found.").build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/reject")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response rejectOccurrence(OccurrenceAcceptData data) {
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		LOG.fine("Attempt to reject occurrence with id: " + data.occurrenceID + " by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to reject occurrence, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(!SecurityManager.userHasAccess("reject_user_occurrence", data.token.userID)) {
			LOG.warning("Failed to reject occurrence, user: " + data.token.username + " with id: " + data.token.userID + " does not have the rights to do it");
			return Response.status(Status.FORBIDDEN).build();
		}
		
		Key userOccurrenceKey = KeyFactory.createKey("User", data.userID);
		Key occurrenceKey = KeyFactory.createKey(userOccurrenceKey, "UserOccurrence", data.occurrenceID);
		
		Transaction txn = datastore.beginTransaction();	
		try {	
			Key resolvedKey = KeyFactory.createKey("ResolvedOccurrence", KeyFactory.keyToString(occurrenceKey));
			
			// Check resolved occurrence existence
			datastore.get(txn, resolvedKey);
			
			datastore.delete(txn, resolvedKey);
			
			// Get occurrence
			Entity occurrenceEntity = datastore.get(txn, occurrenceKey);
			occurrenceEntity.setProperty("user_occurrence_status", "OPEN");
			datastore.put(txn, occurrenceEntity);
			
			txn.commit();
			LOG.info("User " + data.token.username + " rejected occurrence with id: " + data.occurrenceID);
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			Key acceptedKey = KeyFactory.createKey("AcceptedOccurrence", KeyFactory.keyToString(occurrenceKey));
			
			try {
				// Check resolved occurrence existence
				datastore.get(txn, acceptedKey);
			
				datastore.delete(txn, acceptedKey);
				
				// Get occurrence
				Entity occurrenceEntity = datastore.get(txn, occurrenceKey);
				occurrenceEntity.setProperty("user_occurrence_status", "OPEN");
				datastore.put(txn, occurrenceEntity);
				
				txn.commit();
				LOG.info("User " + data.token.username + " rejected occurrence with id: " + data.occurrenceID);
				return Response.ok().build();
			} catch (EntityNotFoundException e1) {
				return Response.status(Status.BAD_REQUEST).entity("Resolved or accepted occurrence not found.").build();
			}
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/accept/list")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response acceptedListOccurrences(OccurrenceAcceptListData data) {
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		LOG.fine("Attempt to get accepted occurrences list for user with id " + data.userID + " by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to get accepted occurrences list, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(data.userID != data.token.userID && !SecurityManager.userHasAccess("see_accepted_occurrences", data.token.userID)) {
			LOG.warning("Failed to get accepted occurrences list, user: " + data.token.username + " with id: " + data.token.userID + " does not have the rights to do it");
			return Response.status(Status.FORBIDDEN).build();
		}
		
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(QUERY_LIMIT);
		if(data.cursor != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(data.cursor));
		}
		FilterPredicate filter = new FilterPredicate("accepted_occurrence_userID", FilterOperator.EQUAL, data.userID);
		Query ctrQuery = new Query("AcceptedOccurrence").setFilter(filter).addSort("accepted_occurrence_date", SortDirection.DESCENDING);
		QueryResultList<Entity> results = datastore.prepare(ctrQuery).asQueryResultList(fetchOptions);
		
		List<Map<String, Object>> occurrences = new LinkedList<Map<String, Object>>();
		Query ctrQueryMedia;
		Map<String, Object> occurrenceMap;
		List<Entity> mediaResults;
		List<String> mediaIDs;
		Key occurrenceKey;
		Key userOccurrenceKey;
		for(Entity acceptEntity: results) {
			try {
				Entity occurrenceEntity = datastore.get(KeyFactory.stringToKey(acceptEntity.getKey().getName()));
			
				occurrenceKey = occurrenceEntity.getKey();
				userOccurrenceKey = occurrenceEntity.getParent();
				
				occurrenceMap = new HashMap<String, Object>();
				occurrenceMap.putAll(occurrenceEntity.getProperties());
				occurrenceMap.put("username", userOccurrenceKey.getName());
				occurrenceMap.put("occurrenceID", String.valueOf(occurrenceKey.getId()));
				occurrenceMap.put("userID", String.valueOf(userOccurrenceKey.getId()));
				occurrenceMap.put("accepted_occurrence_date", (Date)(acceptEntity.getProperty("accepted_occurrence_date")));
				ctrQueryMedia = new Query("UserOccurrenceMedia").setAncestor(occurrenceKey);
				mediaResults = datastore.prepare(ctrQueryMedia).asList(FetchOptions.Builder.withDefaults());
				mediaIDs = new LinkedList<String>();
				for(Entity occurrenceMediaEntity: mediaResults) {
					mediaIDs.add(String.valueOf(occurrenceMediaEntity.getKey().getId()));
				}
				occurrenceMap.put("mediaIDs", mediaIDs);
				ctrQueryMedia = new Query("UserResolvedOccurrenceMedia").setAncestor(occurrenceEntity.getKey());
				mediaResults = datastore.prepare(ctrQueryMedia).asList(FetchOptions.Builder.withDefaults());
				mediaIDs = new LinkedList<String>();
				for(Entity occurrenceMediaEntity: mediaResults) {
					mediaIDs.add(String.valueOf(occurrenceMediaEntity.getKey().getId()));
				}
				occurrenceMap.put("resolvedMediaIDs", mediaIDs);
				occurrences.add(occurrenceMap);
			} catch (EntityNotFoundException e) {
				// shouldn't activate i guess
			}
		}
		CursorList cursorList = new CursorList(results.getCursor().toWebSafeString(), occurrences);
		LOG.info("List of accepted occurrences sent");
		return Response.ok(g.toJson(cursorList)).build();
	}
	
	@POST
	@Path("/resolve/list")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resolvedListOccurrences(OccurrenceAcceptListData data) {
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		LOG.fine("Attempt to get resolved occurrences list for user with id " + data.userID + " by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to get resolved occurrences list, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(data.userID != data.token.userID && !SecurityManager.userHasAccess("see_accepted_occurrences", data.token.userID)) {
			LOG.warning("Failed to get resolved occurrences list, user: " + data.token.username + " with id: " + data.token.userID + " does not have the rights to do it");
			return Response.status(Status.FORBIDDEN).build();
		}
		
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(QUERY_LIMIT);
		if(data.cursor != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(data.cursor));
		}
		FilterPredicate filter = new FilterPredicate("resolved_occurrence_userID", FilterOperator.EQUAL, data.userID);
		Query ctrQuery = new Query("ResolvedOccurrence").setFilter(filter).addSort("resolved_occurrence_date", SortDirection.DESCENDING).setKeysOnly();
		QueryResultList<Entity> results = datastore.prepare(ctrQuery).asQueryResultList(fetchOptions);
		
		List<Map<String, Object>> occurrences = new LinkedList<Map<String, Object>>();
		Query ctrQueryMedia;
		Map<String, Object> occurrenceMap;
		List<Entity> mediaResults;
		List<String> mediaIDs;
		Key occurrenceKey;
		Key userOccurrenceKey;
		for(Entity resolveEntity: results) {
			try {
				Entity occurrenceEntity = datastore.get(KeyFactory.stringToKey(resolveEntity.getKey().getName()));
			
				occurrenceKey = occurrenceEntity.getKey();
				userOccurrenceKey = occurrenceEntity.getParent();
				
				occurrenceMap = new HashMap<String, Object>();
				occurrenceMap.putAll(occurrenceEntity.getProperties());
				occurrenceMap.put("username", userOccurrenceKey.getName());
				occurrenceMap.put("occurrenceID", String.valueOf(occurrenceKey.getId()));
				occurrenceMap.put("userID", String.valueOf(userOccurrenceKey.getId()));
				occurrenceMap.put("resolved_occurrence_date", (Date)(resolveEntity.getProperty("resolved_occurrence_date")));
				ctrQueryMedia = new Query("UserOccurrenceMedia").setAncestor(occurrenceKey);
				mediaResults = datastore.prepare(ctrQueryMedia).asList(FetchOptions.Builder.withDefaults());
				mediaIDs = new LinkedList<String>();
				for(Entity occurrenceMediaEntity: mediaResults) {
					mediaIDs.add(String.valueOf(occurrenceMediaEntity.getKey().getId()));
				}
				occurrenceMap.put("mediaIDs", mediaIDs);
				ctrQueryMedia = new Query("UserResolvedOccurrenceMedia").setAncestor(occurrenceEntity.getKey());
				mediaResults = datastore.prepare(ctrQueryMedia).asList(FetchOptions.Builder.withDefaults());
				mediaIDs = new LinkedList<String>();
				for(Entity occurrenceMediaEntity: mediaResults) {
					mediaIDs.add(String.valueOf(occurrenceMediaEntity.getKey().getId()));
				}
				occurrenceMap.put("resolvedMediaIDs", mediaIDs);
				occurrences.add(occurrenceMap);
			} catch (EntityNotFoundException e) {
				// shouldn't activate i guess
			}
		}
		CursorList cursorList = new CursorList(results.getCursor().toWebSafeString(), occurrences);
		LOG.info("List of accepted occurrences sent");
		return Response.ok(g.toJson(cursorList)).build();
	}

}
