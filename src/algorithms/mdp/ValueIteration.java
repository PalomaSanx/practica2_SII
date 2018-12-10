package algorithms.mdp;

import java.util.Collection;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.management.monitor.GaugeMonitorMBean;
import javax.xml.soap.Detail;

import learning.*;

/**
 * Implements the value iteration algorithm for Markov Decision Processes
 */
public class ValueIteration extends LearningAlgorithm {

	/** Stores the utilities for each state */
	;
	private HashMap<State, Double> utilities;
	private HashMap<State, Double> utilitiesCurrent;

	/** Max delta. Controls convergence. */
	private double maxDelta = 0.01;

	/**
	 * Learns the policy (notice that this method is protected, and called from the
	 * public method learnPolicy(LearningProblem problem, double gamma) in
	 * LearningAlgorithm.
	 */
	@Override
	protected void learnPolicy() {
		// This algorithm only works for MDPs
		if (!(problem instanceof MDPLearningProblem)) {
			System.out.println("The algorithm ValueIteration can not be applied to this problem (model is not visible).");
			System.exit(0);
		}

		// ****************************/
		//
		// TO DO
		//
		//
		// ***************************/

		// Atributos necesarios
		double delta = 0.0;
		Collection<State> estados = problem.getAllStates();
		double utilidadMax = Double.NEGATIVE_INFINITY;
		double sumatorio = 0;
		Action maxAccion = null;
		utilities = new HashMap<State, Double>();
		utilitiesCurrent = new HashMap<State, Double>();

		for (State state : estados) {
			if (!problem.isFinal(state)) {

				utilities.put(state, 0d);
			}else {
				utilities.put(state, problem.getReward(state));
			}
		}

		while (delta < maxDelta * (1 - problem.gamma) / (problem.gamma)) {
			estados = problem.getAllStates();
			sumatorio=0;
			
			for (State state : estados) {
				
				if (!problem.isFinal(state)) {
					utilidadMax = Double.NEGATIVE_INFINITY;
					
					for (Action action : problem.getPossibleActions(state)) {
						System.out.println(sumatorio+"  "+utilidadMax);
						sumatorio = (((MDPLearningProblem) problem).getExpectedUtility(state, action, utilities,
								problem.gamma));
						
						if (sumatorio > utilidadMax) {
							utilidadMax = sumatorio;
							maxAccion = action;
						}
						

					}

					solution.setAction(state, maxAccion);
					utilitiesCurrent.put(state, utilidadMax);

					if (Math.abs(utilitiesCurrent.get(state) - utilities.get(state)) > delta) {
						delta = Math.abs(utilitiesCurrent.get(state) - utilities.get(state));
					}
				}
			}

			utilities.putAll(utilitiesCurrent);
			
		}
		printResults();

		
		
	
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
		// Prints the utilities.
		System.out.println("Value Iteration\n");
		System.out.println("Utilities");
		for (Entry<State, Double> entry : utilities.entrySet()) {
			State state = entry.getKey();
			double utility = entry.getValue();
			System.out.println("\t" + state + "  ---> " + utility);
		}
		// Prints the policy
		System.out.println("\nOptimal policy");
		System.out.println(solution);
	}

	/** Main function. Allows testing the algorithm with MDPExProblem */
	public static void main(String[] args) {
		LearningProblem mdp = new problems.mdpexample2.MDPExProblem();
		mdp.setParams(null);
		ValueIteration vi = new ValueIteration();
		vi.setProblem(mdp);
		vi.learnPolicy(mdp);
		vi.printResults();

	}

}
