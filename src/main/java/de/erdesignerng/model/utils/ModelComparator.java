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
package de.erdesignerng.model.utils;

import de.erdesignerng.ERDesignerBundle;
import de.erdesignerng.model.Attribute;
import de.erdesignerng.model.AttributeList;
import de.erdesignerng.model.Index;
import de.erdesignerng.model.IndexList;
import de.erdesignerng.model.Model;
import de.erdesignerng.model.Relation;
import de.erdesignerng.model.RelationList;
import de.erdesignerng.model.Table;
import de.erdesignerng.model.TableList;
import de.erdesignerng.model.View;
import de.erdesignerng.model.ViewList;
import de.mogwai.common.i18n.ResourceHelper;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A comparator for models.
 *
 * @author mirkosertic
 */
public class ModelComparator {

	private static final ResourceHelper HELPER = ResourceHelper.getResourceHelper(ERDesignerBundle.BUNDLE_NAME);

	/**
	 * Compare two models.
	 *
	 * @param aCurrentModel  the current model
	 * @param aDatabaseModel the database model
	 * @return the compare result
	 */
	public ModelCompareResult compareModels(Model aCurrentModel, Model aDatabaseModel) {
		DefaultMutableTreeNode theModelSideRootNode = new DefaultMutableTreeNode(HELPER.getText(ERDesignerBundle.CURRENTMODEL));
		DefaultMutableTreeNode theDBSideRootNode = new DefaultMutableTreeNode(HELPER.getText(ERDesignerBundle.OTHERMODEL));

		TableList theAllTables = new TableList();
		theAllTables.addAll(aCurrentModel.getTables());

        aDatabaseModel.getTables().stream().filter(theTable -> theAllTables.findByName(theTable.getName()) == null).forEach(theAllTables::add);

		for (Table theTable : theAllTables) {

			String theTableName = theTable.getName();

			DefaultMutableTreeNode theModelSideTableNode;
			DefaultMutableTreeNode theDBSideTableNode;

			Table theTableFromModel = aCurrentModel.getTables().findByName(theTableName);
			Table theTableFromDB = null;

			// Add it to both sides
			if (theTableFromModel != null) {

				// Entity exists in model
				theModelSideTableNode = new DefaultMutableTreeNode(theTableName);
				theModelSideRootNode.add(theModelSideTableNode);

			} else {

				// Entity does not exist in model
				theModelSideTableNode = new DefaultMutableTreeNode(new MissingEntityInfo(theTableName));
				theModelSideRootNode.add(theModelSideTableNode);

			}

			if (aDatabaseModel.getTables().findByName(theTableName) != null) {

				// Entity exists in db
				theDBSideTableNode = new DefaultMutableTreeNode(theTableName);
				theDBSideRootNode.add(theDBSideTableNode);

				theTableFromDB = aDatabaseModel.getTables().findByName(theTableName);

			} else {

				// Entity does not exists in db
				theDBSideTableNode = new DefaultMutableTreeNode(new MissingEntityInfo(theTableName));
				theDBSideRootNode.add(theDBSideTableNode);

			}

			AttributeList theAllAttributes = new AttributeList();
			IndexList theAllIndexes = new IndexList();
			RelationList theAllRelations = new RelationList();

			if (theTableFromModel != null) {
				theAllAttributes.addAll(theTableFromModel.getAttributes());
				theAllIndexes.addAll(theTableFromModel.getIndexes());
				theAllRelations.addAll(aCurrentModel.getRelations().getForeignKeysFor(theTableFromModel));
			}

			if (theTableFromDB != null) {
                theTableFromDB.getAttributes().stream().filter(theAttribute -> theAllAttributes.findByName(theAttribute.getName()) == null).forEach(theAllAttributes::add);
                theTableFromDB.getIndexes().stream().filter(theIndex -> theAllIndexes.findByName(theIndex.getName()) == null).forEach(theAllIndexes::add);
                aDatabaseModel.getRelations().getForeignKeysFor(theTableFromDB).stream().filter(theRelation -> theAllRelations.findByName(theRelation.getName()) == null).forEach(theAllRelations::add);
			}

			generateAttributesForTable(theModelSideTableNode, theDBSideTableNode, theTableFromModel, theTableFromDB,
					theAllAttributes);

			DefaultMutableTreeNode theIndexModelSideNode = new DefaultMutableTreeNode(HELPER
					.getText(ERDesignerBundle.INDEXES));
			DefaultMutableTreeNode theIndexDBSideNode = new DefaultMutableTreeNode(HELPER
					.getText(ERDesignerBundle.INDEXES));

			theModelSideTableNode.add(theIndexModelSideNode);
			theDBSideTableNode.add(theIndexDBSideNode);

			generateIndexesForTable(theIndexModelSideNode, theIndexDBSideNode, theTableFromModel, theTableFromDB,
					theAllIndexes);

			DefaultMutableTreeNode theRelationsModelSideNode = new DefaultMutableTreeNode(HELPER
					.getText(ERDesignerBundle.RELATIONS));
			DefaultMutableTreeNode theRelationsDBSideNode = new DefaultMutableTreeNode(HELPER
					.getText(ERDesignerBundle.RELATIONS));

			theModelSideTableNode.add(theRelationsModelSideNode);
			theDBSideTableNode.add(theRelationsDBSideNode);

			generateRelationsForTable(theRelationsModelSideNode, theRelationsDBSideNode, theTableFromModel,
					theTableFromDB, theAllRelations, aDatabaseModel, aCurrentModel);

		}

		ViewList theAllViews = new ViewList();
		theAllViews.addAll(aCurrentModel.getViews());

        aDatabaseModel.getViews().stream().filter(theView -> theAllViews.findByName(theView.getName()) == null).forEach(theAllViews::add);

		for (View theView : theAllViews) {

			String theViewName = theView.getName();

			DefaultMutableTreeNode theModelSideTableNode;
			DefaultMutableTreeNode theDBSideTableNode;

			View theViewFromModel = aCurrentModel.getViews().findByName(theViewName);
			View theViewFromDB = aDatabaseModel.getViews().findByName(theViewName);

			// Add it to both sides
			if (theViewFromModel != null) {

				if (theViewFromModel.isModified(theView)) {
					// View was redefined
					theModelSideTableNode = new DefaultMutableTreeNode(new RedefinedViewInfo(theViewName));
					theModelSideRootNode.add(theModelSideTableNode);
				} else {
					// View exists in model
					theModelSideTableNode = new DefaultMutableTreeNode(theViewName);
					theModelSideRootNode.add(theModelSideTableNode);
				}

			} else {

				// Entity does not exist in model
				theModelSideTableNode = new DefaultMutableTreeNode(new MissingViewInfo(theViewName));
				theModelSideRootNode.add(theModelSideTableNode);

			}

			if (theViewFromDB != null) {

				if (theViewFromDB.isModified(theView)) {
					// View was redefined
					theDBSideTableNode = new DefaultMutableTreeNode(new RedefinedViewInfo(theViewName));
					theDBSideRootNode.add(theDBSideTableNode);

				} else {
					// View exists in db
					theDBSideTableNode = new DefaultMutableTreeNode(theViewName);
					theDBSideRootNode.add(theDBSideTableNode);
				}

			} else {

				// Entity does not exists in db
				theDBSideTableNode = new DefaultMutableTreeNode(new MissingViewInfo(theViewName));
				theDBSideRootNode.add(theDBSideTableNode);

			}
		}
		return new ModelCompareResult(theModelSideRootNode, theDBSideRootNode);
	}

