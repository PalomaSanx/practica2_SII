package algorithms.qlearning;

import learning.*;
import utils.Utils;

/**
 * This class must implement the QLearning algorithm to learn the optimal
 * policy.
 */
public class QLearning extends LearningAlgorithm {

	/* Table containing the Q values for each pair State-Action. */
	private QTable qTable;

	/* Number of iterations used to learn the algorithm. */
	private int iterations = 1000;

	/* Alpha parameter. */
	private double alpha = 0.1;

	/* Probability of doing a random selection of the action (instead of q) */
	private double probGreedy = 0.9;

	/** Sets the number of iterations. */
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	/** Sets the parameter alpha. */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/**
	 * Learns the policy (notice that this method is protected, and called from the
	 * public method learnPolicy(LearningProblem problem, double gamma) in
	 * LearningAlgorithm.
	 */
	public void learnPolicy() {
		// Creates the QTable
		qTable = new QTable(problem);

		// The algorithm carries out a certain number of iterations
		for (int nIteration = 0; nIteration < iterations; nIteration++) {
			State currentState, newState; // Current state and new state
			Action selAction; // Selected action
			double Q, reward, maxQ; // Values necessary to update the table.

			// Generates a new initial state.
			currentState = problem.getRandomState();//---->Aqui obtenemos un estado inicial aleatorio.(inicializamos).
			// Use fix init point for debugging
			// currentState = problem.getInitialState();

			// Iterates until it finds a final state.

			// ****************************/
			//
			// TO DO
			//
			//
			// ***************************/
			//----algoritmo----//
			
			while (!problem.isFinal(currentState)) { 
				
				if (qTable.getActionMaxValue(currentState)==null) { //si la qTabla no tiene valores asignados o no estaba el estado en la tabla.
					//System.out.println(qTable.toString());
					selAction = problem.randomAction(currentState); //también podríamos coger según un orden como vimos en clase.
					newState = problem.applyAction(currentState, selAction);				
				} else {
					
				selAction = qTable.getActionMaxValue(currentState); //guardamos la accion con valor máximo.
				newState = problem.applyAction(currentState, selAction); //se aplica dicha accion al estado actual.
				
				}
				
				reward = problem.getReward(newState) + problem.getTransitionReward(currentState, selAction, newState); //obtenemos la recompensa (si hay queso/gato + paso por agua/tunel)
				
				if (problem.isFinal(newState)) {

					qTable.setQValue(currentState, selAction,
							(1 - alpha) * qTable.getQValue(currentState, selAction) + alpha * (reward)); //se actualiza la Q-Table, si es un estado final no calcula el valor máximo por accion
				} else {

					qTable.setQValue(currentState, selAction, (1 - alpha) * qTable.getQValue(currentState, selAction)
							+ alpha * (reward + problem.gamma * qTable.getMaxQValue(newState))); //se actualiza la Q-Table con el valor máximo para la acción 'newState'
				}
				
				currentState = newState; //actualizamos el estado actual, y volvemos de forma concurrente hasta la siguiente ite(currentState is final).
			}

		}

		solution = qTable.generatePolicy(); //por cada uno de los estados de la tabla obtiene aquella acción cuyo valor sea mayor.(devuelve Policy (HashMap (estado,accion)).
		printResults();
		
		
	}

	/** Sets the parameters of the algorithm. */
	@Override
	public void setParams(String[] args) {
		if (args.length > 0) {
			// Alpha
			try {
				alpha = Double.parseDouble(args[0]);
			} catch (Exception e) {
				System.out.println("The value for alpha is not correct. Using 0.75.");
			}
			// Maximum number of iterations.
			if (args.length > 1) {
				try {
					iterations = Integer.parseInt(args[1]);
				} catch (Exception e) {
					System.out.println("The value for the number of iterations is not correct. Using 1000.");
				}
			}
		}
	}

	/** Prints the results */
	public void printResults() {
		// Prints the utilities.
		System.out.println("QLearning \n");
		// Prints the policy
		System.out.println("\nOptimal policy");
		System.out.println(solution);
		// Prints the qtable
		System.out.println("QTable");
		System.out.println(qTable);
	}

	/** Main function. Allows testing the algorithm with MDPExProblem */
	public static void main(String[] args) {
		LearningProblem mdp = new problems.mdpexample2.MDPExProblem();
		mdp.setParams(null);
		QLearning ql = new QLearning();
		ql.setProblem(mdp);
		ql.learnPolicy(mdp);
		ql.printResults();
	}
}
