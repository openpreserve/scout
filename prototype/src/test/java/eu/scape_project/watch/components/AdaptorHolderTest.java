package eu.scape_project.watch.components;

import org.junit.Assert;
import org.junit.Test;

public class AdaptorHolderTest {

	@Test
	public void testAdaptorHolder() {
		AdaptorHolder tAdaptorHolder = new AdaptorHolder();
		Assert.assertTrue(tAdaptorHolder.getNextTime()==Long.MAX_VALUE);
	}
}
