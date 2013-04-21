/*
 * Parses XML containing information about Buildings.
 */
//Note: Reusing ResourceXMLParser would be a better idea.
package spaceappschallenge.moonville.xml_parsers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import spaceappschallenge.moonville.businessmodels.Building;
import spaceappschallenge.moonville.businessmodels.Resource;
import spaceappschallenge.moonville.managers.ApplicationService;
import android.content.Context;
import android.util.Log;

public class BuildingXMLParser {
	protected Context context = null;
	protected InputStream inputStream = null;
	protected XmlPullParser xpp;
	protected ArrayList<Building> buildings;
	protected boolean isBuildingFinished = false;

	/*
	 * @param: inputStream: InputStream object with the XML as the file
	 */
	public BuildingXMLParser(InputStream inputStream)
			throws XmlPullParserException {
		this.inputStream = inputStream;
		this.context = ApplicationService.getInstance().getApplicationContext();
		XmlPullParserFactory xmlFactory = XmlPullParserFactory.newInstance();
		xmlFactory.setNamespaceAware(true);
		xpp = xmlFactory.newPullParser();
		this.buildings = new ArrayList<Building>();
		Log.i("BuildingXMLParser", "Done initializing parser");
	}

	String buildingName = "";
	String buildingInfo = "";
	int buildingAmount = 0;
	int buildingInputPower = 0, buildingOutputPower = 0;
	int buildingMonetaryCost = 0, buildingRegolithCost = 0;
	int buildingRequiredTurns = 0;
	int buildingXPos = 0;
	int buildingYPos = 0;
	ArrayList<Resource> requiredResources;
	ArrayList<Building> requiredBuildings;

	public void readBuildingAttributes() {
		// Read the attributes for each building
		buildingName = xpp.getAttributeValue(null, "name");
		buildingInfo = xpp.getAttributeValue(null, "info");
		buildingAmount = Integer
				.parseInt(xpp.getAttributeValue(null, "amount"));
		buildingInputPower = Integer.parseInt(xpp.getAttributeValue(null,
				"inputPower"));
		buildingOutputPower = Integer.parseInt(xpp.getAttributeValue(null,
				"outputPower"));
		buildingMonetaryCost = Integer.parseInt(xpp.getAttributeValue(null,
				"monetaryCost"));
		buildingRegolithCost = Integer.parseInt(xpp.getAttributeValue(null,
				"regolithCost"));
		buildingRequiredTurns = Integer.parseInt(xpp.getAttributeValue(null,
				"requiredTurns"));
		buildingXPos = Integer.parseInt(xpp.getAttributeValue(null,
				"x"));
		buildingYPos = Integer.parseInt(xpp.getAttributeValue(null,
				"y"));
		Log.i("XML", "buildingName: " + buildingName);
	}

	public void addRequiredResource(XmlPullParser xpp,
			ArrayList<Resource> requiredResources) {

		String reqdResName = xpp.getAttributeValue(null, "name");
		int reqdResAmount = Integer.parseInt(xpp.getAttributeValue(null,
				"amount"));
		requiredResources.add(new Resource(reqdResName, reqdResAmount, 0f));
		Log.i("XML", "required resource " + reqdResName);
	}

	public void addRequiredBuilding(XmlPullParser xpp,
			ArrayList<Building> requiredBuildings) {
		String reqdBuildName = xpp.getAttributeValue(null, "name");
		String reqdBuildInfo = xpp.getAttributeValue(null, "info");
		int reqdBuildAmount = 0;
		try {
			reqdBuildAmount = Integer.parseInt(xpp.getAttributeValue(null,
					"amount"));
		} catch (Exception e) {
			Log.e("XMLError", "reqdBuildAmount");
		}
		int reqdBuildInputPower = 0;
		try {
			reqdBuildInputPower = Integer.parseInt(xpp.getAttributeValue(null,
					"inputPower"));
		} catch (Exception e) {
			Log.e("XMLError", "reqdBuildInputPower");
		}
		requiredBuildings.add(new Building(reqdBuildName, reqdBuildInfo,
				reqdBuildAmount, reqdBuildInputPower));
		Log.i("XML", "reqd  building " + reqdBuildName);
	}

