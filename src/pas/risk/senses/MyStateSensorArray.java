package pas.risk.senses;


// SYSTEM IMPORTS
import edu.bu.jmat.Matrix;

import edu.bu.pas.risk.GameView;
import edu.bu.pas.risk.TerritoryOwnerView;
import edu.bu.pas.risk.agent.IAgent;
import edu.bu.pas.risk.agent.senses.StateSensorArray;
import edu.bu.pas.risk.territory.Continent;
import edu.bu.pas.risk.territory.Territory;
import edu.bu.pas.risk.TerritoryOwnerView;
import edu.bu.pas.risk.util.Registry;

import java.util.HashMap;
import java.util.List;


// JAVA PROJECT IMPORTS


/**
 * A suite of sensors to convert a {@link GameView} into a feature vector (must be a row-vector)
 */ 
public class MyStateSensorArray
    extends StateSensorArray
{
    public static final int NUM_FEATURES = 25;

    public MyStateSensorArray(final int agentId)
    {
        super(agentId);
    }

    public Matrix getSensorValues(final GameView state)
    {

        System.out.println();
        System.out.println();
        System.out.println("getSensorValues called!");
        HashMap<Integer,Integer> playerTerritories = new HashMap<>();
        HashMap<Integer,Integer> playerArmyCount = new HashMap<>();

        System.out.println("Num territories: " + state.getBoard().territories().size());
        Registry<TerritoryOwnerView> owners = state.getTerritoryOwners();
        for (TerritoryOwnerView tov : owners) {
            // System.out.println(tov.getTerritory().name() + " owned by " + tov.getOwner() + " with " + tov.getArmies() + " armies");
            playerTerritories.put(tov.getOwner(), playerTerritories.getOrDefault(tov.getOwner(), 0) + 1);
            playerArmyCount.put(tov.getOwner(),playerArmyCount.getOrDefault(tov.getOwner(), 0) + tov.getArmies());
        }   
        System.out.println();
        System.out.println();

        playerArmyCount.forEach((key,value) -> {
            System.out.println("player " + key + " has " + value + " armies");
        });

        // System.out.println("Num continents: " + state.getBoard().continents().size());
        // Registry<Continent> conts = state.getBoard().continents();
        // for (Continent c : conts) {
        //     System.out.println(c.name());
        // }   


        playerTerritories.forEach((key, value) -> {
            System.out.println("Player: " + key + ", has : " + value + " territories");
        });

        System.out.println();
        System.out.println();


        // ? num territories owned by each player
        // ? num continents owned 

        Matrix stateMatrix = Matrix.zeros(1, NUM_FEATURES);
        playerTerritories.forEach((key, value) -> {
            // System.out.println("setting territories for player " 
            //     + key
            // );
            stateMatrix.set(0, key + 1, value);
        });

        for (int i = 0; i < state.getNumAgents(); i++) {
            List<Continent> contList = state.getContinentsOwnedBy(i);
            for (Continent c : contList) {
                System.out.println("player " + i + " owns " + c.name());
            }
            System.out.println("player " + i + " owns: " + state.getContinentsOwnedBy(i).size() + " continents");
            stateMatrix.set(0, 7 +  i, state.getContinentsOwnedBy(i).size());
            stateMatrix.set(0, 13 + i,playerArmyCount.get(i));
        }



        



        
        // todo army budget
        // todo armies per turn 
        // todo exposed territories

        // System.out.println("continents");
        // for (Continent c : state.getBoard().continents()) {
        //     System.out.println("continent: " + c.name());
        //     for (Territory t : c.territories()) {
        //         System.out.println(t.name() + "owned by player: " + );
        //     }
        //     System.out.println();
        // }
        // System.out.println();

        // Registry<TerritoryOwnerView> tViewReg = state.getTerritoryOwners();
        // System.out.println("tViewReg: ");
        // for (TerritoryOwnerView tView : tViewReg) {
        //     System.out.println(tView.getOwner());
        //     System.out.println(tView.name());
        // }

        // System.out.println("examining continents owned by player 0");

        
       



        // army budget  -> getArmyBudgets() The budgets available to each agent, indexed by IAgent.agentId().
        // total armies we own across all territories
        // total armies each opponent has (one feature per opponent, or just the max/sum)
        // Territory count per player — how many territories we own vs. enemy
        // Continent control — for each continent, who owns it (you, enemy, contested)
        // getContinentsOwnedBy(int agentIdx)  Returns the list of all continents owned by a specific agent.
        // Frontier exposure: number of our territories that are adjacent to enemy territories 
        // Army concentration: our armies / our territories (are we spread thin or fortified?)
        // Card count: getNumPreviousRedemptions() and how many cards you hold
        // Turn number — getNumTurns() normalized
        // [x,x,....,x]

        // state.getBoard().territories() → all Territory objects
        //   each .getOwner() = agentId, .getArmies() = int, .getTerritory() = Territory
        // state.getNumAgents() → int
        // state.getBoard().continents() → Collection<Continent>
        //   each continent.territories() → iterable of Territory
        System.out.println("stateMatrix: " + stateMatrix.toString());
        System.out.println();
        System.out.println("agentInventories");
        List<IAgent> iAList = state.getAgents();
        for (IAgent ia : iAList) {
            System.out.println("agent " + ia.agentId());
        }

        return stateMatrix;  // row vector
    }

    public static void main(String[] args) {
        MyStateSensorArray sensor = new MyStateSensorArray(0);
        System.out.println("sensor class message");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        // you can't easily construct a GameView manually,
        // so the real test has to go through SingleGameEval or SequentialTrain
    }

}

