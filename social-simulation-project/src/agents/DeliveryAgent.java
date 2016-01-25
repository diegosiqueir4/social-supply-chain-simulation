package agents;

import java.util.ArrayList;

import social_simulation_project.OrderObserver;
import actors.SupplyChainMember;
import artefacts.Order;

/**
* This class represents a delivery agent. They are 
* responsible for delivery (only retailers, wholesalers,
* distributors and the manufacturer). 
* Delivery agents communicate with
* - the user's inventory agent
* - the callee's order agent
*
* @author  PS Development Team
* @since   2015-11-30
*/
public class DeliveryAgent 
{
	//price for the goods
	private int price;
	private int current_outgoing_inventory_level;
	private ArrayList<Order> receivedOrders; // list for all received orders
	private ArrayList<Order> everReceivedOrders;//all orders ever received
	private ArrayList<Order> openOrders;//list to transfer open orders
	private int shortage = 0;//gives the shortage of the last tick, will be used for the forecast
	private SupplyChainMember parent;//to which SupplyChainMember it belongs
	
	public DeliveryAgent(int price, SupplyChainMember parent) 
	{
		this.receivedOrders = new ArrayList<Order>();
		this.everReceivedOrders = new ArrayList<Order>();
		this.openOrders = new ArrayList<Order>();
		this.price = price;
		this.parent = parent;
	}
	
	/**
	   * This method receives orders.
	   * 
	   * @param order Order of the callee with details about amount etc.
	   * @return Nothing.
	   */
	public void receiveOrder(Order order) 
	{
		order.setDeliveryAgent(this);
		receivedOrders.add(order);
		everReceivedOrders.add(order);
	}
	
	/**
	   * This method receives goods at the beginning of each tick
	   * 
	   * @param inventoryAgent inventory agent used to get information 
	   * 	    about the inventory level of the current deliverer
	   */
	public void deliver(InventoryAgent inventoryAgent)
	{
		current_outgoing_inventory_level = inventoryAgent.getOutgoingInventoryLevel();
		shortage=0;
		for (Order order : receivedOrders) 
		{
			//if the needed rest quantity of the order is higher then the inventory and the need is bigger then 8
			if (order.getUnfullfilledQuantity() > current_outgoing_inventory_level && current_outgoing_inventory_level > 8) 
			{
				//if the needed rest quantity of the order is higher then the inventory
				//part of the order will be delivered
				order.partDelivery(current_outgoing_inventory_level);
				openOrders.add(order);
				shortage=+order.getUnfullfilledQuantity();
				inventoryAgent.reduceOutgoingInventoryLevel(current_outgoing_inventory_level);
				order.getOrderAgent().receiveShipment(order,this);
				current_outgoing_inventory_level=0;
			}
			//if <=8 no delivery will be made
			else if(order.getUnfullfilledQuantity() > current_outgoing_inventory_level){
				shortage=+order.getUnfullfilledQuantity();
				openOrders.add(order);
			}
			//if the order can be completly fullfilled, it will be
			else 
			{
				int buffer=order.getUnfullfilledQuantity();
				order.setProcessed(true);
				order.partDelivery(buffer);
				//sub the amount because the order is not open anymore
				order.getOrderAgent().receiveShipment(order,this);
				//System.out.println(order.getQuantity());
				inventoryAgent.reduceOutgoingInventoryLevel(buffer);
				current_outgoing_inventory_level = inventoryAgent.getOutgoingInventoryLevel();
			}
		}
		//the received list must be deleted completly and filled with the openOrder list
		//otherwise RePast has a problem
		receivedOrders.clear();
		receivedOrders.addAll(openOrders);
		openOrders.clear();
	}


	/*
	 * GETTERS
	 */
	public int getPrice() 
	{	
		return this.price;
	}

	public int getShortage() 
	{
		return shortage;
	}
	
	public ArrayList<Order> getAllOrders()
	{	
		return this.everReceivedOrders;
	
	}
	
	public double getExpectedDeliveryTime() {
		//TODO implement
		return 2;
	}
	
	public SupplyChainMember getParent() {
		return this.parent;
	}
	
	/*
	 * SETTERS
	 */
}