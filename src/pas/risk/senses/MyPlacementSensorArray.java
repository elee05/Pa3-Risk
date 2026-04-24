package pas.risk.senses;


// SYSTEM IMPORTS
import edu.bu.jmat.Matrix;

import edu.bu.pas.risk.GameView;
import edu.bu.pas.risk.TerritoryOwnerView;
import edu.bu.pas.risk.agent.senses.PlacementSensorArray;
import edu.bu.pas.risk.territory.Continent;
import edu.bu.pas.risk.territory.Territory;


// JAVA PROJECT IMPORTS


/**
 * A suite of sensors to convert a {@link Territory} into a feature vector (must be a row-vector)
 */ 
public class MyPlacementSensorArray
    extends PlacementSensorArray

    
{

    public static final int NUM_FEATURES = 5;

    public MyPlacementSensorArray(final int agentId)
    {
        super(agentId);
    }

    public Matrix getSensorValues(final GameView state,
                                  final int numRemainingArmies,
                                  final Territory territory)
    {
        Matrix m = Matrix.zeros(0, NUM_FEATURES);

        // armies already in territory
        TerritoryOwnerView tov = state.getTerritoryOwners().getById(territory.id());
        double currentArmies = tov.getArmies() / 50.0;
        

        // # of adjacent enemy territories
        int enemyNeighbors = 0;
        for (Territory neighbor : territory.adjacentTerritories()) {
            TerritoryOwnerView neighborTov = state.getTerritoryOwners().getById(neighbor.id());
            if (!neighborTov.isUnclaimed() && neighborTov.getOwner() != this.getAgentId()) {
                enemyNeighbors++;
            }
        }
        double enemyNeighborFeature = enemyNeighbors / 6.0; // max adjacency in risk is ~6

        // # of adjacent friendly territories
        int friendlyNeighbors = 0;
        for (Territory neighbor : territory.adjacentTerritories()) {
            TerritoryOwnerView neighborTov = state.getTerritoryOwners().getById(neighbor.id());
            if (!neighborTov.isUnclaimed() && neighborTov.getOwner() == this.getAgentId()) {
                friendlyNeighbors++;
            }
        }
        double friendlyNeighborFeature = friendlyNeighbors / 6.0;


        // continent completion level
        Continent c = territory.continent();
        int myCount = 0;
        int total = 0;
        for (Territory t : c) {
            total++;
            TerritoryOwnerView tTov = state.getTerritoryOwners().getById(t.id());
            if (!tTov.isUnclaimed() && tTov.getOwner() == this.getAgentId()) {
                myCount++;
            }
        }
        double continentFraction = (total == 0) ? 0.0 : (double) myCount / total;

        m.set(0, 0, currentArmies);
        m.set(0, 1, enemyNeighborFeature);
        m.set(0, 2, friendlyNeighborFeature);
        m.set(0, 3, continentFraction);
        m.set(0, 4, numRemainingArmies);


        return m;
    }

}

