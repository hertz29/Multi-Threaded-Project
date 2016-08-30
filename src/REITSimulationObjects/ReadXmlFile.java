package REITSimulationObjects;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;

/**
 * 
 * @author us
 * This class contains static functions that help us 
 *
 */

public class ReadXmlFile {
	
	/**
	 * calls all the functions that read all the information needed to initialize the simulation
	 * @param management
	 * @return management
	 * 
	 * this function gets a mangement type class from Driver and returns an initialized management type
	 */
	public static Management initializeManagment(Management management, String[] args){
		readInitialData(management, args[0]);
		readCustomerGroups(management,args[3]);
		readAssets(management, args[2]);
		readAssetContentsRepairDetails(management, args[1]);
		return management;
	}

	/**
	 * This function opens and returns it as a document type for the other functions to read.
	 * 
	 * @param fileName
	 * @return document type
	 * gets a string with a file name, and returns an open document that can be read by the rest of the functions
	 */
	private static Document openFile(String fileName){
		try{
			File fXmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(fXmlFile);
			document.getDocumentElement().normalize();

			return document;

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * This function adds to management all the information needed from "InitialData.XML"
	 * @param management
	 */
	private static void readInitialData(Management management, String fileName){

		Document document =	openFile(fileName);
		readInitialDataTools(document, management);
		readInitialDataMaterial(document, management);
		int numberOfMaintenancePersons = Integer.parseInt(document.getElementsByTagName("NumberOfMaintenancePersons").item(0).getTextContent());
		int totalNumberOfRentalRequests = Integer.parseInt(document.getElementsByTagName("TotalNumberOfRentalRequests").item(0).getTextContent());
		management.initializeMaintenanceGuys(numberOfMaintenancePersons);
		management.initializeNumRentalRequests(totalNumberOfRentalRequests);
		readInitialDataClerk(document, management);

	}

	/**
	 * This function adds to management all the information needed from "CustomerGroups.XML"
	 * @param management
	 */
	private static void readCustomerGroups(Management management, String fileName){
		Document document = openFile(fileName);
		document.getDocumentElement().normalize(); 
		NodeList customerGroupDetailsList = document.getElementsByTagName("CustomerGroupDetails");
		for (int i = 0; i < customerGroupDetailsList.getLength(); i++) { 
			
			Node customerGroupDetailsNode = customerGroupDetailsList.item(i);
			if (customerGroupDetailsNode.getNodeType() == Node.ELEMENT_NODE) {

				Element customerGroupDetails = (Element) customerGroupDetailsNode;
				
				String customerGroupManagerName = customerGroupDetails.getElementsByTagName("GroupManagerName").item(0).getTextContent();
				CustomerGroupDetails tempCustomerGroup = new CustomerGroupDetails(customerGroupManagerName); 

				NodeList customerList = customerGroupDetails.getElementsByTagName("Customer");
				addCustomerGroup(customerList, tempCustomerGroup);

				NodeList requestList = customerGroupDetails.getElementsByTagName("Request");
				addRentalRequest(requestList, tempCustomerGroup, customerList.getLength());
				management.addCustomerGroup(tempCustomerGroup);
			}
		}
	}

	/**
	 * this function adds to management all the information needed from "Assets.XML"
	 * @param management
	 */
	private static void readAssets(Management management, String fileName){
		Document document = openFile(fileName);
		NodeList assetList = document.getElementsByTagName("Asset");
		for (int i = 0; i < assetList.getLength(); i++) { 
			Node tempNode = assetList.item(i);

			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				Element currentAsset = (Element) tempNode;
				Asset tempAsset = createAsset(currentAsset, management);
				NodeList assetContentList = currentAsset.getElementsByTagName("AssetContent");
				addAssetContent(assetContentList, tempAsset);
			}
		}

		management.sortAssets();
	


	}

	/**
	 * this function adds to management all the information needed from "AssetContentRepairDetails.XML"
	 * @param management
	 */
	private static void readAssetContentsRepairDetails(Management management, String fileName) {
		Document document = openFile(fileName);
		document.getDocumentElement().normalize();
		NodeList assetContentList = document.getElementsByTagName("AssetContent"); 
		for (int i = 0; i < assetContentList.getLength(); i++) { // read the assert content
			Node assetContentNode = assetContentList.item(i);

			if (assetContentNode.getNodeType() == Node.ELEMENT_NODE) {
				Element assetContent = (Element) assetContentNode;
				String assetContentName = assetContent.getElementsByTagName("Name").item(0).getTextContent();

				ArrayList<RepairToolInformation> repairToolList = new ArrayList<RepairToolInformation>();
				ArrayList<RepairMaterialInformation> repairMaterialList = new ArrayList<RepairMaterialInformation>();

				NodeList toolsList = assetContent.getElementsByTagName("Tool");

				addToolInformation(toolsList, repairToolList);

				NodeList materialsList = assetContent.getElementsByTagName("Material");	

				addMaterialInformation(materialsList, repairMaterialList);

				management.addItemRepairTool(assetContentName, repairToolList);
				management.addItemRepairMaterial(assetContentName, repairMaterialList);
			}
		}
		management.initializeRepairAssetInformation();
	}

	/**
	 * This function is an help function for readAssets
	 * @param currentAsset
	 * @param management
	 * @return Asset
	 * 
	 * The function gets an element which is an asset node and management type,
	 * it creates all the necessary values that are needed to create an asset from the element
	 * than it creates an Asset that is added to management assets list. 
	 */
	private static Asset createAsset(Element currentAsset, Management management){
		String assetName = currentAsset.getElementsByTagName("Name").item(0).getTextContent();
		String assetType = currentAsset.getElementsByTagName("Type").item(0).getTextContent();
		int assetSize = Integer.parseInt(currentAsset.getElementsByTagName("Size").item(0).getTextContent());
		int location_X = Integer.parseInt(currentAsset.getElementsByTagName("Location").item(0).getAttributes().getNamedItem("x").getTextContent());
		int location_Y = Integer.parseInt(currentAsset.getElementsByTagName("Location").item(0).getAttributes().getNamedItem("y").getTextContent());
		int assetCostPerNight = Integer.parseInt(currentAsset.getElementsByTagName("CostPerNight").item(0).getTextContent());
		Asset tempAsset = new Asset(assetName, assetType, assetSize, location_X, location_Y, assetCostPerNight);
		management.addAsset(assetName,assetSize,  assetType, tempAsset);
		return tempAsset;
	}

	/**
	 * A function that adds tool information that is needed to repair asset contents
	 * @param toolsList
	 * @param repairToolVec
	 *
	 * This function is called by "readAssetContentRepairDetails" function.
	 * The functions gets a tools list which is a NodeList that helps reading from the xml file,
	 * it retrieves all the tools that are needed to fix an asset content, and adds it to 
	 * repairToolVec which is the the collection of repair tool information for a certain asset content. 
	 *
	 */
	private static void addToolInformation(NodeList toolsList, ArrayList<RepairToolInformation> repairToolList){
		for (int j = 0; j < toolsList.getLength(); j++) { //read the tools
			Node toolNode = toolsList.item(j);
			if (toolNode.getNodeType() == Node.ELEMENT_NODE) {
				Element tools = (Element) toolNode;
				String toolName = tools.getElementsByTagName("Name").item(0).getTextContent();
				int toolQuantity = Integer.parseInt(tools.getElementsByTagName("Quantity").item(0).getTextContent());
				RepairToolInformation tempToolInfo = new RepairToolInformation(toolName, toolQuantity);
				repairToolList.add(tempToolInfo);
			}	
		}

	}

	/**
	 * A function that adds tool information that is needed to repair asset contents
	 * @param materialList
	 * @param repairMaterialVec
	 *
	 * This function is called by "readAssetContentRepairDetails" function.
	 * The functions gets a material list which is a NodeList that helps reading from the xml file,
	 * it retrieves all the material information that are needed to fix an asset content, and adds it to 
	 * materialToolVec which is the the collection of repair material information for a certain asset content. 
	 *
	 */
	private static void addMaterialInformation(NodeList materialsList, ArrayList<RepairMaterialInformation> materialInformationList){
		for (int j = 0; j < materialsList.getLength(); j++) { //read the tools

			Node materialNode = materialsList.item(j);
			if (materialNode.getNodeType() == Node.ELEMENT_NODE) {
				Element materials= (Element) materialNode;
				String materialName = materials.getElementsByTagName("Name").item(0).getTextContent();
				int materialQuantity = Integer.parseInt(materials.getElementsByTagName("Quantity").item(0).getTextContent());
				RepairMaterialInformation tempMaterialInfo = new RepairMaterialInformation(materialName, materialQuantity);
				materialInformationList.add(tempMaterialInfo);
			}	
		}
	}

	/**
	 * adds asset content into a certain asset
	 * @param assetContentList
	 * @param tempAsset
	 * 
	 * gets assetContentList which is a NodeList of asset contents for a certain asset
	 * we create a asset content that we retrieve from the xml file and add it to tempAsset, which is 
	 * the asset that contains all the asset content from assetContentList
	 */
	private static void addAssetContent(NodeList assetContentList, Asset tempAsset){	
		for (int j = 0; j <assetContentList.getLength(); j++) {
			Node assetContentNode = assetContentList.item(j);

			if (assetContentNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement2 = (Element) assetContentNode;
				String assetContentName = eElement2.getElementsByTagName("Name").item(0).getTextContent();
				double repairMultiplier = Double.parseDouble(eElement2.getElementsByTagName("RepairMultiplier").item(0).getTextContent());
				AssetContent tempAssetContent = new AssetContent(assetContentName, repairMultiplier);
				tempAsset.addAssetContent(tempAssetContent);
			}

		}

	}

	/**
	 * adds a repair tool into the warehouse
	 * @param document
	 * @param management
	 * gets a document that contian information about the content of the ware house
	 * gets the managment that contain the warehouse
	 * we create a reapir tool that we retrive from the xml files and add it to the warehouse
	 */
	private static void readInitialDataTools(Document document, Management management){ 
		NodeList ToolList = document.getElementsByTagName("Tool");

		for (int i = 0; i < ToolList.getLength(); i++) {

			Node toolNode = ToolList.item(i);

			if (toolNode.getNodeType() == Node.ELEMENT_NODE) {
				Element tool = (Element) toolNode;
				String toolName = tool.getElementsByTagName("Name").item(0).getTextContent();
				int toolQuantity = Integer.parseInt(tool.getElementsByTagName("Quantity").item(0).getTextContent());

				RepairTool tempRepairTool = new RepairTool(toolName, toolQuantity);
				management.addToolToWarehouse(toolName, tempRepairTool);
			}
		}
	}

	private static void readInitialDataClerk(Document document, Management management){
		NodeList clerkList = document.getElementsByTagName("Clerk");
		int clerkCounter = 0;
		for (int i = 0; i < clerkList.getLength(); i++) {
			Node clerkNode = clerkList.item(i); 

			if (clerkNode.getNodeType() == Node.ELEMENT_NODE) {
				Element clerk = (Element) clerkNode;
				String clerkName = clerk.getElementsByTagName("Name").item(0).getTextContent();
				int location_X = Integer.parseInt(clerk.getElementsByTagName("Location").item(0).getAttributes().getNamedItem("x").getTextContent());
				int location_Y = Integer.parseInt(clerk.getElementsByTagName("Location").item(0).getAttributes().getNamedItem("y").getTextContent());
				ClerkDetails tempClerkDetails = new ClerkDetails(location_X, location_Y, clerkName);
				management.addClerk(tempClerkDetails);
				clerkCounter++;
			}		
		}
		management.addNumberOfClerks(clerkCounter);
	}

	private static void readInitialDataMaterial(Document document, Management management){
		NodeList materialsList = document.getElementsByTagName("Material");
		for (int i = 0; i < materialsList.getLength(); i++) {
			Node materialNode = materialsList.item(i);

			if (materialNode.getNodeType() == Node.ELEMENT_NODE) {
				Element material = (Element) materialNode;
				String materialName = material.getElementsByTagName("Name").item(0).getTextContent();
				int materialQuantity = Integer.parseInt(material.getElementsByTagName("Quantity").item(0).getTextContent());
				RepairMaterial tempRepairMaterial = new RepairMaterial(materialName, materialQuantity);
				management.addMaterialToWarehouse(materialName, tempRepairMaterial);
			}
		}
	}

	private static Customer createCustomer(Element customer){
		String customerName = customer.getElementsByTagName("Name").item(0).getTextContent();
		String customerVandalisemType = customer.getElementsByTagName("Vandalism").item(0).getTextContent();
		int customerMinimumDamage = Integer.parseInt(customer.getElementsByTagName("MinimumDamage").item(0).getTextContent());
		int customerMaximumDamage = Integer.parseInt(customer.getElementsByTagName("MaximumDamage").item(0).getTextContent());
		return new Customer(customerName, customerVandalisemType, customerMinimumDamage, customerMaximumDamage);
	}

	private static RentalRequest createRentalRequest(Element request, int numOfCustomersInTheRequest){
		String requestId = request.getAttribute("id");
		String assetType = request.getElementsByTagName("Type").item(0).getTextContent();
		int assetSize = Integer.parseInt(request.getElementsByTagName("Size").item(0).getTextContent());
		int durationTime = Integer.parseInt(request.getElementsByTagName("Duration").item(0).getTextContent());
		RentalRequest tempRequest = new RentalRequest(requestId, assetType, assetSize, durationTime, numOfCustomersInTheRequest);
		return tempRequest;
	}

	private static void addCustomerGroup(NodeList customerList, CustomerGroupDetails tempCustomerGroup){
		for (int j = 0; j < customerList.getLength(); j++) { 
			Node customerNode = customerList.item(j);
			if (customerNode.getNodeType() == Node.ELEMENT_NODE) {
				Element customer = (Element) customerNode;
				tempCustomerGroup.addCustomer(createCustomer(customer));
			}	
		}
	}

	private static void addRentalRequest(NodeList requestList, CustomerGroupDetails tempCustomerGroup, int numOfCustomersInRequest){
		for (int j = 0; j < requestList.getLength(); j++) { 
			Node requestNode = requestList.item(j);			

			if (requestNode.getNodeType() == Node.ELEMENT_NODE) {
				Element request= (Element) requestNode;
				tempCustomerGroup.addRentalRequest(createRentalRequest(request, numOfCustomersInRequest));
			}	
		}
	}

}
