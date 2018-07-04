package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceAcceptData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceResolveData;
import pt.unl.fct.di.apdc.firstwebapp.util.ListIds;
import pt.unl.fct.di.apdc.firstwebapp.util.SecurityManager;

@Path("/backoffice")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class BackOfficeResource {

	private static final Logger LOG = Logger.getLogger(BackOfficeResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public BackOfficeResource() { } //Nothing to be done here...
	
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
		Transaction txn = datastore.beginTransaction();
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
			
			Key userKey = KeyFactory.createKey("User", data.token.userID);
			Entity acceptedEntity = new Entity("AcceptedOccurrence", KeyFactory.keyToString(occurrenceKey), userKey);
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
		Transaction txn = datastore.beginTransaction();
		try {
			Key userOccurrenceKey = KeyFactory.createKey("User", data.userID);
			Key occurrenceKey = KeyFactory.createKey(userOccurrenceKey, "UserOccurrence", data.occurrenceID);
			Key userKey = KeyFactory.createKey("User", data.token.userID);
			Key acceptedKey = KeyFactory.createKey(userKey, "AcceptedOccurrence", KeyFactory.keyToString(occurrenceKey));
			
			// Check accepted occurrence existence
			datastore.get(txn, acceptedKey);
			
			datastore.delete(txn, acceptedKey);
			
			// Get occurrence
			Entity occurrenceEntity = datastore.get(txn, occurrenceKey);
			occurrenceEntity.setProperty("user_occurrence_status", "RESOLVED");
			datastore.put(txn, occurrenceEntity);
			
			Entity resolvedEntity = new Entity("ResolvedOccurrence", KeyFactory.keyToString(occurrenceKey), userKey);
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

}
