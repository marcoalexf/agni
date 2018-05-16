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

import pt.unl.fct.di.apdc.firstwebapp.resources.constructors.RegisterData;

import org.apache.commons.codec.digest.DigestUtils;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public RegisterResource() { } //Nothing to be done here...
	
	@POST
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
	}
	
	
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
