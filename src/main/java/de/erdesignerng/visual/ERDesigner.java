/**
 * Mogwai ERDesigner. Copyright (C) 2002 The Mogwai Project.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package de.erdesignerng.visual;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;

import javax.swing.JFrame;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import de.erdesignerng.dialect.DataTypeIO;
import de.mogwai.common.client.looks.components.DefaultSplashScreen;

/**
 * @author $Author: mirkosertic $
 * @version $Date: 2008-11-17 20:53:55 $
 */

/**
 * 程序入口
 *  
 *@Date 2016年11月16日 下午5:21:40
 */
public final class ERDesigner {

	private final static  Logger LOGGER = LoggerFactory.getLogger(ERDesigner.class);
	
	private ERDesigner() {
	}

	public static void main(String[] args)
			throws
			IllegalAccessException,
			TransformerException, IOException, ParserConfigurationException,
			SAXException {

		String theFilenameToOpen = null;
		if (args != null) {
			for (String theArgument : args) {
				LOGGER.info("Was called with argument :" + theArgument);
			}
			// In WebStart mode or standalone, there can be two options
			// -open <filename>
			// -print <filename>
			if (args.length == 2) {
				if ("-open".equals(args[0])  || "-print".equals(args[0])) {
					theFilenameToOpen = args[1];
				}
			}
		}
		
		// Disable D3D rendering pipeline
		System.setProperty("sun.java2d.d3d", "false");
		
		//Show  LOGO
		DefaultSplashScreen theScreen = new DefaultSplashScreen(
				"/de/erdesignerng/splashscreen.jpg");
		theScreen.setVisible(true);
		
		DataTypeIO.getInstance().loadUserTypes();
		
		final ERDesignerMainFrame frame = new ERDesignerMainFrame();
		frame.setModel(frame.createNewModel());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// Just wait here :-)
		}
		
		theScreen.setVisible(false);
		frame.setVisible(true);
		
		if (StringUtils.isNotEmpty(theFilenameToOpen)) {
			frame.commandOpenFile(new File(theFilenameToOpen));
		}
	}
}