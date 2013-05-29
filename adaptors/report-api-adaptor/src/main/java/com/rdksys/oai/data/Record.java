
package com.rdksys.oai.data;

import java.io.Serializable;

/**
 * @author David Uvalle, david.uvalle@gmail.com
 * @version 0.1
 * 
 */
public class Record implements Serializable {

	private static final long serialVersionUID = 1L;
	private Header header;
	private Metadata metadata;
	
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public Metadata getMetadata() {
		return metadata;
	}
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	

}
