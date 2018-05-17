package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Arrays;
//import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
//import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
//import com.google.appengine.api.datastore.Query.Filter;
//import com.google.appengine.api.datastore.Query.FilterOperator;
//import com.google.appengine.api.datastore.Query.FilterPredicate;
//import com.google.appengine.api.datastore.Query.SortDirection;
import org.apache.commons.codec.digest.DigestUtils;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.LoginData;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

	/**
	 * A logger object.
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public LoginResource() { } //Nothing to be done here...
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogin(LoginData data, 
			                  @Context HttpServletRequest request,
			                  @Context HttpHeaders headers) {
		LOG.info("Attempt to login user: " + data.username);

		Transaction txn = datastore.beginTransaction();
		Key userKey = KeyFactory.createKey("User", data.username);
		try {
			Entity user = datastore.get(userKey);
			
			// Obtain the user login statistics
			Query ctrQuery = new Query("UserStats").setAncestor(userKey);
			List<Entity> results = datastore.prepare(ctrQuery).asList(FetchOptions.Builder.withDefaults());
			Entity ustats = null;
			if (results.isEmpty()) {
				ustats = new Entity("UserStats", user.getKey() );
				ustats.setProperty("user_stats_logins", 0L);
				ustats.setProperty("user_stats_failed", 0L);
			} else {
				ustats = results.get(0);
			}

			String hashedPWD = (String) user.getProperty("user_pwd");
			if (hashedPWD.equals(DigestUtils.shaHex(data.password))) {
				// Password correct
				
				// Construct the logs
				Entity log = new Entity("UserLog", user.getKey());
				log.setProperty("user_login_ip", request.getRemoteAddr());
				log.setProperty("user_login_host", request.getRemoteHost());
				log.setProperty("user_login_latlon", headers.getHeaderString("X-AppEngine-CityLatLong"));
				log.setProperty("user_login_city", headers.getHeaderString("X-AppEngine-City"));
				log.setProperty("user_login_country", headers.getHeaderString("X-AppEngine-Country"));
				log.setProperty("user_login_time", new Date());
				// Get the user statistics and updates it
				ustats.setProperty("user_stats_logins", 1L + (long) ustats.getProperty("user_stats_logins"));
				ustats.setProperty("user_stats_failed", 0L );
				ustats.setProperty("user_stats_last", new Date());
				
				// Save token
				AuthToken token = new AuthToken(data.username);
				Entity tokenEntity = new Entity("UserToken", user.getKey());
				tokenEntity.setProperty("user_token_id", token.tokenID);
				tokenEntity.setProperty("user_token_creation_data", token.creationData);
				tokenEntity.setProperty("user_token_expiration_data", token.expirationData);
				
				// Batch operation
				List<Entity> logs = Arrays.asList(log,ustats,tokenEntity);
				datastore.put(txn,logs);
				txn.commit();
				
				// Return token
				LOG.info("User '" + data.username + "' logged in sucessfully.");
				return Response.ok(g.toJson(token)).build();				
			} else {
				// Incorrect password
				ustats.setProperty("user_stats_failed", 1L + (long) ustats.getProperty("user_stats_failed"));
				datastore.put(txn,ustats);				
				txn.commit();

				LOG.warning("Wrong password for username: " + data.username);
				return Response.status(Status.FORBIDDEN).build();				
			}
		} catch (EntityNotFoundException e) {
			// Username does not exist
			if (txn.isActive()) {
				txn.rollback();
			}
			LOG.warning("Failed login attempt for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				return Response.status(Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		
	}
	
	/**@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response checkUsernameAvailable(LoginData data ) {
		
		Key userKey = KeyFactory.createKey("User", data.username);
		try {
			Entity user = datastore.get(userKey);
			String hashedPWD = (String) user.getProperty("user_pwd");
			if (hashedPWD.equals(DigestUtils.shaHex(data.password))) {

				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -1);
				Date yesterday = cal.getTime();
				
				// Obtain the user login statistics
				Filter propertyFilter =
					    new FilterPredicate("user_login_time", FilterOperator.GREATER_THAN_OR_EQUAL, yesterday);
				Query ctrQuery = new Query("UserLog").setAncestor(KeyFactory.createKey("User", data.username))
						             .setFilter(propertyFilter)
						             .addSort("user_login_time", SortDirection.DESCENDING);
				ctrQuery.addProjection(new PropertyProjection("user_login_time", Date.class));
				ctrQuery.addProjection(new PropertyProjection("user_login_ip", String.class));
				List<Entity> results = datastore.prepare(ctrQuery).asList(FetchOptions.Builder.withLimit(3));
				
/*				List<Date> loginDates = new ArrayList();
//				for(Entity userlog:results) {
//						loginDates.add((Date) userlog.getProperty("user_login_time"));
//				}
//				return Response.ok(g.toJson(results)).build();
			
			} else {
				LOG.warning("Wrong password for username: " + data.username );
				return Response.status(Status.FORBIDDEN).build();				
			}
		} catch (EntityNotFoundException e) {
			// Username does not exist
			LOG.warning("Failed login attempt for username: " + data.username);
			return Response.status(Status.FORBIDDEN).build();
		}
	}**/


}