	// Create "Building" objects by parsing input stream
	// This is the longest function I have written in my entire life : Robik
	public ArrayList<Building> parse() throws XmlPullParserException,
			IOException {
		Log.i("XML", "bufferedreader....");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				this.inputStream));
		Log.i("XML", "bufferedreader done....");
		xpp.setInput(br);
		Log.i("XML", "xpp done");
		int eventType = xpp.getEventType();

		// Parse the xml file to create "building" objects
		while (eventType != XmlPullParser.END_DOCUMENT) {

			boolean atBuilding = false;
			if (eventType == XmlPullParser.START_TAG
					&& xpp.getName().equalsIgnoreCase("building")) {
				atBuilding = true;
				readBuildingAttributes();
				requiredBuildings = new ArrayList<Building>();
				requiredResources = new ArrayList<Resource>();

				// For Nested elements
				while (atBuilding) {// <building>

					// Break the loop when the end tag: </building> is reached
					if (eventType == XmlPullParser.END_TAG
							&& xpp.getName().equalsIgnoreCase("building")) {
						atBuilding = false;
						break;
					}

					boolean atRequiredResources = false;// The tag:
														// <requiredResources>
														// may be absent
					if (eventType == XmlPullParser.START_TAG
							&& xpp.getName().equalsIgnoreCase(
									"requiredResources")) {
						atRequiredResources = true;
					}
					while (atRequiredResources) {
						// Break the loop when the end tag: </requiredResources>
						// is reached
						if (eventType == XmlPullParser.END_TAG
								&& xpp.getName().equalsIgnoreCase(
										"requiredResources")) {
							atRequiredResources = false;
							break;
						}

						boolean atRequiredResource = false;// The tag:
															// <requiredResource>
															// might be absent
						if (eventType == XmlPullParser.START_TAG
								&& xpp.getName().equalsIgnoreCase("resource")) {
							atRequiredResource = true;
						}// requiredResource

						while (atRequiredResource) {
							if (eventType == XmlPullParser.END_TAG
									&& xpp.getName().equalsIgnoreCase(
											"resource")) {
								atRequiredResource = false;
								break;
							}

							if (xpp.getAttributeCount() > 0) {
								addRequiredResource(xpp, requiredResources);
							}
							eventType = xpp.next();
						}// atRequiredResource

						eventType = xpp.next();
					}// atRequiredResources

					boolean atRequiredBuildings = false;// The tag:
														// <requiredBuildings>
														// might be absent
					if (eventType == XmlPullParser.START_TAG
							&& xpp.getName().equalsIgnoreCase(
									"requiredBuildings")) {
						atRequiredBuildings = true;
					}
					while (atRequiredBuildings) {
						// Break the loop when the end tag: </building> is
						// reached
						if (eventType == XmlPullParser.END_TAG
								&& xpp.getName().equalsIgnoreCase(
										"requiredBuildings")) {
							atRequiredBuildings = false;
							break;
						}

						boolean atRequiredBuilding = false;// The tag:
															// <requiredBuilding>
															// might be absent
						if (eventType == XmlPullParser.START_TAG
								&& xpp.getName().equalsIgnoreCase("building")) {
							atRequiredBuilding = true;
						}// requiredBuilding

						while (atRequiredBuilding) {
							if (eventType == XmlPullParser.END_TAG
									&& xpp.getName().equalsIgnoreCase(
											"building")) {
								atRequiredBuilding = false;
								break;
							}
							addRequiredBuilding(xpp, requiredBuildings);

							eventType = xpp.next();
						}// atRequiredBuilding

						eventType = xpp.next();
					}// atRequiredBuildings

					eventType = xpp.next();
				}// while atBuilding
				this.buildings.add(new Building(buildingName, buildingInfo,
						buildingAmount, buildingInputPower,
						buildingOutputPower, buildingMonetaryCost,
						buildingRegolithCost, buildingRequiredTurns,
						requiredResources, requiredBuildings,
						buildingXPos, buildingYPos));
			}// if building

			eventType = xpp.next();

		}// end
		return this.buildings;

	}// end of function
}// class
