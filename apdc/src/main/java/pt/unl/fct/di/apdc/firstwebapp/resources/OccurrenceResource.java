package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Date;
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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.ListOccurrenceData;
import pt.unl.fct.di.apdc.firstwebapp.util.OccurrenceData;
import pt.unl.fct.di.apdc.firstwebapp.util.SecurityManager;

@Path("/occurrence")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class OccurrenceResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();
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
			datastore.put(txn, occurrenceEntity);
			
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
		List<Entity> results = datastore.prepare(ctrQuery).asList(FetchOptions.Builder.withDefaults());
		for(Entity occurrenceEntity: results) {
			occurrences.add(occurrenceEntity.getProperties());
		}
		LOG.info("List of occurrences sent");
		return Response.ok(g.toJson(occurrences)).build();
	}
	
	@GET
	@Path("/list")
	public Response listOccurrences() {
		return listOccurrences(new ListOccurrenceData());
	}

}
