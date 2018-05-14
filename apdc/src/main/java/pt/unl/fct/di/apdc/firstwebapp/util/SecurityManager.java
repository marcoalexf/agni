package pt.unl.fct.di.apdc.firstwebapp.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class SecurityManager {
	
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final Map<String, Set<String>> accesses;
    static {
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        
        //////////////// USER //////////////////////
        Set<String> userSet = new HashSet<String>();
        map.put("USER", userSet);
        // Set rights
        ////////////////////////////////////////////
        
		//////////////// MOD ///////////////////////
        Set<String> modSet = new HashSet<String>();
		map.put("MOD", modSet);
		// Set rights
		modSet.addAll(userSet);
		modSet.add("see_private_occurrences");
		////////////////////////////////////////////
		
		//////////////// ADMIN /////////////////////
		Set<String> adminSet = new HashSet<String>();
		map.put("ADMIN", adminSet);
		// Set rights
		modSet.addAll(modSet);
		////////////////////////////////////////////
		
		// Create the immutable map
        accesses = Collections.unmodifiableMap(map);
    }
	
	public static boolean roleHasAccess(String request, String role) {
		return accesses.get(role).contains(request);
	}
	
	public static boolean userHasAccess(String request, String username) {
		Key userKey = KeyFactory.createKey("User", username);
		Entity user;
		try {
			user = datastore.get(userKey);
		} catch (EntityNotFoundException e) {
			return false;
		}
		String role = (String)user.getProperty("user_role");
		return roleHasAccess(request, role);
	}

}
