package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import artefacts.Order;
import artefacts.trust.CompetenceDimension;
import artefacts.trust.DimensionType;
import artefacts.trust.KPI;
import artefacts.trust.QualityDimension;
import artefacts.trust.ReliabilityDimension;
import artefacts.trust.SharedValuesDimension;
import artefacts.trust.Trust;
import artefacts.trust.TrustDimension;


/**
* This class represents a trust agent. 
*
* @author  PS Development Team
* @since   2015-11-30
*/
public class TrustAgent 
{
	private ArrayList<DeliveryAgent> delivery_agents;
	
	private Map<DeliveryAgent, Trust> trustStorage = new HashMap<DeliveryAgent, Trust>();
	
	// When do we classify a shipment as overdue?
	private double ShipmentRuntimeOverdueThreshold = 2;
	
	public TrustAgent(ArrayList<DeliveryAgent> delivery_agents)
	{
		this.delivery_agents = delivery_agents;
		for (DeliveryAgent delivery_agent : this.delivery_agents) 
		{
			// Alle TrustDimensions starten mit 90% Wichtigkeit und 50% Initialwert
			
			ReliabilityDimension reliability = new ReliabilityDimension(0.25, 0.5);
			CompetenceDimension competence = new CompetenceDimension(0.25, 0.5);
			QualityDimension quality = new QualityDimension(0.25, 0.5);
			SharedValuesDimension shared_values = new SharedValuesDimension(0.25, 0.5);
			
			Trust trust = new Trust(reliability, competence, quality, shared_values);
			
			trustStorage.put(delivery_agent, trust);
		}
	}

	private void updateTrustDimensionValue(DeliveryAgent delivery_agent, DimensionType type, double value) 
	{
		this.trustStorage.get(delivery_agent).getDimension(type).updateDimension(value);
	}
	
	public void inspectNewArrivals(OrderAgent orderAgent) 
	{
		ArrayList<Order> shipments = orderAgent.getReceivedShipments();

		for (Order shipment : shipments) 
		{
			inspectShipment(orderAgent, shipment);
		}
	}
	
	//Jedes Shipment wird einzeln untersucht, daraufhin wird der Trust-Wert der spezifischen Dimension eines bestimmten orderAgent geändert
	private void inspectShipment(OrderAgent orderAgent, Order shipment) 
	{
		//reliability
		//is the shipment overdue?
		int runtime = (shipment.getReceivedAt() - shipment.getOrderedAt());
		//runtime is at least 2 weeks: ordered at 1, processed at 2, delivered at 3
						
		DimensionType[] dimensions = {DimensionType.RELIABILITY, DimensionType.COMPETENCE, DimensionType.QUALITY, DimensionType.SHARED_VALUES};
		
		Map<TrustDimension, Double> orderFulfillments = new HashMap<TrustDimension, Double>();
		
		Trust trust = trustStorage.get(shipment.getDeliveryAgent());
		
		KPI Kpi = new KPI(shipment);
		
		for (DimensionType dimensionType : dimensions) {
		
			double kpiValue = Kpi.getKPIForDimension(dimensionType);
			
			orderFulfillments.put(trust.getDimension(DimensionType.RELIABILITY), kpiValue);
		
		}
		
				
		// recalculate reliability:
		
		
	}
	
	
	/*
	 * GETTERS
	 */
	public double getTrustValue(DeliveryAgent delivery_agent) 
	{
		//hier muss der trust wert zurueclgegeben werden.
		return this.trustStorage.get(delivery_agent).getUnifiedTrustValue();
	} 
	
	public DeliveryAgent getCheapestSupplier() 
	{
		DeliveryAgent cheapestSupplier = delivery_agents.get(0);
		
		for (int i = 0; i < delivery_agents.size() - 1; i++)
		{
			if (cheapestSupplier.getPrice() > delivery_agents.get(i).getPrice())
			{
				cheapestSupplier = delivery_agents.get(i);
			}
		}
		return cheapestSupplier;
	}
	
	/* 
	 * SETTERS
	 */
}