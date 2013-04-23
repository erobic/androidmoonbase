package spaceappschallenge.moonville.businessmodels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import spaceappschallenge.moonville.factories.Buildings;
import spaceappschallenge.moonville.xml_parsers.BuildingDefinition;

/**
 * Contains all buildings per level as of 
 * https://www.facebook.com/photo.php?fbid=10151316698507504
 * 
 * As opposed to Buildings, this handles buildings that actually are placed in the world.
 * 
 * @author Felix
 *
 */
public class BuildingTree implements Serializable {

	Building building;
	
	List<BuildingTree> childs = new ArrayList<BuildingTree>();
	
	/**
	 * Creates an empty tree.
	 */
	public BuildingTree() {		
	}
	
	/**
	 * Creates a tree using the parameter as value.
	 */
	private BuildingTree(Building building) {
		this.building = building;
	}
	
	/**
	 * Tests if all required buildings exist and the building has not 
	 * been built yet.
	 * 
	 * @return True if the building can be inserted (required buildings exist).
	 */
	public boolean canBuild(Building b) {
		return findInsertionNode(b) != null;
	}
	
	/**
	 * Searches the tree for the correct node to insert a building to, 
	 * or null if none is found (the building can't be built).
	 * 
	 * @warning If the building attribute of the returned node is not null,
	 * 			you have to insert a new child into the returned node.
	 */
	private BuildingTree findInsertionNode(Building b) {
		if (b == null) 
			return null;
		// Moon Base has no requirements, so take first empty node.
		if (building == null && b.getName().equals("Moon Base"))
			return this;
		// Required building for anything beneath this node not available.
		if (building == null)
			return null;
		// Building already exists.
		if (building.getName().equals(b.getName()))
			return null;
		// Check if we can insert into childs.
		for (BuildingTree bt : childs) {
			if (bt.findInsertionNode(b) != null) {
				return this;
			}
		}
		// If b requires building in this node, insert into new child 
		// (creating the child is handled by add()).
		ArrayList<BuildingDefinition> required = Buildings.getInstance().
				getBuilding(b.getName()).getRequiredBuildings();
		for (BuildingDefinition r : required) {
			if (r.getName().equals(building.getName())) {
				return this;
			}
		}
		return null;
	}
	
	/**
	 * Insert a building into the tree. 
	 *
	 * @param b The building to insert.
	 * @return True if the building was inserted successfully.
	 */
	public boolean add(Building b) {
		BuildingTree bt = findInsertionNode(b);
		if (bt != null) {
			if (bt.building != null) {
				 bt.childs.add(new BuildingTree(b));
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the total power produced by all buildings, then does a 
	 * breadth-first traversal over the tree, powering buildings 
	 * near the root first, setting Building.hasPower accordingly.
	 * 
	 * This implementation does not consider resources, or required buildings, 
	 * which means a building may be powered while it does actually work.
	 */
	public void checkPower() {
		int power = computeTotalPowerOutput();
		List<BuildingTree> q = new ArrayList<BuildingTree>();
		q.add(this);
		while (!q.isEmpty()) {
			BuildingTree v = q.get(0);
			q.remove(v);
			if (v.building == null)
				continue;
			int inputPower = Buildings.getInstance().getBuilding(
					v.building.getName()).getInputPower();
			if (inputPower <= power) {
				power -= inputPower;
				v.building.setHasPower(true);
			}
			else
				v.building.setHasPower(false);
			for (BuildingTree c : v.childs)
				q.add(c);
		}
	}

	/**
	 * Recursively computes the total power generated by all buildings.
	 */
	private int computeTotalPowerOutput() {
		int power = 0;
		if (building != null)
			power += Buildings.getInstance().getBuilding(
							building.getName()).getOutputPower();
		for (BuildingTree c : childs)
			power += c.computeTotalPowerOutput();
		return power;		
	}
	
	/**
	 * Interface function for checkrequiredBuildings(boolean)
	 */
	public void checkRequiredBuildings() {
		// Moon Base has no required buildings.
		checkRequiredBuildings(false);
	}
	
	/**
	 * Checks if each building has its required buildings. This is true if 
	 * no higher node in the tree is empty.
	 * 
	 * @param isParentEmpty True if the parent node is empty. Means required 
	 * 						buildings are not available.
	 */
	private void checkRequiredBuildings(boolean isParentEmpty) {
		if (!isParentEmpty) {
			if (building != null)
				building.setHasRequiredBuildings(true);
			for (BuildingTree c : childs)
				c.checkRequiredBuildings(false);
		}
		else {
			if (building != null)
				building.setHasRequiredBuildings(false);
			for (BuildingTree c : childs)
				c.checkRequiredBuildings(true);
		}
	}
	
	/**
	 * Recursively checks how much resources are available in total, 
	 * considering resources used by buildings and non-working buildings.
	 * 
	 * @param resourceAvailable Previous amount of resources.
	 * @return List of resources that are available from buildings.
	 */
	public List<Resource> checkResources(List<Resource> resourceAvailable) {
		if (building != null && building.getHasPower() && 
				building.getHasRequiredResources()) {
			// Save values in case we need to reset available resources (if a 
			// building has some but not all of its resources available).
			List<Resource> oldAmount = new ArrayList<Resource>();
			for (Resource r : resourceAvailable)
				oldAmount.add(new Resource(r));

			BuildingDefinition bd = Buildings.getInstance().getBuilding(
					building.getName());
			for (Resource resourceNeed : bd.getRequiredResources()) {
				if (subtractBuildingResources(resourceAvailable, resourceNeed)) {
					building.setHasRequiredResources(true);
					resourceAvailable = Resource.merge(resourceAvailable, 
							bd.getOutputResources());				
				}
				else {
					// Reset resources so we don't remove part of the resources 
					// for a building that can't work.
					resourceAvailable = oldAmount;
					building.setHasRequiredResources(false);
				}
			}
		}
		for (BuildingTree c : childs) 
			Resource.merge(resourceAvailable, c.checkResources(resourceAvailable));
		return resourceAvailable;
	}

	/**
	 * If enough resources are available, those are removed from resource pool.
	 * 
	 * @return True if enough resources are available of each type.
	 */
	private boolean subtractBuildingResources(List<Resource> resourceAvailable,
			Resource resourceNeed) {
		for (Resource resourceHave : resourceAvailable) {
			if (resourceNeed.getName().equals(resourceHave.getName())) {
				if (resourceHave.getAmount() >= resourceNeed.getAmount())
					resourceHave.setAmount(resourceHave.getAmount() - 
							resourceNeed.getAmount());
				else
					return false;
			}
		}
		return true;
	}
}
