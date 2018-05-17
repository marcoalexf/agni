package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

import pt.unl.fct.di.apdc.firstwebapp.util.GcsManager;

@Path("/")
public class UploadResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	//public UploadResource() { } //Nothing to be done here...
	
	@POST
	@Path("/upload/{id}")
	public Response uploadFile(@Context HttpServletRequest req, @PathParam("id") long id) {
		LOG.fine("Attempt to upload file with id: " + id);
		
		Transaction txn = datastore.beginTransaction();
		try {
			Key fileKey = KeyFactory.createKey("FileUpload", id);
			Entity fileEntity = datastore.get(txn, fileKey);
			if(System.currentTimeMillis() > (long)fileEntity.getProperty("file_expiration")) {
				datastore.delete(fileKey);
				LOG.warning("Expired entity for file upload with id: " + id);
				return Response.status(Status.BAD_REQUEST).build();
			}
			String fileContentType = req.getContentType();
			switch((String)fileEntity.getProperty("file_type")) {
				case "IMAGE":
					switch(fileContentType) {
						case "image/png":
						case "image/jpeg":
						case "image/svg+xml":
						case "image/bmp":
						case "image/tiff":
						case "image/gif":
							break;
						default:
							return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).build();
					}
			}
			InputStream inputStream = (InputStream)req.getInputStream();
			String filePath = (String)fileEntity.getProperty("file_folder") + (String)fileEntity.getProperty("file_name");
			GcsManager.gcsPost(fileContentType, inputStream, filePath);
			datastore.delete(txn, fileKey);
			txn.commit();
			return Response.ok().build();
		} catch (EntityNotFoundException e) {
			txn.rollback();
			LOG.warning("No upload entity for file upload with id: " + id);
			return Response.status(Status.BAD_REQUEST).build();
		} catch (IOException e) {
			txn.rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}

}
