package eu.scape_project.watch.core.rest.data;

import java.util.ArrayList;
import java.util.List;

import eu.scape_project.watch.core.rest.model.AsyncRequest;

public class AsyncRequestData {
	static List<AsyncRequest> asyncRequests = new ArrayList<AsyncRequest>();

	static {
		asyncRequests.add(createAsyncRequest(0));
		asyncRequests.add(createAsyncRequest(1));
		asyncRequests.add(createAsyncRequest(2));
		asyncRequests.add(createAsyncRequest(3));
		asyncRequests.add(createAsyncRequest(4));
		asyncRequests.add(createAsyncRequest(5));
	}

	static AsyncRequest createAsyncRequest(long id) {
		AsyncRequest asyncRequest = new AsyncRequest();
		asyncRequest.setId(id);
		return asyncRequest;
	}

	public AsyncRequest getAsyncRequestById(long id) {
		AsyncRequest ret = null;
		for (AsyncRequest asyncRequest : asyncRequests) {
			if(asyncRequest.getId()==id) {
				ret = asyncRequest;
				break;
			}
		}
		return ret;
	}
	
	public void addAsyncRequest(AsyncRequest asyncRequest) {
		asyncRequests.add(asyncRequest);
	}
	
	
}