	private void generateAttributesForTable(DefaultMutableTreeNode aModelSideTableNode,
											DefaultMutableTreeNode aDBSideTableNode, Table aTableFromModel, Table aTableFromDB,
											AttributeList<Table> aAllAttributes) {

		// Now, do it for each attribute
		for (Attribute<Table> theAttribute : aAllAttributes) {
			String theAttributeName = theAttribute.getName();

			// First, handle the model side
			if (aTableFromModel != null) {
				Attribute<Table> theAttributeFromModel = aTableFromModel.getAttributes().findByName(theAttributeName);

				if (theAttributeFromModel != null) {
					Attribute<Table> theAttributeFromDB = null;
					if (aTableFromDB != null) {
						theAttributeFromDB = aTableFromDB.getAttributes().findByName(theAttributeName);
					}

					if (theAttributeFromDB != null) {

						if (theAttributeFromModel.isModified(theAttributeFromDB, true)) {
							// Compute the difference

							String theDiffInfo = theAttributeFromModel.getPhysicalDeclaration();

							// Differences in definition
							DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedAttributeInfo(
									theAttributeName + " " + theDiffInfo));
							aModelSideTableNode.add(error);

						} else {
							DefaultMutableTreeNode existing = new DefaultMutableTreeNode(theAttributeName);
							aModelSideTableNode.add(existing);
						}

						// Here, we have to compare the attributes

					} else {

						DefaultMutableTreeNode existing = new DefaultMutableTreeNode(theAttributeName);
						aModelSideTableNode.add(existing);
					}

				} else {

					// The entity is existing, but the attribute is
					// missing
					DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingAttributeInfo(
							theAttributeName));
					aModelSideTableNode.add(missing);

				}

			} else {

				// The entity is not existing in the model, so every
				// attribute is missing
				DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingAttributeInfo(theAttributeName));
				aModelSideTableNode.add(missing);

			}

			// Now, the database side
			if (aTableFromDB != null) {

				Attribute<Table> theAttributeFromDB = aTableFromDB.getAttributes().findByName(theAttributeName);

				if (theAttributeFromDB != null) {

					Attribute<Table> theAttributeFromModel = null;
					if (aTableFromModel != null) {
						theAttributeFromModel = aTableFromModel.getAttributes().findByName(theAttributeName);
					}

					if (theAttributeFromModel != null) {

						if (theAttributeFromModel.isModified(theAttributeFromDB, true)) {

							String diffInfo = theAttributeFromDB.getPhysicalDeclaration();

							// Modified
							// Differences in definition
							DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedAttributeInfo(
									theAttributeName + " " + diffInfo));
							aDBSideTableNode.add(error);

						} else {
							DefaultMutableTreeNode existing = new DefaultMutableTreeNode(theAttributeName);
							aDBSideTableNode.add(existing);
						}

					} else {

						DefaultMutableTreeNode existing = new DefaultMutableTreeNode(theAttributeName);
						aDBSideTableNode.add(existing);
					}

				} else {

					// The entity is existing, but the attribute is
					// missing
					DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingAttributeInfo(
							theAttributeName));
					aDBSideTableNode.add(missing);

				}

			} else {

				// The entity is not existing in the model, so every
				// attribute is missing
				DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingAttributeInfo(theAttributeName));
				aDBSideTableNode.add(missing);

			}
		}
	}

	private void generateIndexesForTable(DefaultMutableTreeNode aModelSideTableNode,
										 DefaultMutableTreeNode aDBSideTableNode, Table aTableFromModel, Table aTableFromDB, IndexList aAllIndexes) {

		// Now, do it for each attribute
		for (Index theAttribute : aAllIndexes) {
			String theIndexName = theAttribute.getName();

			// First, handle the model side
			if (aTableFromModel != null) {

				Index theIndexFromModel = aTableFromModel.getIndexes().findByName(theIndexName);

				if (theIndexFromModel != null) {

					Index theIndexFromDB = null;
					if (aTableFromDB != null) {
						theIndexFromDB = aTableFromDB.getIndexes().findByName(theIndexName);
					}

					if (theIndexFromDB != null) {

						if (theIndexFromModel.isModified(theIndexFromDB, true)) {
							// Compute the difference

							String theDiffInfo = "";

							// Differences in definition
							DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedIndexInfo(
									theIndexName + " " + theDiffInfo));
							aModelSideTableNode.add(error);

						} else {
							DefaultMutableTreeNode existing = new DefaultMutableTreeNode(theIndexName);
							aModelSideTableNode.add(existing);
						}

						// Here, we have to compare the attributes

					} else {

						DefaultMutableTreeNode existing = new DefaultMutableTreeNode(theIndexName);
						aModelSideTableNode.add(existing);
					}

				} else {

					// The entity is existing, but the attribute is
					// missing
					DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingIndexInfo(theIndexName));
					aModelSideTableNode.add(missing);

				}

			} else {

				// The entity is not existing in the model, so every
				// attribute is missing
				DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingIndexInfo(theIndexName));
				aModelSideTableNode.add(missing);

			}

			// Now, the database side
			if (aTableFromDB != null) {

				Index theIndexFromDB = aTableFromDB.getIndexes().findByName(theIndexName);

				if (theIndexFromDB != null) {

					Index theIndexFromModel = null;
					if (aTableFromModel != null) {
						theIndexFromModel = aTableFromModel.getIndexes().findByName(theIndexName);
					}

					if (theIndexFromModel != null) {

						if (theIndexFromModel.isModified(theIndexFromDB, true)) {

							String diffInfo = "";

							// Modified
							// Differences in definition
							DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedIndexInfo(
									theIndexName + " " + diffInfo));
							aDBSideTableNode.add(error);

						} else {
							DefaultMutableTreeNode existing = new DefaultMutableTreeNode(theIndexName);
							aDBSideTableNode.add(existing);
						}

					} else {

						DefaultMutableTreeNode existing = new DefaultMutableTreeNode(theIndexName);
						aDBSideTableNode.add(existing);
					}

				} else {

					// The entity is existing, but the attribute is
					// missing
					DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingIndexInfo(theIndexName));
					aDBSideTableNode.add(missing);

				}

			} else {

				// The entity is not existing in the model, so every
				// attribute is missing
				DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingIndexInfo(theIndexName));
				aDBSideTableNode.add(missing);

			}
		}
	}

	private void generateRelationsForTable(DefaultMutableTreeNode aModelSideTableNode,
										   DefaultMutableTreeNode aDBSideTableNode, Table aTableFromModel, Table aTableFromDB,
										   RelationList aAllRelations, Model aDBModel, Model aCurrentModel) {

		// Now, do it for each attribute
		for (Relation theRelation : aAllRelations) {
			String theRelationName = theRelation.getName();

			// First, handle the model side
			if (aTableFromModel != null) {

				Relation theRelationFromModel = aCurrentModel.getRelations().findByName(theRelationName);

				if (theRelationFromModel != null) {

					Relation theRelationFromDB = null;
					if (aTableFromDB != null) {
						theRelationFromDB = aDBModel.getRelations().findByName(theRelationName);
					}

					if (theRelationFromDB != null) {

						if (theRelationFromModel.isModified(theRelationFromDB, true)) {
							// Compute the difference

							String theDiffInfo = "";

							// Differences in definition
							DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedRelationInfo(
									theRelationName + " " + theDiffInfo));
							aModelSideTableNode.add(error);

						} else {
							DefaultMutableTreeNode existing = new DefaultMutableTreeNode(theRelationName);
							aModelSideTableNode.add(existing);
						}

						// Here, we have to compare the attributes

					} else {

						DefaultMutableTreeNode existing = new DefaultMutableTreeNode(theRelationName);
						aModelSideTableNode.add(existing);
					}

				} else {

					// The entity is existing, but the attribute is
					// missing
					DefaultMutableTreeNode missing = new DefaultMutableTreeNode(
							new MissingRelationInfo(theRelationName));
					aModelSideTableNode.add(missing);

				}

			} else {

				// The entity is not existing in the model, so every
				// attribute is missing
				DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingRelationInfo(theRelationName));
				aModelSideTableNode.add(missing);

			}

			// Now, the database side
			if (aTableFromDB != null) {

				Relation theRelationFromDB = aDBModel.getRelations().findByName(theRelationName);

				if (theRelationFromDB != null) {

					Relation theRelationFromModel = null;
					if (aTableFromModel != null) {
						theRelationFromModel = aCurrentModel.getRelations().findByName(theRelationName);
					}

					if (theRelationFromModel != null) {

						if (theRelationFromModel.isModified(theRelationFromDB, true)) {

							String diffInfo = "";

							// Modified
							// Differences in definition
							DefaultMutableTreeNode error = new DefaultMutableTreeNode(new RedefinedRelationInfo(
									theRelationName + " " + diffInfo));
							aDBSideTableNode.add(error);

						} else {
							DefaultMutableTreeNode existing = new DefaultMutableTreeNode(theRelationName);
							aDBSideTableNode.add(existing);
						}

					} else {

						DefaultMutableTreeNode existing = new DefaultMutableTreeNode(theRelationName);
						aDBSideTableNode.add(existing);
					}

				} else {

					// The entity is existing, but the attribute is
					// missing
					DefaultMutableTreeNode missing = new DefaultMutableTreeNode(
							new MissingRelationInfo(theRelationName));
					aDBSideTableNode.add(missing);

				}

			} else {

				// The entity is not existing in the model, so every
				// attribute is missing
				DefaultMutableTreeNode missing = new DefaultMutableTreeNode(new MissingRelationInfo(theRelationName));
				aDBSideTableNode.add(missing);

			}
		}
	}
}
