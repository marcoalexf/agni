package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.HashMap;
import java.util.Map;
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
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.ProfileInformationData;

@Path("/profile")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ProfileInformationResource {

	/**
	 * A logger object.
	 */
	private static final Logger LOG = Logger.getLogger(ProfileInformationResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public ProfileInformationResource() { } //Nothing to be done here...

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogin(ProfileInformationData data) {
		LOG.info("Attempt to get user: " + data.username + "profile information requested by user: " + data.token.username);
		Key userKey = KeyFactory.createKey("User", data.username);
		try {
			Entity user = datastore.get(userKey);
			if(!data.token.isTokenValid()) {
				LOG.warning("Failed to request profile, token for user: " + data.token.username + "is invalid");
				return Response.status(Status.FORBIDDEN).build();
			}
			Map<String, Object> profile = new HashMap<String, Object>();
			profile.putAll(user.getProperties());
			profile.remove("user_pwd");
			LOG.info("User " + data.username + " profile was sent successfully to user " + data.token.username);
			return Response.ok(g.toJson(profile)).build();
		} catch (EntityNotFoundException e) {
			// Username does not exist
			LOG.warning("Failed to request profile, user:" + data.username + " does not exist");
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

}
