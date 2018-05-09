package pt.unl.fct.di.apdc.firstwebapp.resources;

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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;

@Path("/logout")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LogoutResource {

	/**
	 * A logger object.
	 */
	private static final Logger LOG = Logger.getLogger(LogoutResource.class.getName());
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public LogoutResource() { } //Nothing to be done here...
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogout(AuthToken data) {
		LOG.info("Attempt to logout user: " + data.username);
		Key userKey = KeyFactory.createKey("User", data.username);
		Transaction txn = datastore.beginTransaction();
		try {
			@SuppressWarnings("unused")
			Entity user = datastore.get(userKey);
			if(!data.isTokenValid()) {
				LOG.warning("Failed to logout user: " + data.username + ", token is invalid");
				return Response.status(Status.FORBIDDEN).build();
			}
			Filter propertyFilter = new FilterPredicate("user_token_id", FilterOperator.EQUAL, data.tokenID);
			Query ctrQuery = new Query("UserToken").setAncestor(userKey).setFilter(propertyFilter);
			List<Entity> results = datastore.prepare(ctrQuery).asList(FetchOptions.Builder.withDefaults());
			for(Entity tokenEntity: results) {
				if((long)tokenEntity.getProperty("user_token_creation_data")  == data.creationData && (long)tokenEntity.getProperty("user_token_expiration_data") == data.expirationData) {
					datastore.delete(txn, tokenEntity.getKey());
				}
			}
			txn.commit();
			LOG.info("User " + data.username + " was logged out with success");
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			// Username does not exist
			LOG.warning("Failed to logout, user:" + data.username + " does not exist");
			return Response.status(Status.BAD_REQUEST).build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}

}
