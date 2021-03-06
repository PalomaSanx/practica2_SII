package problems.maze;

import java.util.ArrayList;
import java.util.Collection;

import learning.*;
import visualization.*;
import utils.*;

/** 
 * Implements the maze problem as a model free problem.
 */
public class MazeProblemMF extends MFLearningProblem  implements MazeProblem, ProblemVisualizable{
	
	/** Size of the problem. Default value is 10.*/
	protected int size = 10;
	
	/** Maze */
	protected Maze maze;
	
	/** Constructors */
	public MazeProblemMF(){
		this.maze = new Maze(size,0);
		initialState = new MazeState(maze.posHamster.x, maze.posHamster.y);
	}
	public MazeProblemMF(int size){
		this.size = size;
		this.maze = new Maze(size, 0);
	}
	public MazeProblemMF(int size, int seed){
		this.size = size;
		this.maze = new Maze(size,seed);
		initialState = new MazeState(maze.posHamster.x, maze.posHamster.y);
	}
	public MazeProblemMF(Maze maze){
		this.size = maze.size;
		this.maze = maze;
		initialState = new MazeState(maze.posHamster.x, maze.posHamster.y);
	}	
	
	/** Generates a random instance of the problem given the seed. */
	private void generateInstance(int size, int seed) {
		this.size = size;
		this.maze = new Maze(size,seed);
	    initialState = new MazeState(maze.posHamster.x, maze.posHamster.y);
	}		
	
	/** Returns a reference to the maze */
	public Maze getMaze(){
		return this.maze;
	}
	
	// Inherited from Learning Problem
	
	/** Whether the state corresponds to a final state (CAT or CHEESE).*/
	@Override
	public boolean isFinal(State state) {
		//
		// COMPLETAR
		// 
		MazeState mazestate = (MazeState)state;
		int posx = mazestate.position.x;
		int posy = mazestate.position.y;
		if(maze.cells[posx][posy] == Maze.CAT || maze.cells[posx][posy]==Maze.CHEESE) {
			return true;
		}
			return false; 
	}

	/** Returns the set of actions that can be done at each step. */
	@Override
	public ArrayList<Action> getPossibleActions(State state) {
		MazeState mazeState = (MazeState) state;
		ArrayList<Action> possibleActions = new ArrayList<Action>();
		//
		// COMPLETAR
		// 
		int posx = mazeState.X();
		int posy = mazeState.Y();

		// comprobamos que estamos dentro del laberinto

		// queremos movernos a la derecha y en la derecha no tengamos un muro
//		if ((posx < maze.size - 1 ) && (maze.cells[posx + 1][posy] != Maze.WALL)) {
//			possibleActions.add(MazeAction.RIGHT);
//		}
//
//		// queremos movernos a la izquierda y en la izquierda no tengamos un muro
//		if ((posx > 0) && (maze.cells[posx - 1][posy] != Maze.WALL)) {
//			possibleActions.add(MazeAction.LEFT);
//		}
//	
//		// queremos movernos hacia arriba y no tengamos un muro
//		if ((posy > 0) && (maze.cells[posx][posy - 1] != Maze.WALL)) {
//			possibleActions.add(MazeAction.UP);
//		}
//		
//		// queremos movernos hacia abajo y no tengamos un muro
//		if ((posy < maze.size-1) && (maze.cells[posx][posy + 1] != Maze.WALL)) {
//			possibleActions.add(MazeAction.DOWN);
//
//		}
		possibleActions.add(MazeAction.RIGHT);
		possibleActions.add(MazeAction.LEFT);
		possibleActions.add(MazeAction.UP);
		possibleActions.add(MazeAction.DOWN);
		// queremos meternos al hueco
		
		if (maze.cells[posx][posy] == Maze.HOLE) {
			possibleActions.add(MazeAction.DIVE);

		}
		//System.out.println(possibleActions);
		return possibleActions;
		
	}	
	
