package spaceappschallenge.moonville.businessmodels;

import java.util.ArrayList;
import java.util.List;

public class Building {
	protected String name;
	protected String info;
	protected int amount;
	protected ArrayList<Resource> inputResources;
	protected ArrayList<Building> inputBuildings;
	protected ArrayList<Building> requiredBuildings;
	protected ArrayList<Resource> outputResources;
	protected int inputPower;
	protected int outputPower;
	// These are computed each time they are needed, so no init necessary.
	protected boolean hasPower;
	protected boolean hasRequiredBuildings;

	public Building(String name, String info, int amount, int inputPower) {
		initBuilding(name, info, amount, inputPower);
	}

	protected void initBuilding(String name, String info, int amount,
			int inputPower) {
		this.name = name;
		this.info = info;
		this.amount = amount;
		this.inputPower = inputPower;
	}
	
	public Building(String name, String info, int amount, int inputPower, int outputPower) {
		initBuilding(name, info, amount, inputPower);
		this.outputPower=outputPower;
	}


	public Building(String name, String info, int amount, int inputPower,
			int outputPower, ArrayList<Building> requiredBuildings) {
		initBuilding(name, info, amount, inputPower, outputPower,
				requiredBuildings);

	}

	protected void initBuilding(String name, String info, int amount,
			int inputPower, int outputPower, 
			ArrayList<Building> requiredBuildings) {
		initBuilding(name, info, amount, inputPower);
		this.outputPower = outputPower;
		this.requiredBuildings = requiredBuildings;
	}

	public Building(String name, String info, int amount, int inputPower,
			int outputPower, ArrayList<Resource> requiredResources,
			ArrayList<Building> requiredBuildings, ArrayList<Resource> outputResources) {
		super();
		initBuilding(name, info, amount, inputPower, outputPower,
				requiredBuildings, outputResources);
	}

	protected void initBuilding(String name, String info, int amount,
			int inputPower, int outputPower, 
			ArrayList<Building> requiredBuildings, ArrayList<Resource> outputResources) {
		initBuilding(name, info, amount, inputPower, outputPower,
				requiredBuildings);
		this.outputResources = outputResources;

	}

	// Increases amount, if input resources/buildings fulfill required
	// resources/buildings
	public void build() {
	}

	public String getName() {
		return name;
	}
	
	public ArrayList<Building>getRequiredBuildings(){
		return this.requiredBuildings;
	}
	
	public int getOutputPower() {
		return outputPower;
	}
	
	public int getInputPower() {
		return inputPower;
	}
	
	public void setHasPower(boolean hasPower) {
		this.hasPower = hasPower;
	}
	
	public boolean getHasPower() {
		return hasPower;
	}
	
	public void setHasRequiredBuildings(boolean hasRequiredBuildings) {
		this.hasRequiredBuildings = hasRequiredBuildings;
	}
	
	public boolean getHasRequiredBuildings() {
		return hasRequiredBuildings;
	}
}
