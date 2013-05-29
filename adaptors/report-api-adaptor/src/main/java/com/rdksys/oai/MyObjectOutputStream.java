

package com.rdksys.oai;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author David Uvalle, david.uvalle@gmail.com
 * @version 0.1
 * 
 */
public class MyObjectOutputStream extends ObjectOutputStream {
	public MyObjectOutputStream(OutputStream out) throws IOException {
		super(out);
	}

	@Override
	protected void writeStreamHeader()  {
		
		try {
			super.reset();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
}
