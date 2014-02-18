package eu.scape_project.watch.merging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.dao.DAO;
import eu.scape_project.watch.domain.Entity;
import eu.scape_project.watch.domain.PropertyValue;
import eu.scape_project.watch.domain.SourceAdaptor;

/**
 * Default rule to merge entities and property values into the knowledge base.
 * 
 * @author Luis Faria <lfaria@keep.pt>
 * 
 */
public class DefaultMergeRule implements MergeRule {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void mergeEntity(final Entity entity) {
		String name = entity.getName();
		if (name.contains("_")) {
			String[] splits = name.split("_");
			Entity newEntity = new Entity(entity.getType(), splits[0]);
			log.trace("Mapped entity backlog: {}", newEntity);
			DAO.ENTITY.save(newEntity);
		} else {
			DAO.ENTITY.save(entity);
		}
	}

	@Override
	public void mergePropertyValue(final SourceAdaptor adaptor,
			final PropertyValue propertyValue) {
		Entity entity = propertyValue.getEntity();
		String name = entity.getName();
		if (name.contains("_")) {
			String[] splits = name.split("_");
			Entity newEntity = new Entity(entity.getType(), splits[0]);
			propertyValue.setEntity(newEntity);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			try {
				date = sdf.parse(splits[1]);
			} catch (ParseException e) {
				log.error("Error merging property value " + propertyValue, e);
			}
			log.trace("Mapped property value backlog to date {}: {}", date,
					propertyValue);
			DAO.PROPERTY_VALUE.save(adaptor, date, propertyValue);
		} else {
			DAO.PROPERTY_VALUE.save(adaptor, propertyValue);
		}
	}

}
