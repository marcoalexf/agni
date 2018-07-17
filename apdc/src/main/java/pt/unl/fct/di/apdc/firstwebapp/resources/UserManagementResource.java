package pt.unl.fct.di.apdc.firstwebapp.resources;

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
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.UserApprovalData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.UserListData;
import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.UserModData;
import pt.unl.fct.di.apdc.firstwebapp.util.CursorList;
import pt.unl.fct.di.apdc.firstwebapp.util.SecurityManager;

@Path("/backoffice")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UserManagementResource {

	private static final Logger LOG = Logger.getLogger(UserManagementResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final int QUERY_LIMIT = 100;
	
	public UserManagementResource() { } //Nothing to be done here...
	
	@POST
	@Path("/worker/approve")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response approveWorker(UserApprovalData data) {
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		LOG.fine("Attempt to approve user with id: " + data.userID + " by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to approve user, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(!SecurityManager.userHasAccess("approve_worker", data.token.userID)) {
			LOG.warning("Failed to approve user, user: " + data.token.username + " with id: " + data.token.userID + " does not have the rights to do it");
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.beginTransaction();
		try {
			Key userKey = KeyFactory.createKey("User", data.userID);
			
			// Get user
			Entity userEntity = datastore.get(txn, userKey);
			Boolean waitingApproval = (Boolean)userEntity.getProperty("user_waiting_approval");
			if(waitingApproval != null && !waitingApproval) {
				txn.rollback();
				LOG.warning("Failed to approve user, it isn't waiting for approval");
				return Response.status(Status.FORBIDDEN).build();
			}
			
			Key modKey = KeyFactory.createKey("User", data.token.userID);
			// Get mod
			Entity modEntity = datastore.get(txn, modKey);
			if(!((String)(modEntity.getProperty("user_entity"))).equals(((String)(userEntity.getProperty("user_entity"))))) {
				txn.rollback();
				LOG.warning("Failed to approve user, it isn't from the same entity as the moderator");
				return Response.status(Status.FORBIDDEN).build();
			}
			
			userEntity.setProperty("user_waiting_worker_approval", false);
			userEntity.setProperty("user_role", "WORKER");
			datastore.put(txn, userEntity);
			
			txn.commit();
			LOG.info("User " + data.token.username + " with id " + data.token.userID + " approved the user with id: " + data.userID);
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("User not found.").build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/worker/remove")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeWorker(UserApprovalData data) {
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		LOG.fine("Attempt to remove worker role from user with id: " + data.userID + " by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to remove worker role , token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(!SecurityManager.userHasAccess("remove_worker", data.token.userID)) {
			LOG.warning("Failed to remove worker role, user: " + data.token.username + " with id: " + data.token.userID + " does not have the rights to do it");
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.beginTransaction();
		try {
			Key userKey = KeyFactory.createKey("User", data.userID);
			
			// Get user
			Entity userEntity = datastore.get(txn, userKey);
			if(!((String)(userEntity.getProperty("user_role"))).equals("WORKER")) {
				LOG.warning("Failed to remove moderator role, it doesnt' have the role WORKER");
				return Response.status(Status.FORBIDDEN).build();
			}
			userEntity.setProperty("user_role", "USER");
			datastore.put(txn, userEntity);
			
			txn.commit();
			LOG.info("User " + data.token.username + " with id " + data.token.userID + " made the user with id " + data.userID + " a moderator");
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("User not found.").build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/moderator/give")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response giveMod(UserModData data) {
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		LOG.fine("Attempt to make user with id: " + data.userID + " moderator by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to make user moderator, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(!SecurityManager.userHasAccess("make_moderator", data.token.userID)) {
			LOG.warning("Failed to make user moderator, user: " + data.token.username + " with id: " + data.token.userID + " does not have the rights to do it");
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.beginTransaction();
		try {
			Key userKey = KeyFactory.createKey("User", data.userID);
			
			// Get user
			Entity userEntity = datastore.get(txn, userKey);
			if(!((String)(userEntity.getProperty("user_role"))).equals("USER")) {
				LOG.warning("Failed to make user moderator, it doesnt' have the role USER");
				return Response.status(Status.FORBIDDEN).build();
			}
			userEntity.setProperty("user_role", "MOD");
			userEntity.setProperty("user_entity", data.entity);
			datastore.put(txn, userEntity);
			
			txn.commit();
			LOG.info("User " + data.token.username + " with id " + data.token.userID + " made the user with id " + data.userID + " a moderator");
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("User not found.").build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/moderator/remove")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeMod(UserApprovalData data) {
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		LOG.fine("Attempt to remove moderator role from user with id: " + data.userID + " by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to remove moderator role , token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(!SecurityManager.userHasAccess("remove_moderator", data.token.userID)) {
			LOG.warning("Failed to remove moderator role, user: " + data.token.username + " with id: " + data.token.userID + " does not have the rights to do it");
			return Response.status(Status.FORBIDDEN).build();
		}
		Transaction txn = datastore.beginTransaction();
		try {
			Key userKey = KeyFactory.createKey("User", data.userID);
			
			// Get user
			Entity userEntity = datastore.get(txn, userKey);
			if(!((String)(userEntity.getProperty("user_role"))).equals("MOD")) {
				LOG.warning("Failed to remove moderator role, it doesnt' have the role MOD");
				return Response.status(Status.FORBIDDEN).build();
			}
			userEntity.setProperty("user_role", "USER");
			datastore.put(txn, userEntity);
			
			txn.commit();
			LOG.info("User " + data.token.username + " with id " + data.token.userID + " made the user with id " + data.userID + " a moderator");
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			return Response.status(Status.BAD_REQUEST).entity("User not found.").build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response userList(UserListData data) {
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		LOG.fine("Attempt to users list by user: " + data.token.username);
		if(!data.token.isTokenValid()) {
			LOG.warning("Failed to get users list, token for user: " + data.token.username + "is invalid");
			return Response.status(Status.FORBIDDEN).build();
		}
		if(!SecurityManager.userHasAccess("see_users_list", data.token.userID)) {
			LOG.warning("Failed to get users list, user: " + data.token.username + " with id: " + data.token.userID + " does not have the rights to do it");
			return Response.status(Status.FORBIDDEN).build();
		}
		
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(QUERY_LIMIT);
		if(data.cursor != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(data.cursor));
		}
		
		Filter filter = null;
		List<Filter>subFilters = new LinkedList<Filter>();
		
		if(data.entity != null && !data.entity.isEmpty()) {
			filter = new FilterPredicate("user_entity", FilterOperator.EQUAL, data.entity);
			subFilters.add(filter);
		}
		if(data.role != null && !data.role.isEmpty()) {
			filter = new FilterPredicate("user_role", FilterOperator.EQUAL, data.role);
			subFilters.add(filter);
		}
		if(data.waitingModApproval != null) {
			filter = new FilterPredicate("user_waiting_worker_approval", FilterOperator.EQUAL, data.waitingModApproval);
			subFilters.add(filter);
		}
		
		if(subFilters.size() >= 2) {
			filter = CompositeFilterOperator.and(subFilters);
		}
		
		Query ctrQuery = new Query("User");//.addSort("user_creation_time", SortDirection.DESCENDING);
		if(filter != null) {
			ctrQuery.setFilter(filter);
		}
		ctrQuery.addProjection(new PropertyProjection("user_username", String.class));
		ctrQuery.addProjection(new PropertyProjection("user_name", String.class));
		QueryResultList<Entity> results = datastore.prepare(ctrQuery).asQueryResultList(fetchOptions);
		
		List<Map<String, Object>> users = new LinkedList<Map<String, Object>>();
		Map<String, Object> userMap;
		long userID;
		for(Entity userEntity: results) {
			userID = userEntity.getKey().getId();
			userMap = new HashMap<String, Object>();
			userMap.putAll(userEntity.getProperties());
			userMap.put("userID", userID);
			//userMap.put("user_username", (String)(userEntity.getProperty("user_username")));
			//userMap.put("user_name", (String)(userEntity.getProperty("user_name")));
			users.add(userMap);
		}
		LOG.info(String.valueOf(results.size()));
		CursorList cursorList = new CursorList(results.getCursor().toWebSafeString(), users);
		LOG.info("List of users sent");
		return Response.ok(g.toJson(cursorList)).build();
	}

}
