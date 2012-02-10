package eu.scape_project.watch.core.model;

public class Question {

	private String sparql;

	public Question() {
		super();
	}

	public Question(String sparql) {
		super();
		this.sparql = sparql;
	}

	public String getSparql() {
		return sparql;
	}

	public void setSparql(String sparql) {
		this.sparql = sparql;
	}

}
