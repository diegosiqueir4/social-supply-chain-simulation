package social_simulation_project;

/**
* This class represents an inventory agent. They are 
* responsible for inventory (all supply chain members).
*
* @author  PS Development Team
* @since   2015-11-30
*/
public class InventoryAgent 
{
	private int inventory_level;

	public InventoryAgent() 
	{
		
	}
	
	/**
	   * This method receives goods.
	   * 
	   * @param ?
	   * @return Nothing.
	   */
	public void receiveShipment(Shipment shipment) 
	{
		this.inventory_level += shipment.getShipmentAmount();
	}
	
	/*
	 * GETTERS
	 */
	public int getInventoryLevel() 
	{
		return this.inventory_level;
	}
	
	/*
	 * SETTERS
	 */
	public void setInventoryLevel(int inventory_level) 
	{
		this.inventory_level = inventory_level;
	}
}