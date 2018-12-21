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
			System.out
					.println("The algorithm ValueIteration can not be applied to this problem (model is not visible).");
			System.exit(0);
		}

		// ****************************/
		//
		// TO DO
		//
		//
		// ***************************/

		// --- Atributos necesarios ----
		double delta = 0;
		double diferencia;
		utilitiesCurrent = new HashMap<State, Double>();
		utilities = new HashMap<State, Double>();
		double utilidadMax = -10000.0;
		// -----recorremos estado a estado, y comprobamos si es final------// (0,0,0,0,-100,+100,...,0,0).
		for (State estado : ((MDPLearningProblem) problem).getAllStates()) {
			if (problem.isFinal(estado)) {
				utilities.put(estado, problem.getReward(estado));// es final= le damos -100 si es gato o +100 si es
																	// queso.
				// System.out.println("getReward->"+problem.getReward(estado));
			} else
				utilities.put(estado, new Double(0)); // no es final = se incicializa utilidades a 0.
		}

		// System.out.println(((1.0 - problem.gamma) / problem.gamma));

		do {
			delta = 0;
			for (State estado : ((MDPLearningProblem) problem).getAllStates()) {
				if (problem.isFinal(estado)) {
					utilidadMax = problem.getReward(estado); // es final= le damos -100 si es gato o +100 si es queso.
				} else {
					for (Action accion : problem.getPossibleActions(estado)) {// no es final=obtenemos acciones para el
																				// estado 'x'
						double sumatorio = ((MDPLearningProblem) problem).getExpectedUtility(estado, accion, utilities,
								problem.gamma); //Calcula la utilidad esperada para un estado-acción dadas las utilidades de todos los estados en el problema.

						if (sumatorio > utilidadMax)
							utilidadMax = sumatorio; //se queda con la utilidad max para dicho estado.
					}
				}
				utilitiesCurrent.put(estado, utilidadMax); // asigna la utilidad máxima a estado.
				diferencia = utilidadMax - utilities.get(estado); // delta para estado 'x'

				if (Math.abs(diferencia) > delta) //se actualiza delta para un estado 'x'.
					delta = Math.abs(diferencia);
				// System.out.println(" " + utilities.get(estado) + " pasa a ser " +
				// utilidadMax);
				// System.out.println("Delta = " + delta);

				utilidadMax = -10000.0;
			}

			utilities.putAll(utilitiesCurrent); // vuelcas utilitiesCurrent en utilities y repites el proceso si no se a alcanzado la convergencia.

		} while (delta > (maxDelta * ((1.0 - problem.gamma) / problem.gamma))); // se alcance el error mínimo.

		Action best = null;
		Double max = -10000.0;
		Double utility;
		//Aquí ya tenemos las utilities de los estados, ahora falta obtener la politica optima (A,C,D,A...).
		// BASICAMENTE OBTENEMOS LA MEJOR UTILIDAD PARA EL ESTADO, CON LA MEJOR ACCION.
		for (State estado : ((MDPLearningProblem) problem).getAllStates()) {
			for (Action accion : problem.getPossibleActions(estado)) {
				utility = ((MDPLearningProblem) problem).getExpectedUtility(estado, accion, utilities, problem.gamma);
				if (utility > max) {
					best = accion;
					max = utility;
				}
			}

			solution.setAction(estado, best); //politica optima
			max = -10000.0;
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
