package pas.risk.senses;


// SYSTEM IMPORTS
import edu.bu.jmat.Matrix;

import edu.bu.pas.risk.GameView;
import edu.bu.pas.risk.TerritoryOwnerView;
import edu.bu.pas.risk.agent.IAgent;
import edu.bu.pas.risk.agent.senses.StateSensorArray;
import edu.bu.pas.risk.territory.Continent;
import edu.bu.pas.risk.territory.Territory;
import edu.bu.pas.risk.territory.TerritoryCard;
import edu.bu.pas.risk.TerritoryOwnerView;
import edu.bu.pas.risk.util.Registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;


// JAVA PROJECT IMPORTS


/**
 * A suite of sensors to convert a {@link GameView} into a feature vector (must be a row-vector)
 */ 
public class MyStateSensorArray
    extends StateSensorArray
{
    public static final int NUM_FEATURES = 17;

    public MyStateSensorArray(final int agentId)
    {
        super(agentId);
    }

    public Matrix getSensorValues(final GameView state)
    {

        System.out.println();
        System.out.println();
        System.out.println("getSTATESensorValues called!");
        HashMap<Integer,Integer> playerTerritoryCounts = new HashMap<>();
        HashMap<Integer,Set<Territory>> playerTerritorySets = new HashMap<>();
        HashMap<Integer,Integer> playerArmyCount = new HashMap<>();

        // looping through territory owner views to get territory and army count
        Registry<TerritoryOwnerView> owners = state.getTerritoryOwners();
        for (TerritoryOwnerView tov : owners) {
            playerTerritoryCounts.put(tov.getOwner(), playerTerritoryCounts.getOrDefault(tov.getOwner(), 0) + 1);

            Set<Territory> territories = playerTerritorySets.getOrDefault(tov.getOwner(), new HashSet<>());
            territories.add(tov.getTerritory());
            playerTerritorySets.put(tov.getOwner(), territories);

            playerArmyCount.put(tov.getOwner(),playerArmyCount.getOrDefault(tov.getOwner(), 0) + tov.getArmies());
        }   
        System.out.println();
        System.out.println();



        // **DEBUG** showing who owns what continent, territory, num armies
        // for (int i = 0; i < state.getNumAgents(); i++) {
        //     List<Continent> contList = state.getContinentsOwnedBy(i);
        //     for (Continent c : contList) {
        //         System.out.println("player " + i + " owns " + c.name());
        //     }
        //     System.out.println("player " + i + " owns: " + state.getContinentsOwnedBy(i).size() + " continents");
        // }

        // playerTerritoryCounts.forEach((key, value) -> {
        //     System.out.println("Player: " + key + ", has : " + value + " territories");
        // });
        // playerArmyCount.forEach((key,value) -> {
        //     System.out.println("player " + key + " has " + value + " armies");
        // });

        // CREATING SENSOR MATRIX

        Matrix stateMatrix = Matrix.zeros(1, NUM_FEATURES);


        // stateMatrix.set(0,0,state.getContinentsOwnedBy(this.getAgentId()).size());
        // stateMatrix.set(0,1,playerTerritoryCounts.get(this.getAgentId()));
        // stateMatrix.set(0,2,playerArmyCount.get(this.getAgentId()));
        stateMatrix.set(0, 0, state.getContinentsOwnedBy(this.getAgentId()).size());
        stateMatrix.set(0, 1, playerTerritoryCounts.getOrDefault(this.getAgentId(), 0));
        stateMatrix.set(0, 2, playerArmyCount.getOrDefault(this.getAgentId(), 0));
                

        
        
        // setting the enemy army data
        playerArmyCount.remove(this.getAgentId());
        Integer maxArmyEnemy = Collections.max(playerArmyCount.entrySet(), Map.Entry.comparingByValue()).getKey();
        // System.out.println("biggest foe: " + maxArmyEnemy + " has army count: " + playerArmyCount.get(maxArmyEnemy));

        stateMatrix.set(0,3,playerArmyCount.get(maxArmyEnemy));
        stateMatrix.set(0,4,maxArmyEnemy);
        

       
        // ! player continent count
        // ! player territory count
        // ! player army count
        // ! most danger enemy army count
        // ! most dangeorus enemy
        // ! [6] continent completion
        // ! total number of adjacent enemies(by how many of ours they touch)
        // ! total number of adjacent enemies(removing double counting)
        // ! exposed territories


        // continent completion process

        HashMap<Integer, HashMap<Continent,Integer>> playerContinentCompletionMap = new HashMap<>();

        for (int i = 0; i < state.getNumAgents(); i++) {
            HashMap<Continent,Integer> counts = new HashMap<>();
            for (Continent c : state.getBoard().continents()) {
                counts.put(c, 0);
            }
            
            playerContinentCompletionMap.put(i,counts );
        }

        // System.out.println("completion map completed");

       
        
        // Registry<TerritoryOwnerView> tView = state.getTerritoryOwners();
        // for (TerritoryOwnerView sTView : tView) {
        //     // System.out.println("territory " + sTView.getTerritory().name()+  "owned by player " + sTView.getOwner());
        //     Integer sTViewPlayer = sTView.getOwner();
        //     Continent sTViewCont = sTView.getTerritory().continent();
        //     playerContinentCompletionMap.get(sTViewPlayer).put(sTViewCont, playerContinentCompletionMap.get(sTViewPlayer).getOrDefault(sTViewCont,0) + 1);
        // }

        Registry<TerritoryOwnerView> tView = state.getTerritoryOwners();
        for (TerritoryOwnerView sTView : tView) {
            if (sTView.isUnclaimed()) continue; // skip territories with no owner
            
            Integer sTViewPlayer = sTView.getOwner();
            Continent sTViewCont = sTView.getTerritory().continent();
            playerContinentCompletionMap.get(sTViewPlayer).put(sTViewCont, 
                playerContinentCompletionMap.get(sTViewPlayer).getOrDefault(sTViewCont, 0) + 1);
        }
        //  System.out.println("playerContinentCompletionMap: ");
        System.out.println();



        // playerContinentCompletionMap.forEach((key,value) -> {
        //     System.out.println("player " + key );

            
        //     value.forEach((metaKey, metaValue) -> {
        //         System.out.println("continent completion for " + metaKey.name() + " is: " + metaValue);

                
        //     });
        // });
        
        HashMap<Continent, Integer> continentLeaderMap = new HashMap<>(); // continent -> playerId with most territories

        for (Continent c : state.getBoard().continents()) {
            int leadingPlayer = -1;
            int maxCount = 0;

            for (int i = 0; i < state.getNumAgents(); i++) {
                int count = playerContinentCompletionMap.get(i).get(c);
                if (count > maxCount) {
                    maxCount = count;
                    leadingPlayer = i;
                }
            }

            continentLeaderMap.put(c, leadingPlayer); // -1 means tied at 0
        }
        // adding continent completion to sensor vector
        // System.out.println("continents");
        // for (int i = 0; i < state.getBoard().continents().size(); i++) {
        //     // System.out.println("continent: " + state.getBoard().continents().getById(i).name() );
        //     int leader = continentLeaderMap.get(state.getBoard().continents().getById(i));
        //     // System.out.println("leader is player " + leader );
        //     Integer contCompletion = playerContinentCompletionMap.get(leader).get(state.getBoard().continents().getById(i));
        //     // System.out.println(state.getBoard().continents().getById(i).name() +" completion: " + contCompletion);
        //     Integer contSize = state.getBoard().continents().getById(i).territories().size();
        //     // System.out.println("cont size: " + contSize);
        //     stateMatrix.set(0, 5 + i, (float) contCompletion/ (float) contSize);
        //     if (leader != this.getAgentId()) {
        //         stateMatrix.set(0, 5 + i, (float) -contCompletion/ (float) contSize);
        //     }
        // }
        for (int i = 0; i < state.getBoard().continents().size(); i++) {
        Continent c = state.getBoard().continents().getById(i);
        int leader = continentLeaderMap.get(c);
        
        if (leader == -1) {
            // no one owns any territories in this continent yet
            stateMatrix.set(0, 5 + i, 0.0);
            continue;
        }
        
        int contCompletion = playerContinentCompletionMap.get(leader).get(c);
        int contSize = c.territories().size();
        
        float value = (float) contCompletion / (float) contSize;
        if (leader != this.getAgentId()) {
            value = -value;
        }
        stateMatrix.set(0, 5 + i, value);
    }

        System.out.println();

        //  EXPOSED territories

        // loop through our player's territories (state.getTerritoriesOwnedBy)
        // ----for each territory, look at its adjacent territories
        // -------count how many are owned by hostiles
        // -------tally up

        Integer numExposedTerritories = 0;
        Integer numAdjacentHostiles = 0;
        Integer numAdj = 0;
        Set<Territory> hostileTerritories = new HashSet<>();

        for (Territory t : state.getTerritoriesOwnedBy(this.getAgentId())) {
            Set<Territory> adjacents = t.adjacentTerritories();
            Boolean inDanger = false;
            for (Territory adTerritory : adjacents) {
                if (!playerTerritorySets.get(this.getAgentId()).contains(adTerritory)) {
                    hostileTerritories.add(adTerritory);
                    numAdjacentHostiles +=1;
                    inDanger = true;
                }
            }
            if (inDanger) {
                numExposedTerritories +=1;
            }
        }
        numAdj = hostileTerritories.size();
        // System.out.println();
        // System.out.println("num adjacent hostiles: " + numAdjacentHostiles);
        // System.out.println("cleaned adjacent hostiles: " + numAdj);
        // System.out.println("num exposed territories: " + numExposedTerritories);
        // System.out.println("hostile territories");
        // for (Territory t : hostileTerritories) {
        //     System.out.println(t.name());
        // }
        System.out.println();


        stateMatrix.set(0, 11, numAdjacentHostiles);
        stateMatrix.set(0,12,numAdj);
        stateMatrix.set(0,13,numExposedTerritories);


        // playerTerritorySets.forEach((key,value) -> {
        //     System.out.println("player " + key + " has ");
        //     for (Territory t : value) {
        //         System.out.println(t.name());
        //     }
        //     System.out.println();
        // });
       



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

        List<TerritoryCard> myCards = state.getAgentInventory(this.getAgentId());
        // total number of cards in hand (normalized by max hand size ~44)
        float cardCount = (float) myCards.size() / (float) 44.0;

        // number of wild cards
        double wildCount = myCards.stream()
            .filter(TerritoryCard::isWild)
            .count();

        // whether I can currently make a valid trade
        double canTrade = TerritoryCard.hasValidTrade(myCards) ? 1.0 : 0.0;

        

        stateMatrix.set(0, 14, cardCount);
        stateMatrix.set(0,15,wildCount);
        stateMatrix.set(0, 16, canTrade);
        
        // todo cards held
        // todo players remaining

        // System.out.println();
        // System.out.println("agentInventories");
        // List<IAgent> iAList = state.getAgents();
        // for (IAgent ia : iAList) {
        //     System.out.println("agent " + ia.agentId());
        // }
        System.out.println("we own " + stateMatrix.get(0, 0) + " continents");
        System.out.println("we own " + stateMatrix.get(0,1) + " territories");
        System.out.println("we have " + stateMatrix.get(0, 2) + " armies");
        System.out.println("our most dangerous enemy has " + stateMatrix.get(0, 3) + " armies");
        System.out.println("our most dangerous enemy is " + stateMatrix.get(0, 4));
        System.out.println("Asia is " + stateMatrix.get(0, 5) + " completed");
        System.out.println("North America is " + stateMatrix.get(0, 6) + " completed");
        System.out.println("South America is " + stateMatrix.get(0, 7) + " completed");
        System.out.println("Africa is " + stateMatrix.get(0, 8) + " completed");
        System.out.println("Europe is " + stateMatrix.get(0, 9) + " completed");
        System.out.println("Australia is " + stateMatrix.get(0, 10) + " completed");
        System.out.println("there are " + stateMatrix.get(0, 11) + " threat points to us");
        System.out.println("there are " + stateMatrix.get(0, 12) + " enemy territories in contact with us");
        System.out.println(stateMatrix.get(0, 13) + " of our territories are in contact with enemies");
        System.out.println("our hand size capacity: " + stateMatrix.get(0, 14));
        System.out.println("we have " + stateMatrix.get(0, 15) + " wild cards");
        System.out.println("can trade: " + stateMatrix.get(0, 16) );

        // for (Continent c : state.getBoard().continents()) {
        //     System.out.println(c.name());
        // }
        System.out.println();
        
        System.out.println("stateMatrix: " + stateMatrix.toString());
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

