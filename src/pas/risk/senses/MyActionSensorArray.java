package pas.risk.senses;


// SYSTEM IMPORTS
import edu.bu.jmat.Matrix;

import edu.bu.pas.risk.GameView;
import edu.bu.pas.risk.action.Action;
import edu.bu.pas.risk.action.AttackAction;
import edu.bu.pas.risk.territory.Territory;
import edu.bu.pas.risk.TerritoryOwnerView;
import edu.bu.pas.risk.action.FortifyAction;
import edu.bu.pas.risk.action.NoAction;
import edu.bu.pas.risk.action.RedeemCardsAction;
import edu.bu.pas.risk.agent.senses.ActionSensorArray;
import edu.bu.pas.risk.util.Registry;

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

        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println("getACTIONSensorValues called");
        System.out.println("getACTIONSensorValues called");
        System.out.println("getACTIONSensorValues called");
        System.out.println("getACTIONSensorValues called");
        System.out.println("getACTIONSensorValues called");

        System.out.println();
        System.out.println();
        System.out.println();

        // ? player id                          0
        // ? terminal flag                      1
        // ? action type flag                   2  (1:Attack, 2:Fortify,3:Redeem Cards)
        // ? ATTACK info: #armies in attack     3
        // ? ATTACK info: #armies moving        4
        // ? ATTACK info: #army ratio           5
        // ? FORTIFY info: #armies moving       6

        int id=this.getAgentId();
        Matrix actionMatrix=Matrix.zeros(1,NUM_FEATURES);
        //decide if actions ends
        actionMatrix.set(0, 0, id);
        if(!action.isTerminal()){
            actionMatrix.set(0,1,1);
            System.out.println("action is terminal");
        }

        // !ACTION is ATTACK
        if (action instanceof AttackAction) {
            AttackAction a = (AttackAction) action;
            System.out.println("action is of type: ATTACK");

            actionMatrix.set(0,2,1); // flag for attacking
            actionMatrix.set(0,3,a.attackingArmies());
            actionMatrix.set(0,4,a.movingArmies());

            Territory from  = a.from();
            Territory to = a.to();

            Integer defendingArmies = 0;

            Registry<TerritoryOwnerView> tview = state.getTerritoryOwners();
            for (TerritoryOwnerView tOView : tview) {
                if (tOView.getTerritory() == to) {
                    defendingArmies = tOView.getArmies();
                }
            }

            float armyRatio = (float) a.attackingArmies() / (float) defendingArmies;
            
            actionMatrix.set(0,5,armyRatio);


            // todo calculate army ratio

            // use a.attackingArmies(), a.from(), a.to(), etc.


        // !ACTION is FORTIFY
        } else if (action instanceof FortifyAction) {
            FortifyAction f = (FortifyAction) action;
            System.out.println("action is of type: FORTIFY");


            actionMatrix.set(0,2,2); // flag for fortifying
            actionMatrix.set(0,6,f.deltaArmies());
            // use f.from(), f.to(), f.deltaArmies()


        // !ACTION is REEDEMCARDS
        } else if (action instanceof RedeemCardsAction) {
            RedeemCardsAction r = (RedeemCardsAction) action;
            System.out.println("action is of type: REDEEM CARDS");

            actionMatrix.set(0,2,3);
            
            // cast and use


        // !ACTION is  NOACTION
        } else if (action instanceof NoAction) {
            System.out.println("action is of type: NO ACTION");
            // terminal / pass action
        }

        

        System.out.println("action matrix: " + actionMatrix.toString());
        return actionMatrix;

        // This encodes a specific action. The action can be an AttackAction, FortifyAction, RedeemCardsAction, or NoAction. You have 10 features. The key insight: you need to handle the different action types.
        // Good features:

        // Action type — one-hot encode (attack=1 0 0 0, fortify=0 1 0 0, redeem=0 0 1 0, no-op=0 0 0 1)
        // For attacks (AttackAction):

        // Attacking armies (action.attackingArmies())
        // Moving armies after winning (action.movingArmies())
        // Attacker army count at from() territory
        // Defender army count at to() territory
        // Army ratio: attacker/defender (is this attack favorable?)
        // Whether the target territory would complete a continent for you


        // Action counter (normalized) — how many actions you've taken this turn so far
        // Is terminal — action.isTerminal()
      
         
         
        

    }

}

