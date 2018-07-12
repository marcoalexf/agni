package pt.unl.fct.di.apdc.firstwebapp.resources.constructors;

public class OccurrenceApproveListData {
	
	public AuthToken token;
	public String entity;
	public String cursor;

	
	public OccurrenceApproveListData() {
		
	}
	
	public OccurrenceApproveListData(AuthToken token, String entity, String cursor) {
		this.token = token;
		this.entity = entity;
		this.cursor = cursor;
	}
	
	public boolean valid() {
		return entity != null && token != null;
	}
	
}
