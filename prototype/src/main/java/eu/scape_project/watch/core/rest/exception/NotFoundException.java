package eu.scape_project.watch.core.rest.exception;

public class NotFoundException extends ApiException {

	public NotFoundException(String msg) {
		super(404, msg);
	}
}
