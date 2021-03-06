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
package de.erdesignerng.model.serializer;

import de.erdesignerng.model.Model;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.model.OwnedModelItem;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class CommonAbstractXMLSerializer<T extends OwnedModelItem> implements CommonXMLElementsAndAttributes {

	protected Element addElement(Document aDocument, Node aNode, String aElementName) {
		Element theElement = aDocument.createElement(aElementName);
		aNode.appendChild(theElement);
		return theElement;
	}

	protected void setBooleanAttribute(Element aElement, String aAttributeName, boolean aValue) {
		aElement.setAttribute(aAttributeName, aValue ? TRUE : FALSE);
	}

	protected void serializeProperties(Document aDocument, Element aNode, ModelItem aItem) {

		aNode.setAttribute(ID, aItem.getSystemId());
		aNode.setAttribute(NAME, aItem.getName());

		for (String theKey : aItem.getProperties().getProperties().keySet()) {
			String theValue = aItem.getProperties().getProperties().get(theKey);
			if (theValue != null) {
				Element theProperty = addElement(aDocument, aNode, PROPERTY);
				theProperty.setAttribute(NAME, theKey);
				theProperty.setAttribute(VALUE, theValue);
			}
		}
	}

	protected void serializeCommentElement(Document aDocument, Element aElement, ModelItem aItem) {
		if (!StringUtils.isEmpty(aItem.getComment())) {
			Element theCommentElement = aDocument.createElement(COMMENT);
			theCommentElement.appendChild(aDocument.createTextNode(aItem.getComment()));

			aElement.appendChild(theCommentElement);
		}
	}

	protected void deserializeCommentElement(Element aElement, ModelItem aItem) {
		NodeList theChildren = aElement.getChildNodes();
		for (int i = 0; i < theChildren.getLength(); i++) {
			Node theChild = theChildren.item(i);
			if (COMMENT.equals(theChild.getNodeName())) {
				Element theElement = (Element) theChild;
				if (theElement.getChildNodes().getLength() > 0) {
					aItem.setComment(theElement.getChildNodes().item(0).getNodeValue());
				}
			}
		}
	}

	protected void deserializeProperties(Element aElement, ModelItem aModelItem) {

		aModelItem.setSystemId(aElement.getAttribute(ID));
		aModelItem.setName(aElement.getAttribute(NAME));

		NodeList theProperties = aElement.getElementsByTagName(PROPERTY);
		for (int i = 0; i < theProperties.getLength(); i++) {
			Element theElement = (Element) theProperties.item(i);

			aModelItem.getProperties().setProperty(theElement.getAttribute(NAME), theElement.getAttribute(VALUE));
		}
	}

	public abstract void serialize(T aModelItem, Document aDocument, Element aRootElement);

	public abstract void deserialize(Model aModel, Document aDocument);
}
