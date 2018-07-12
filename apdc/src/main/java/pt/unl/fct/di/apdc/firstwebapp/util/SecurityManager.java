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
        
		////////////// WORKER //////////////////////
		Set<String> workerSet = new HashSet<String>();
		map.put("WORKER", workerSet);
		// Set rights
		workerSet.addAll(userSet);
		workerSet.add("see_private_occurrences");
		workerSet.add("accept_user_occurrence");
		workerSet.add("resolve_user_occurrence");
		workerSet.add("see_approved_occurrences");
		////////////////////////////////////////////
        
		//////////////// MOD ///////////////////////
        Set<String> modSet = new HashSet<String>();
		map.put("MOD", modSet);
		// Set rights
		modSet.addAll(userSet);
		modSet.add("edit_user_occurrence_comment");
		modSet.add("see_private_occurrences");
		modSet.add("see_user_profile");
		modSet.add("edit_user_occurrence");
		modSet.add("delete_user_occurrence");
		modSet.add("see_liked_occurrences");
		modSet.add("conclude_user_occurrence");
		modSet.add("reject_user_occurrence");
		modSet.add("approve_worker");
		modSet.add("remove_worker");
		modSet.add("see_accepted_occurrences");
		modSet.add("verify_accepted_occurrence");
		modSet.add("approve_user_occurrence");
		modSet.add("see_approved_occurrences");
		modSet.add("see_resolved_occurrences");
		////////////////////////////////////////////
		
		//////////////// ADMIN /////////////////////
		Set<String> adminSet = new HashSet<String>();
		map.put("ADMIN", adminSet);
		// Set rights
		adminSet.addAll(modSet);
		adminSet.addAll(workerSet);
		adminSet.add("edit_user_profile");
		adminSet.add("make_moderator");
		adminSet.add("remove_moderator");
		////////////////////////////////////////////
		
		// Create the immutable map
        accesses = Collections.unmodifiableMap(map);
    }
	
	public static boolean roleHasAccess(String request, String role) {
		Set<String> roleRights = accesses.get(role);
		if(roleRights == null) {
			return false;
		}
		return roleRights.contains(request);
	}
	
	public static boolean userHasAccess(String request, long userID) {
		Key userKey = KeyFactory.createKey("User", userID);
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
