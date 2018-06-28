package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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

	private static final Logger LOG = Logger.getLogger(UploadResource.class.getName());
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final long EXPIRATION_TIME = 1000*60*30; //30min
	private static final Map<String, Set<String>> acceptedContentTypes;
	static {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        
        //////////////// IMAGE //////////////////////
        Set<String> imageSet = new HashSet<String>();
        map.put("IMAGE", imageSet);
        // Set content types
        imageSet.add("image/png");
		imageSet.add("image/jpeg");
		imageSet.add("image/svg+xml");
		imageSet.add("image/bmp");
		imageSet.add("image/tiff");
		imageSet.add("image/gif");
        ////////////////////////////////////////////
        
		//////////////// VIDEO ///////////////////////
        Set<String> videoSet = new HashSet<String>();
		map.put("VIDEO", videoSet);
		// Set content types
		videoSet.add("video/x-flv");
		videoSet.add("video/mp4");
		videoSet.add("application/x-mpegURL");
		videoSet.add("video/MP2T");
		videoSet.add("video/3gpp");
		videoSet.add("video/quicktime");
		videoSet.add("video/x-msvideo");
		videoSet.add("video/x-ms-wmv");
		videoSet.add("video/webm");
		videoSet.add("video/ogg");
		////////////////////////////////////////////
		
		//////////////// IMAGE&VIDEO /////////////////////
		Set<String> imageVideoSet = new HashSet<String>();
		map.put("IMAGE&VIDEO", imageVideoSet);
		// Set content types
		imageVideoSet.addAll(imageSet);
		imageVideoSet.addAll(videoSet);
		////////////////////////////////////////////
		
		// Create the immutable map
        acceptedContentTypes = Collections.unmodifiableMap(map);
    }
	
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
			if(!acceptedContentTypes.get((String)fileEntity.getProperty("file_type")).contains(fileContentType)) {
				return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).build();
			}
			InputStream inputStream = (InputStream)req.getInputStream();
			String filePath = (String)fileEntity.getProperty("file_folder") + (String)fileEntity.getProperty("file_name");
			boolean isPublic = (boolean)fileEntity.getProperty("file_is_public");
			boolean isTemporary = (boolean)fileEntity.getProperty("file_is_temporary");
			GcsManager.gcsPost(fileContentType, inputStream, filePath, isPublic, isTemporary);
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
	
	public static Entity newUploadFileEntity(String folder, String name, String type, boolean isPublic, 
			boolean isTemporary) {
		Entity fileUpload = new Entity("FileUpload");
		fileUpload.setProperty("file_folder", folder);
		fileUpload.setProperty("file_name", name);
		fileUpload.setProperty("file_expiration", System.currentTimeMillis() + EXPIRATION_TIME);
		fileUpload.setProperty("file_type", type);
		fileUpload.setProperty("file_is_public", isPublic);
		fileUpload.setProperty("file_is_temporary", isTemporary);
		return fileUpload;
	}

}
