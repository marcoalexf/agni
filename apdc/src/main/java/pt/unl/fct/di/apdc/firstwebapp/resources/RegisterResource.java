package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Date;
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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.RegisterData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	private final Gson g = new Gson();
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public RegisterResource() { } //Nothing to be done here...
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(RegisterData data) {
		LOG.fine("Attempt to register user: " + data.username);
		
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		
		if(!data.password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$")) {
			return Response.status(Status.BAD_REQUEST).entity("New password should have atleast a number, a lower case char, a upper case char, a special char and no spaces.").build();
		}
		
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {
			FilterPredicate filter = new FilterPredicate("user_username", FilterOperator.EQUAL, data.username);
			Query ctrQuery = new Query("User").setFilter(filter).setKeysOnly();
			List<Entity> results = datastore.prepare(ctrQuery).asList(FetchOptions.Builder.withDefaults());
			if(!results.isEmpty()) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
			}
			filter = new FilterPredicate("user_email", FilterOperator.EQUAL, data.email);
			ctrQuery.setFilter(filter).setKeysOnly();
			results = datastore.prepare(ctrQuery).asList(FetchOptions.Builder.withDefaults());
			if(!results.isEmpty()) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("Email already exists.").build();
			}
			
			Entity user = new Entity("User");
			user.setProperty("user_username", data.username);
			user.setProperty("user_name", data.name);
			user.setProperty("user_pwd", DigestUtils.sha256Hex(data.password));
			user.setProperty("user_email", data.email);
			user.setUnindexedProperty("user_creation_time", new Date());
			user.setProperty("user_role", "USER");
			user.setProperty("user_district", data.district);
			user.setProperty("user_county", data.county);
			user.setProperty("user_locality", data.locality);
			if(data.role.equals("WORKER")) {
				user.setProperty("user_waiting_approval", true);
			}
			datastore.put(txn, user);
			
			/*if(data.role.equals("WORKER")) {
				Entity worker = new Entity("WorkerApproval", user.getKey());
				datastore.put(worker);
			}*/
			
			if(data.uploadPhoto) {
				txn.commit();
				txn = datastore.beginTransaction(options);
				Entity fileUpload = UploadResource.newUploadFileEntity(
						"user/" + user.getKey().getId() + "/", 
						"photo", 
						"IMAGE",
						true,
						false
						);
				datastore.put(txn, fileUpload);
				txn.commit();
				LOG.info("User registered " + data.username + " with id " + user.getKey().getId());
				return Response.ok(g.toJson(fileUpload.getKey().getId())).build();
			}
			else {
				txn.commit();
				LOG.info("User registered " + data.username + " with id " + user.getKey().getId());
				return Response.ok().build();
			}
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}
	
	/**@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(RegisterData data) {
		LOG.fine("Attempt to register user: " + data.username);
		
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		
		TransactionOptions options = TransactionOptions.Builder.withXG(true);
		Transaction txn = datastore.beginTransaction(options);
		try {
			// If the entity does not exist an Exception is thrown. Otherwise,
			Key userKey = KeyFactory.createKey("User", data.username);
			@SuppressWarnings("unused")
			Entity user = datastore.get(txn, userKey);
			txn.rollback();
			return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
		} catch (EntityNotFoundException e) {
			Entity user = new Entity("User", data.username);
			user.setProperty("user_name", data.name);
			user.setProperty("user_pwd", DigestUtils.shaHex(data.password));
			user.setProperty("user_email", data.email);
			user.setUnindexedProperty("user_creation_time", new Date());
			user.setProperty("user_role", data.role);
			user.setProperty("user_district", data.district);
			user.setProperty("user_county", data.county);
			user.setProperty("user_locality", data.locality);
			datastore.put(txn, user);
			if(data.uploadPhoto) {
				Entity fileUpload = UploadResource.newUploadFileEntity(
						"user/" + data.username + "/", 
						"photo", 
						"IMAGE",
						true,
						false
						);
				datastore.put(txn, fileUpload);
				txn.commit();
				LOG.info("User registered " + data.username);
				return Response.ok(g.toJson(fileUpload.getKey().getId())).build();
			}
			else {
				txn.commit();
				LOG.info("User registered " + data.username);
				return Response.ok().build();
			}
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}**/
	
	/**@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(RegisterData data) {
		LOG.fine("Attempt to register user: " + data.username);
		
		if(!data.valid()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		
		Transaction txn = datastore.beginTransaction();
		try {
			// If the entity does not exist an Exception is thrown. Otherwise,
			Key userKey = KeyFactory.createKey("User", data.username);
			@SuppressWarnings("unused")
			Entity user = datastore.get(txn, userKey);
			txn.rollback();
			return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
		} catch (EntityNotFoundException e) {
			Entity user = new Entity("User", data.username);
			user.setProperty("user_name", data.name);
			user.setProperty("user_pwd", DigestUtils.shaHex(data.password));
			user.setProperty("user_email", data.email);
			user.setUnindexedProperty("user_creation_time", new Date());
			user.setProperty("user_role", data.role);
			user.setProperty("user_district", data.district);
			user.setProperty("user_county", data.county);
			user.setProperty("user_locality", data.locality);
			datastore.put(txn,user);
			LOG.info("User registered " + data.username);
			txn.commit();
			return Response.ok().build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}**/
	
	/**@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA + ";charset=utf-8")
	public Response registerUser(@FormDataParam("data") FormDataBodyPart rawData, @FormDataParam("file") FormDataBodyPart file) {
		rawData.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		RegisterData data = rawData.getValueAs(RegisterData.class);
		LOG.fine("Attempt to register user: " + data.username);
		
		String fileContentType = file.getMediaType().toString();
		if(!data.validRegistration() || (fileContentType != "image/png" && fileContentType != "image/jpeg" && fileContentType != "image/svg+xml")) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		
		Transaction txn = datastore.beginTransaction();
		try {
			// If the entity does not exist an Exception is thrown. Otherwise,
			Key userKey = KeyFactory.createKey("User", data.username);
			@SuppressWarnings("unused")
			Entity user = datastore.get(userKey);
			txn.rollback();
			return Response.status(Status.BAD_REQUEST).entity("User already exists.").build(); 
		} catch (EntityNotFoundException e) {
			Entity user = new Entity("User", data.username);
			user.setProperty("user_name", data.name);
			user.setProperty("user_pwd", DigestUtils.shaHex(data.password));
			user.setProperty("user_email", data.email);
			user.setUnindexedProperty("user_creation_time", new Date());
			user.setProperty("user_role", data.role);
			user.setProperty("user_cellphone", data.cellphone);
			user.setProperty("user_telephone", data.telephone);
			user.setProperty("user_address", data.address);
			user.setProperty("user_address_extra", data.addressExtra);
			user.setProperty("user_address_city", data.addressCity);
			user.setProperty("user_address_postal_code", data.addressPostalCode);
			user.setUnindexedProperty("user_nif", data.nif);
			user.setUnindexedProperty("user_cc", data.cc);
			datastore.put(txn,user);
			if(!GcsManager.gcsPost(fileContentType, file.getValueAs(InputStream.class), "user/" + data.username + "/profilePhoto")) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Profile photo could not be uploaded.").build();
			}
			LOG.info("User registered " + data.username);
			txn.commit();
			return Response.ok().build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}**/


}