	/** Returns the reward of an state. */	
	@Override
	public double getReward(State state){
		double reward=0;
		MazeState mazestate = (MazeState)state;
		int posx = mazestate.X();
		int posy = mazestate.Y();
		//
		//
		// COMPLETAR
		// 
		
		//devolvemos la recompensa si es gato=-100 y si es queso=100 (estados finales).
				if(maze.cells[posx][posy]==Maze.CAT) {
					reward=-100;
					return reward;
				}
				if(maze.cells[posx][posy]==Maze.CHEESE) {
					reward=100;
					return reward;
				}
				// Otherwise returns 0
				return reward;
	
	}	
	
	/**  In this case, the transition reward penalizes distance. */	
	@Override
	public double getTransitionReward(State fromState, Action action, State toState) {
		
		double reward = 0;
		
		//
		// COMPLETAR
		// 
		
		// Returns the reward
		
		MazeState mazeState_fromStat = (MazeState)fromState;
		MazeAction mazeAction = (MazeAction)action;
		MazeState mazeState_toState = (MazeState)toState;
		int posx_f = mazeState_fromStat.X();
		int posy_f = mazeState_fromStat.Y();
		int posx_to = mazeState_toState.X();
		int posy_to = mazeState_toState.Y();
		double dist_eucli = 0;
		
		//calculo distancia euclidea
		
		dist_eucli = Math.sqrt(Math.pow((posx_to-posx_f), 2)+Math.pow((posy_to-posy_f), 2));
		reward=  (-1*dist_eucli);
		
		if(mazeAction == MazeAction.DIVE) { 
			reward=reward*0.5;
			return reward;
		}
		if (maze.cells[posx_f][posy_f]==Maze.WATER) {
			reward = reward*2;
			return reward;
		}
		
		return reward; 
	}	
	
	// From MFLearningProblem
	
	/** Generates the transition model for a certain state in this particular problem. 
	  * Assumes that the  action can be applied. This method is PRIVATE.*/
	private StateActionTransModel mazeTransitionModel(State state, Action action){
		// Structures contained by the transition model.
		State[] reachable;
		double[] probs;	
		
		// Coordinates of the current state
		int fromX,fromY;
		fromX = ((MazeState) state).X();
		fromY = ((MazeState) state).Y();	
		
		/* First considers diving. */
		if (action==MazeAction.DIVE){
			// It must be a hole. Gets the outputs.
			Position inputHolePos = new Position(fromX,fromY);
			// It considers all holes but one
			reachable = new State[maze.numHoles-1];
			probs = new double[maze.numHoles-1];
			int holeNum=0;
			for (Position holePos: maze.holeList){
				if (holePos.equals(inputHolePos))
					continue;
				reachable[holeNum]=new MazeState(holePos);
				probs[holeNum++] = 1.0/(maze.numHoles-1);
			}
			// Returns 
			return new StateActionTransModel(reachable, probs);
		}
		
	
		// Creates the transition model.
		reachable = new State[4];
		probs = new double[4];
		
		// Probability of error 0.1 times each position.
		double probError = 0.1;
		double probSuccess = 1.0 - probError*(4-1);
		
		int ind=0;
		//
		// Cell X,Y-1
		//
		// Probability
		if (action==MazeAction.UP)
			probs[ind]=probSuccess;
		else
			probs[ind]=probError;
		// Reached state
		if ((fromY>0) && (maze.cells[fromX][fromY-1]!=Maze.WALL))  
			reachable[ind] = new MazeState(fromX,fromY-1); // Can move
		else
			reachable[ind] = new MazeState(fromX,fromY); // Can't move
	
		
		ind++;
		//
		// Cell X,Y+1
		//
		// Probability
		if (action==MazeAction.DOWN)
			probs[ind]=probSuccess;
		else
			probs[ind]=probError;	
		// Reached state
		if ((fromY<maze.size-1) && (maze.cells[fromX][fromY+1]!=Maze.WALL))  
			reachable[ind] = new MazeState(fromX,fromY+1); // Can move
		else
			reachable[ind] = new MazeState(fromX,fromY); // Can't move
	
		
		ind++;
		//
		// Cell X-1,Y
		//
		// Probability
		if (action==MazeAction.LEFT)
			probs[ind]=probSuccess;
		else
			probs[ind]=probError;
		// Reached state
		if ((fromX>0) && (maze.cells[fromX-1][fromY]!=Maze.WALL))  
			reachable[ind] = new MazeState(fromX-1,fromY); // Can move
		else
			reachable[ind] = new MazeState(fromX,fromY); // Can't move

		
		ind++;
		//
		// Cell X,Y+1
		//
		// Probability
		if (action==MazeAction.RIGHT)
			probs[ind]=probSuccess;
		else
			probs[ind]=probError;
		// Reached state
		if ((fromX<maze.size-1) && (maze.cells[fromX+1][fromY]!=Maze.WALL))  
			reachable[ind] = new MazeState(fromX+1,fromY); // Can move
		else
			reachable[ind] = new MazeState(fromX,fromY); // Can't move
				
		
		// Returns 
		return new StateActionTransModel(reachable, probs);
	}	
	
