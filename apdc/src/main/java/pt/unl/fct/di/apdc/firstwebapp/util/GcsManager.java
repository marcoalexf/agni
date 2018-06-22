package pt.unl.fct.di.apdc.firstwebapp.util;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.tools.cloudstorage.GcsFileMetadata;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFileOptions.Builder;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

import javax.servlet.http.HttpServletResponse;


public class GcsManager {

	private static final boolean SERVE_USING_BLOBSTORE_API = true;

	/**
	 * This is where backoff parameters are configured. Here it is aggressively retrying with
	 * backoff, up to 10 times but taking no more that 15 seconds total to do so.
	 */
	private static final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
		.initialRetryDelayMillis(10)
		.retryMaxAttempts(10)
		.totalRetryPeriodMillis(15000)
		.build());

	/**Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB */
	private static final int BUFFER_SIZE = 2 * 1024 * 1024;
  
	public static boolean gcsGet(HttpServletResponse resp, String filename, boolean temporary) {
		try {
			/**GcsFilename fileName = temporary?
			new GcsFilename("staging.liquid-layout-196103.appspot.com", filename)
			:new GcsFilename("liquid-layout-196103.appspot.com", filename);**/
			GcsFilename fileName = temporary?
					new GcsFilename("staging.custom-tine-204615.appspot.com", filename)
					:new GcsFilename("custom-tine-204615.appspot.com", filename);
			GcsFileMetadata metadata = gcsService.getMetadata(fileName);
			String contentType = metadata.getOptions().getContentEncoding();
			if (SERVE_USING_BLOBSTORE_API) {
				BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
				BlobKey blobKey = blobstoreService.createGsBlobKey(
					  "/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName());
				resp.setHeader("content-type", contentType); 
				blobstoreService.serve(blobKey, resp);
			} else {
				GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
				resp.setHeader("content-type", contentType);
				copy(Channels.newInputStream(readChannel), resp.getOutputStream());
			}
			return true;
		} catch (IOException e) {
			return false;
		}
	}
  
	public static boolean gcsGet(HttpServletResponse resp, String filename) {
		return gcsGet(resp, filename, false);
	}

	public static boolean gcsPost(String contentType, InputStream inputStream, String filename, 
			boolean publicFile, boolean temporary) {
		try {
			Builder builder = new GcsFileOptions.Builder().mimeType(contentType);
			if(publicFile) {
				builder.acl("public-read");
			}
			GcsFileOptions instance = builder.build();
			/**GcsFilename fileName = temporary?
					new GcsFilename("staging.liquid-layout-196103.appspot.com", filename)
					:new GcsFilename("liquid-layout-196103.appspot.com", filename);**/
			GcsFilename fileName = temporary?
					new GcsFilename("staging.custom-tine-204615.appspot.com", filename)
					:new GcsFilename("custom-tine-204615.appspot.com", filename);
			GcsOutputChannel outputChannel;
			outputChannel = gcsService.createOrReplace(fileName, instance);
			copy(inputStream, Channels.newOutputStream(outputChannel));
			return true;
		} catch (IOException e) {
		return false;
		}
	}
	
	public static boolean gcsPost(String contentType, InputStream inputStream, String filename, 
			boolean publicFile) {
		return gcsPost(contentType, inputStream, filename, publicFile, false);
	}
  
	public static boolean gcsPost(String contentType, InputStream inputStream, String filename) {
		return gcsPost(contentType, inputStream, filename, true, false);
	}
 

	/**
	 * Transfer the data from the inputStream to the outputStream. Then close both streams.
	 */
	private static void copy(InputStream input, OutputStream output) throws IOException {
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = input.read(buffer);
			while (bytesRead != -1) {
				output.write(buffer, 0, bytesRead);
				bytesRead = input.read(buffer);
			}
		} finally {
			input.close();
			output.close();
		}
	}
}
