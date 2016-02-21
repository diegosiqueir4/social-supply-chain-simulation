package actors;

import java.util.ArrayList;

import agents.DeliveryAgent;
import agents.OrderAgent;
import agents.ProcurementAgent;
import agents.TrustAgent;
import artefacts.Order;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import social_simulation_project.BWeffectMeasurer;

/**
* This class represents the customer. We have one
* customer in our supply chain. The customer is 
* the only supply chain member, which actually
* consumes goods. He cannot deliver.
*
* @author  PS Development Team
* @since   2015-11-30
*/
public class Customer extends Buy 
{
	// what the customer consumes every tick
	private int consumption;
	
	// what the customer needs for the next tick (forecasted)
	private int nextDemand;
	
	// the desired inventory
	int desiredInventoryLevel;
	// what the customer orders at the end, based 
	// on next_demand and current_inventory_level
	private int orderQuantity;
	
	/**
	   * This constructor gives the customer its own inventory
	   * agent and order agent.
	   * 
	   */
	public Customer(ArrayList<Sale> sailorList, int incomingInventoryLevel, int outgoingInventoryLevel) 
	{
		super(sailorList, incomingInventoryLevel, outgoingInventoryLevel);
	}
	
	/**
	   * The run method of for the simulation. It represents
	   * the customer's process in every tick.
	   * 1. Receive goods from previous tick.
	   * 2. Update trust based upon delivery experience.
	   * 3. Consume goods.
	   * 4. Calculate demand for the next tick.
	   * 5. Order to match demand for the next tick based upon forecasted demand.
	   * 
	   * @return Nothing.
	   */
	//method for every run, start: start tick, priority: which priority it has in the simulation(higher --> better priority)
	@ScheduledMethod(start = 1, interval = 1, priority = 5)
	public void run() 
	{	
		if ((int)RepastEssentials.GetTickCount() == 1) {
			RunEnvironment.getInstance().setScheduleTickDelay(30);
		}
		// set the inventory agents desired level
		inventoryAgent.desiredLevel(lying, desired());
		// 1. processShipments()
		this.receiveShipments();
		orderAgent.clearReceivedShipments();
		// 2. consume()
		this.consume();
		// 3.send order that he made the last tick
		orderAgent.orderIt();
		// 4. order()
		this.order();
		// 5. say those suppliers which I trust, that I will not order at them
		orderAgent.trustWhereIOrder();
	}
	
	private int desired() 
	{
		nextDemand = this.forecastAgent.customerDemand();
		desiredInventoryLevel = nextDemand * 15 / 10;
		return desiredInventoryLevel;
	}

	/**
	   * This method consumes goods and refreshes the
	   * inventory level based upon the actual received 
	   * amount of goods.
	   * The value, that is consumed CAN be different than
	   * the forecasted demand of the previous tick. 
	   * 
	   * @return Nothing.
	   */
	public void consume()
	{
		// consumes every tick amount of 10
		consumption = 10;
		currentIncomingInventoryLevel = inventoryAgent.getIncomingInventoryLevel();
		
		if (consumption > currentIncomingInventoryLevel) {
			// TODO strafkosten/reaktion
			// Inventory less then asked for
			inventoryAgent.setIncomingInventoryLevel(0);
		} else {
			inventoryAgent.setIncomingInventoryLevel(currentIncomingInventoryLevel - consumption);
		}
	}
	
	/**
	   * This method orders goods at the customer's
	   * supplier.
	   * 
	   * @return Nothing.
	   */
	public void order() 
	{
		// 1. need in the next tick
		// 2. whats about my inventory
		// 3. order difference
		
		// 1.
		nextDemand = this.forecastAgent.customerDemand();
		
		// 2.
		currentIncomingInventoryLevel = this.inventoryAgent.getIncomingInventoryLevel();
		
	
		//Update Trust missing?
		
		// 3.
		orderQuantity = nextDemand - currentIncomingInventoryLevel;
		
		// If the inventory level is sufficient for the next demand,
		// do not order
		if (orderQuantity <= 0) {
			// a order with quantity null has to be made for the process in the orderAgent
			// (realize the order of the last tick
			orderQuantity = 0;
			orderAgent.order(this.trustAgent, null);
		} else {
			// System.out.println("[Customer] order_quantity is  " + order_quantity);
			Order order = new Order(orderQuantity, this.orderAgent);
			// Choose retailer
			orderAgent.order(this.trustAgent, order);
			// if he is lying he will order the same at a second supplier
			if (lying) {
				Order order2 = new Order(orderQuantity, this.orderAgent);
				orderAgent.secondOrder(this.trustAgent, order2);
			}
		}
	}
	
	
	/**
	   * This method receives goods at the beginning of each tick
	   * 
	   * @return Nothing.
	   */
	public void receiveShipments() 
	{
		this.orderAgent.receiveShipments(this.inventoryAgent);
	}

	/*
	 * GETTERS
	 */
	public int getNextDemand() 
	{
		return this.nextDemand;
	}
	
	public int getNextOrderQuantity() 
	{
		return this.orderQuantity;
	}
	
	public OrderAgent getOrderAgent() 
	{
		return this.orderAgent;
	}
	
	public TrustAgent getTrustAgent() 
	{
		return this.trustAgent;
	}	 
}