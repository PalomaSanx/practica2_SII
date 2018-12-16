package algorithms.mdp;

import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import learning.*;
import utils.Utils;
import problems.maze.*;

public class PolicyIteration extends LearningAlgorithm {
	/** Max delta. Controls convergence. */
	private double maxDelta = 0.01;

	/**
	 * Learns the policy (notice that this method is protected, and called from the
	 * public method learnPolicy(LearningProblem problem, double gamma) in
	 * LearningAlgorithm.
	 */
	@Override
	protected void learnPolicy() {
		if (!(problem instanceof MDPLearningProblem)) {
			System.out.println(
					"The algorithm PolicyIteration can not be applied to this problem (model is not visible).");
			System.exit(0);
		}

		// Initializes the policy randomly
		solution = new Policy();
		Policy policyAux = null;
		// ****************************/
		//
		// TO DO
		//
		//
		// ***************************/
		// --atributos
		HashMap<State, Double> utilities = new HashMap<State, Double>();
		Collection<State> allStates = ((MDPLearningProblem) problem).getAllStates();
		// ---------Establecemos la política aleatoria------------------------//
		for (State state : allStates) {
			// if(!problem.isFinal(state)) {
			ArrayList<Action> possibleActions = problem.getPossibleActions(state); // guarda las posibles acciones para
																					// un estado 'x'

			int idAccion = Utils.random.nextInt(possibleActions.size()); // genera un nº random entre 0 y el nº de
																			// acciones de el estado 'x'
			solution.setAction(state, possibleActions.get(idAccion)); // añadimos la accion con id 'y' a la solución

			// }
		}
		// Main loop of the policy iteration.
		// ****************************/
		//
		// TO DO
		//
		//
		// ***************************/

		do {
			policyAux = solution; // guardamos la política.(A,B,C..)<--random
			utilities = policyEvaluation(policyAux); // evaluamos la politica=obtenemos las utilidades de esa política.
			solution = policyImprovement(utilities);// obtenemos la mejor politica para esas utilidades. (b,c,a).
		} while (!solution.equals(policyAux));// en cuanto sean iguales las políticas, es decir, ha encontrado la mejor
												// PARAMOS.
		// (B,C,A != A,B,C)
		// (a,c,c == A,C,C) para.
	}

	/**
	 * Policy evaluation. Calculates the utility given the policy
	 */
	private HashMap<State, Double> policyEvaluation(Policy policy) {

		// Initializes utilities. In case of terminal states, the utility corresponds to
		// the reward. In the remaining (most) states, utilities are zero.
		HashMap<State, Double> utilities = new HashMap<State, Double>();
		// ****************************/
		//
		// TO DO
		//
		//
		// ***************************/

		Collection<State> states = ((MDPLearningProblem) problem).getAllStates();
		double delta;
		double sumatorio = 0;
		double gamma = ((MDPLearningProblem) problem).gamma;
		

		HashMap<State, Double> utilities1 = new HashMap<State, Double>();
		for (State state : states) {

			if (!problem.isFinal(state)) {
				utilities.put(state, 0d);
			} else {
				utilities.put(state, problem.getReward(state));
			}

		}

		do {
			delta = 0;
			states = ((MDPLearningProblem) problem).getAllStates();
			sumatorio = 0;
			for (State state : states) {
				if (!problem.isFinal(state)) {
					sumatorio = ((MDPLearningProblem) problem).getExpectedUtility(state, policy.getAction(state),
							utilities, gamma);
					utilities1.put(state, sumatorio);
					if (Math.abs(utilities1.get(state) - utilities.get(state)) > delta) {
						delta = Math.abs(utilities1.get(state) - utilities.get(state));
					}

				}
			}
			utilities.putAll(utilities1);
		} while (delta >= (maxDelta * (1 - gamma) / gamma));

		return utilities;
	}

	/**
	 * Improves the policy given the utility
	 */
	private Policy policyImprovement(HashMap<State, Double> utilities) {
		// Creates the new policy
		Policy newPolicy = new Policy();
		// ****************************/
		//
		// TO DO
		//
		//
		// ***************************/

		double sumatorio = 0;
		double max;
		Action mejor = null;
		double gamma = ((MDPLearningProblem) problem).gamma;
		Collection<State> allStates = ((MDPLearningProblem) problem).getAllStates();

		for (State state : allStates) {
			sumatorio = 0;
			max = Double.NEGATIVE_INFINITY;
			Collection<Action> acciones = ((MDPLearningProblem) problem).getPossibleActions(state);
			if (!problem.isFinal(state)) {
				for (Action accion : acciones) {
					sumatorio = ((MDPLearningProblem) problem).getExpectedUtility(state, accion, utilities, gamma);
					if (sumatorio > max) {
						max = sumatorio;
						mejor = accion;
					}
				}
				newPolicy.setAction(state, mejor);

			}
		}

		return newPolicy;
	}

	/**
	 * Sets the parameters of the algorithm.
	 */
	@Override
	public void setParams(String[] args) {
		// In this case, there is only one parameter (maxDelta).
		if (args.length > 0) {
			try {
				maxDelta = Double.parseDouble(args[0]);
			} catch (Exception e) {
				System.out.println("The value for maxDelta is not correct. Using 0.01.");
			}
		}
	}

	/** Prints the results */
	public void printResults() {
		System.out.println("Policy Iteration");
		// Prints the policy
		System.out.println("\nOptimal policy");
		System.out.println(solution);
	}

	/** Main function. Allows testing the algorithm with MDPExProblem */
	public static void main(String[] args) {
		LearningProblem mdp = new problems.mdpexample2.MDPExProblem();
		mdp.setParams(null);
		PolicyIteration pi = new PolicyIteration();
		pi.setProblem(mdp);
		pi.learnPolicy(mdp);
		pi.printResults();
	}

}
