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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

import pt.unl.fct.di.apdc.firstwebapp.util.ListOccurrenceData;
import pt.unl.fct.di.apdc.firstwebapp.util.OccurrenceData;

@Path("/occurrence")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class OccurrenceResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public OccurrenceResource() { } //Nothing to be done here...
	
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerOccurrence(OccurrenceData data) {
		LOG.fine("Attempt to register ocurrence: " + data.title + " by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to register occurrence, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
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
			occurrenceEntity.setProperty("user_occurrence_lat", data.lat);
			occurrenceEntity.setProperty("user_occurrence_lon", data.lon);
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
		LOG.fine("Attempt to register ocurrence: " + data.title + " by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to register occurrence, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
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
			occurrenceEntity.setProperty("user_occurrence_lat", data.lat);
			occurrenceEntity.setProperty("user_occurrence_lon", data.lon);
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
	}

}
