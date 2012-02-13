package eu.scape_project.components;

import eu.scape_project.pw.elements.Result;
import eu.scape_project.pw.elements.Task;
import eu.scape_project.watch.core.model.PropertyValue;

public class DummyAdaptor2 extends Adaptor {

	@Override
	public boolean checkForTask(Task t) {
		if (t.getEntity().getName()=="pdf" && t.getProperty().getName()=="number_of_browser")
			return true;
		return false;
	}

	@Override
	protected void fetchData() {
		try {
			Thread.sleep(1000);
			//System.out.println("Adaptor fetching results "+tasks.size());
			for (int i=0; i<tasks.size(); i++){
				Task t = tasks.get(i);
				PropertyValue v = new PropertyValue();
				v.setValue((new Long(Math.round(Math.random()*10))).toString());
				Result r = new Result(t.getEntity(),t.getProperty(),v);
				results.add(r);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
