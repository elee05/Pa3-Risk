package pas.risk.rewards;


import edu.bu.jmat.Matrix;
// SYSTEM IMPORTS
import edu.bu.jmat.Pair;

import edu.bu.pas.risk.GameView;
import edu.bu.pas.risk.action.Action;
import edu.bu.pas.risk.agent.rewards.RewardFunction;
import edu.bu.pas.risk.agent.rewards.RewardType;
import pas.risk.senses.MyPlacementSensorArray;
import pas.risk.senses.MyStateSensorArray;


// JAVA PROJECT IMPORTS


/**
 * <p>Represents a function which punishes/pleasures your model according to how well the {@link Action}s its been
 * choosing have been. Your reward function could calculate R(s), R(s,a), or (R,s,a'): whichever is easiest for you to
 * think about (for instance does it make more sense to you to evaluate behavior when you see a state, the action you
 * took in that state, and how that action resolved? If so you want to pick R(s,a,s')).
 *
 * <p>By default this is configured to calculate R(s). If you want to change this you need to change the
 * {@link RewardType} enum in the constructor *and* you need to implement the corresponding method. Refer to
 * {@link RewardFunction} and {@link RewardType} for more details.
 */
public class MyActionRewardFunction
    extends RewardFunction<Action>
{

    public MyActionRewardFunction(final int agentId)
    {
        super(RewardType.STATE, agentId); // change this enum if you don't want to do R(s)
    }

    public double getLowerBound() { return 0.0; }
    public double getUpperBound() { return 100.0; }

    // todo R(s) # territories owned+ get a reward (square)
    // how many t other own
    // # armies owned relative to others
    // + reward for a higher diff(sum all diffs(army ratio))
    // - reward for higher enemy territory count(sum the square of each person territories
    // + reward for higher own ar count
    // + reward for higher troop count

    // + reward for territory completion (x10 then square)

    // ! squared +- reward for territories
    // ! +- for difference in adjacent armies
    // ! + reward for high army count
    // ! + reward for territory completion 10x then square

    /** {@inheritDoc} */
    public double getStateReward(final GameView state) { 

        Double reward = 0.0;

        MyStateSensorArray stateSensor = new MyStateSensorArray(this.getAgentId());
        Matrix stateArray = stateSensor.getSensorValues(state);

        // reward for owned territories: square it, run it through sigmoid
        // minus reward for enemy territories: sum the squares of each person,

        // ! self territory reward
        reward += Math.pow(stateArray.get(0, 1), 2.0);

        return reward; 
    
    } // this sucks you'll need to change this

    /** {@inheritDoc} */
    public double getHalfTransitionReward(final GameView state,
                                          final Action action) { return Double.NEGATIVE_INFINITY; }

    /** {@inheritDoc} */
    public double getFullTransitionReward(final GameView state,
                                          final Action action,
                                          final GameView nextState) { return Double.NEGATIVE_INFINITY; }

}