	// From MFLearningModel
	
	/** Updates the environment. */
	@Override
	public void updateEnvironment(State state, Action action) {
		//
		// COMPLETAR
		// 
		
		//NO HACE NADA YA QUE EN ESTE PROBLEMA EL AGENTE NO MODIFICA EL ENTORNO(Environment)
		
		
	}
	
	/** Generates the new state. */
	@Override
	public State readNewState(State state, Action action) {
		//
		// COMPLETAR
		// 
		MazeState stateE = (MazeState)state;
		MazeAction actionN = (MazeAction)action;

		//se genera el modelo de transicción 
		StateActionTransModel model = mazeTransitionModel(state, action); //genera el modelo de transición para ese estado y accion concreta.
		return model.genNextState(); //sobre el modelo de transición devuelve el nuevo estado.
	}
	
	// Utilities
	
		/** Returns a random state. */
		@Override
		public State getRandomState() {
			// Returns only positions corresponding to empty cells.
			int posX, posY;
			boolean validCell = false;
			do{
				posX = Utils.random.nextInt(size);
				posY = Utils.random.nextInt(size);
				// Walls are not valid states. 
				if (maze.cells[posX][posY]==Maze.WALL)
					continue;
				// Sometimes (not very often) there are empty cells surrounded
				// by walls or by the limit of the maze.  Test that there is at least
				// an adjacent position to move.
				if (posX>0  && maze.cells[posX-1][posY]==Maze.EMPTY) validCell = true;
				if (posX<maze.size-1  && maze.cells[posX+1][posY]==Maze.EMPTY) validCell = true;
				if (posY>0  && maze.cells[posX][posY-1]==Maze.EMPTY) validCell = true;
				if (posY>maze.size-1  && maze.cells[posX][posY+1]==Maze.EMPTY) validCell = true;
			} while (!validCell);
			return new MazeState(posX,posY);
		}
	
		/** Generates and instance of the problem.*/
		@Override
		public void setParams(String[] params) {
			try {
				if (params.length==1) generateInstance(Integer.parseInt(params[0]), 0);
				else generateInstance(Integer.parseInt(params[0]), Integer.parseInt(params[1]));		
			}
			catch (Exception E) {
				System.out.println("There has been an error while generating the na new instance of MazeProblem.");
			}
		}	
		
		/* Visualization */
		
		/** Returns a panel with the view of the problem. */
		public ProblemView getView(int sizePx){
			MazeView mazeView = new MazeView(this, sizePx);
			return mazeView;
		}
		
		/* Test */
		public static void main(String[] args) {
			MazeProblemMF mazeProblem = new MazeProblemMF(15, 5);
//			ProblemVisualization mainWindow = new ProblemVisualization(mazeProblem,600);		// Main window
			
			State currentState = mazeProblem.initialState();
			System.out.println("Current state: "+currentState);
			System.out.println("Reward: "+mazeProblem.getReward(currentState));
			System.out.println("Is final: "+mazeProblem.isFinal(currentState));
			
			System.out.println("Possible actions: ");
			ArrayList<Action> actions = mazeProblem.getPossibleActions(currentState);
			for (Action action: actions)
				System.out.println("\t"+action);
			
			System.out.println("Apply action UP.\n");
			State newState = mazeProblem.applyAction(currentState, MazeAction.UP);
			System.out.println("New state: "+newState);
			System.out.println("Transition reward:"+ mazeProblem.getTransitionReward(currentState, MazeAction.UP, newState));
		}

	


}
