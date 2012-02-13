package eu.scape_project.pw.elements;

import java.util.ArrayList;
import java.util.List;

public class WatchRequest {
	private List<Question> questions; 
	private long id;
	
	public WatchRequest() {
		questions = new ArrayList<Question>();
		id = Math.round(Math.random()*1000);
	}
	
	public void addQuestion(Question q) {
		questions.add(q);
	}
	
	public List<Question> getQuestions() {
		return questions;
	}
	
	public long getid() {
		return id;
	}
}
