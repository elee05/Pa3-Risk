package pas.risk.rewards;


// SYSTEM IMPORTS
import edu.bu.jmat.Pair;

import edu.bu.pas.risk.GameView;
import edu.bu.pas.risk.agent.rewards.RewardFunction;
import edu.bu.pas.risk.agent.rewards.RewardType;
import edu.bu.pas.risk.territory.Territory;
import pas.risk.senses.MyStateSensorArray;
import pas.risk.senses.MyPlacementSensorArray;
import edu.bu.pas.risk.territory.Continent;



import edu.bu.pas.risk.util.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.bu.jmat.Matrix;

// JAVA PROJECT IMPORTS


/**
 * <p>Represents a function which punishes/pleasures your model according to how well the {@link Territory}s its been
 * choosing to place armies have been. Your reward function could calculate R(s), R(s,t), or (R,t,a'): whichever
 * is easiest for you to think about (for instance does it make more sense to you to evaluate behavior when you see a
 * state, the action you took in that state, and how that action resolved? If so you want to pick R(s,t,s')).
 *
 * <p>By default this is configured to calculate R(s). If you want to change this you need to change the
 * {@link RewardType} enum in the constructor *and* you need to implement the corresponding method. Refer to
 * {@link RewardFunction} and {@link RewardType} for more details.
 */
public class MyPlacementRewardFunction
    extends RewardFunction<Territory>

    // ! total continent completion
    // ! expected value of troop generation
    // ! num enemies current continent
    
{

    public MyPlacementRewardFunction(final int agentId)
    {
        super(RewardType.STATE, agentId); // change this enum if you don't want to do R(s)
    }

    public double getLowerBound() { return 0.0; }
    public double getUpperBound() { return 100.0; }

    public double sigmoid(double x) {
        return 1.0 / (1+ Math.exp(-x));
    }

    /** {@inheritDoc} */
    public double getStateReward(final GameView state) { 

        Double reward = 0.0;



        MyStateSensorArray stateSensor = new MyStateSensorArray(this.getAgentId());
        Matrix stateArray = stateSensor.getSensorValues(state);

        // MyPlacementSensorArray placementSensor = new MyPlacementSensorArray(this.getAgentId());
        // Matrix placementarray = placementSensor.getSensorValues(state, 0, null);

        // ? reward for just having territories
        Double territoryReward = (double) stateArray.get(0,1) / 42.0;
        // System.out.println("territory reward: " + (double) stateArray.get(0,1) / 42.0);
        reward += territoryReward;
        // System.out.println("reward for territory count(" + stateArray.get(0,1) + "): " + territoryReward);
        
        // ? REWARD FOR COMPLETION ACROSS ALL CONTINENTS
        Double asiaCompletion = (Double) stateArray.get(0, 5);
        Double nAmericaCompletion = (Double) stateArray.get(0, 6);
        Double sAmeriaCompletion = (Double) stateArray.get(0, 7);
        Double africaCompletion = (Double) stateArray.get(0, 8);
        Double europeCompletion = (Double) stateArray.get(0, 9);
        Double australiaCompletion = (Double) stateArray.get(0, 10);

        List<Double> continentCompletions = Arrays.asList(
            asiaCompletion,
            nAmericaCompletion,
            sAmeriaCompletion,
            africaCompletion,
            europeCompletion,
            australiaCompletion
        );

        Integer ourArmyGeneration = 0;
        Double avgEnemyArmyGeneration;

        Integer totalEnemyArmyGenerations = 0;

        Registry<Continent> contList = state.getBoard().continents();

        for (int i=0; i < continentCompletions.size();i++) {
            Double contComp = continentCompletions.get(i);

            Continent cont = contList.getById(i);
            Integer armiesInCont = cont.armiesPerTurn();

            if (contComp > 0) {
                // ! adding reward for continent completion (0-1)
                reward += Math.pow( (contComp), 2);
                // System.out.println("reward for continent completion of " + cont.name() +  "(" + contComp + "): " + Math.pow( (contComp), 2)) ;
                if (contComp == 1) {
                    ourArmyGeneration+= armiesInCont;
                }
            } else if (contComp == -1) {
                totalEnemyArmyGenerations += armiesInCont;
            }
        }
        // System.out.println();
        // System.out.println();
        // System.out.println("reward finished cont loop");
        // System.out.println();
        // System.out.println();

        avgEnemyArmyGeneration = (double) totalEnemyArmyGenerations / (double) state.getNumAgents();
        Double selfToEnemyArmyRatio = (double) ourArmyGeneration / Math.max(avgEnemyArmyGeneration, 1);

        // ! adding reward for self to army ratio(0-infin)
        // todo need to normalize
        // System.out.println("current reward for territories and cont completion: " + reward);
        reward += selfToEnemyArmyRatio;
        // System.out.println("reward, ourArmyGeneration: " + ourArmyGeneration);
        // System.out.println("reard, avgEnemyArmyGeneration: " + avgEnemyArmyGeneration);
        // System.out.println("reward, selfToEnemyArmyRatio value: " + selfToEnemyArmyRatio);
        


       
     



        // TODO expected value of troop generation
        // TODO num contesting enemies



        // System.out.println("TOTAL reward: " + reward);
        return reward; 
    
    } // this sucks you'll need to change this

    /** {@inheritDoc} */
    public double getHalfTransitionReward(final GameView state,
                                          final Territory action) { 
                                            
        // return Double.NEGATIVE_INFINITY; 
        return 0.0;
    }

    /** {@inheritDoc} */
    public double getFullTransitionReward(final GameView state,
                                          final Territory action,
                                          final GameView nextState) { 
                                            
        // return Double.NEGATIVE_INFINITY; 
        return 0.0;
    }

}

