package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

import java.util.List;
import java.util.UUID;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class AuthToken {

	public static final long EXPIRATION_TIME = 1000*60*60*2; //2h

	public long userID;
	public String username;
	public String tokenID;
	public long creationData;
	public long expirationData;

	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public AuthToken() {

	}

	public AuthToken(long userID, String username) {
		this.userID = userID;
		this.username = username;
		this.tokenID = UUID.randomUUID().toString();
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
	}

	public AuthToken(long userID, String username, String tokenID, long creationData, long expirationData) {
		this.userID = userID;
		this.username = username;
		this.tokenID = tokenID;
		this.creationData = creationData;
		this.expirationData = expirationData;
	}

	public boolean isTokenValid() {
		Key userKey = KeyFactory.createKey("User", userID);
		Filter propertyFilter = new FilterPredicate("user_token_id", FilterOperator.EQUAL, tokenID);
		Query ctrQuery = new Query("UserToken").setAncestor(userKey).setFilter(propertyFilter);
		List<Entity> results = datastore.prepare(ctrQuery).asList(FetchOptions.Builder.withDefaults());
		for(Entity tokenEntity: results) {
			if(((String)tokenEntity.getProperty("user_token_username")).equals(username) && (long)tokenEntity.getProperty("user_token_creation_data")  == creationData && (long)tokenEntity.getProperty("user_token_expiration_data") == expirationData) {
				if(System.currentTimeMillis() >= expirationData) {
					datastore.delete(tokenEntity.getKey());
					return false;
				}
				return true;
			}
		}
		return false;
	}

}
