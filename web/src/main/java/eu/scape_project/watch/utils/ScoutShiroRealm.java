package eu.scape_project.watch.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.realm.text.IniRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scape_project.watch.main.ScoutManager;

public class ScoutShiroRealm extends IniRealm {
	private Logger log = LoggerFactory.getLogger(getClass());

	public ScoutShiroRealm() {
		super();

		String usersConfig = ScoutManager.getConfig().getStringProperty(
				ConfigUtils.USERS_CONFIG_KEY);

		if (!ResourceUtils.resourceExists("file:" + usersConfig)) {
			InputStream defaultUsers = getClass().getResourceAsStream(
					"/users.ini");
			try {
				IOUtils.copy(defaultUsers, new FileOutputStream(new File(
						usersConfig)));
			} catch (FileNotFoundException e) {
				log.error("Could not copy default users to static path", e);
				log.warn("Using internal config");
				setResourcePath("classpath:/users.ini");

			} catch (IOException e) {
				log.error("Could not copy default users to static path", e);
				log.warn("Using internal config");
				setResourcePath("classpath:/users.ini");
			} finally {
				setResourcePath("file:" + usersConfig);
			}

		} else {
			setResourcePath("file:" + usersConfig);
		}

	}

}
