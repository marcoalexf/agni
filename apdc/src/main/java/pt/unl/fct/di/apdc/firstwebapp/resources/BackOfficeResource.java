package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Date;
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
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceAcceptData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.OccurrenceResolveData;
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
			occurrenceEntity.setProperty("user_occurrence_status", "ACCEPTED");
			datastore.put(txn, occurrenceEntity);
			
			Key userKey = KeyFactory.createKey("User", data.token.userID);
			Entity userEntity = datastore.get(txn, userKey);
			Entity acceptedEntity = new Entity("AcceptedOccurrence", KeyFactory.keyToString(occurrenceKey), userKey);
			acceptedEntity.setProperty("accepted_occurrence_date", new Date());
			acceptedEntity.setProperty("accepted_occurrence_responsible_organization", (String)userEntity.getProperty("user_organization"));
			
			datastore.put(txn, acceptedEntity);
			txn.commit();
			LOG.info("User " + data.token.username + " with id " + data.token.userID + " accepted the occurrence with id: " + data.occurrenceID);
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
		Transaction txn = datastore.beginTransaction();
		try {
			Key userOccurrenceKey = KeyFactory.createKey("User", data.userID);
			Key occurrenceKey = KeyFactory.createKey(userOccurrenceKey, "UserOccurrence", data.occurrenceID);
			Key userKey = KeyFactory.createKey("User", data.token.userID);
			Key acceptedKey = KeyFactory.createKey(userKey, "AccpetedOccurrence", KeyFactory.keyToString(occurrenceKey));
			
			// Get accepted occurrence
			Entity acceptedEntity = datastore.get(txn, acceptedKey);
			
			txn.commit();
			LOG.info("User " + data.token.username + " with id " + data.token.userID + " accepted the occurrence with id: " + data.occurrenceID);
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("Accepted occurrence not found.").build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}

}
