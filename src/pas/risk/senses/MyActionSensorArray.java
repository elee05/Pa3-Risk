package pas.risk.senses;


// SYSTEM IMPORTS
import edu.bu.jmat.Matrix;

import edu.bu.pas.risk.GameView;
import edu.bu.pas.risk.action.Action;
import edu.bu.pas.risk.agent.senses.ActionSensorArray;


// JAVA PROJECT IMPORTS


/**
 * A suite of sensors to convert a {@link Action} into a feature vector (must be a row-vector)
 */ 
public class MyActionSensorArray
    extends ActionSensorArray
{

    public static final int NUM_FEATURES = 10;

    public MyActionSensorArray(final int agentId)
    {
        super(agentId);
    }

    public Matrix getSensorValues(final GameView state,
                                  final int actionCounter,
                                  final Action action)
    {
        //return Matrix.randn(1, NUM_FEATURES); // row vector

        int id=this.getAgentId();
        Matrix m=Matrix.zeros(1,NUM_FEATURES);
        //decide if actions ends
        if(!action.isTerminal()){
            m.set(0,0,0.0);
        }else{
            m.set(0,0,1.0);
        }// game features 
        m.set(0,1,(double)actionCounter);
         m.set(0,2,(double)state.getTerritoriesOwnedBy(id).size());
         m.set(0,3,(double)state.getContinentsOwnedBy(id).size());
        m.set(0,4,(double)state.getBonusArmiesFor(id));
        //deicde the type of action todo
      
         
         
        

    }

}

