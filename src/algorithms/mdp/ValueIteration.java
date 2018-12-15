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

		//--- Atributos necesarios ----
		double delta = 0.0; //cambio + significativo de una iteración a otra.
		Collection<State> estados = problem.getAllStates(); //estados=todos los estados del problema.
		double utilidadMax = Double.NEGATIVE_INFINITY; //utilidadMax = - infinito.
		double sumatorio = 0; //lo usaremos para sumar las (prob*utilidad) desde un estado y accion concreta.
		Action maxAccion = null; //accion con la que nos vamos a quedar, para dar el resultado.
		utilities = new HashMap<State, Double>(); //hashmap con utilidades anteriores.
		utilitiesCurrent = new HashMap<State, Double>(); //hashmap con utilidades actuales.
		//------ALGORITMO---------------
		//recorremos los estados si no se ha llegado a un estado final, inicializamos todas las utilidades a 0.
		//y si es final se añaden las recompensas a al hasMap de utilidades de los estados.
		for (State state : estados) {
			if (!problem.isFinal(state)) {
				utilities.put(state, 0.0);
			}else {
				utilities.put(state, problem.getReward(state));
			}
		}

		while (delta < maxDelta * (1 - problem.gamma) / (problem.gamma)) { //bucle hasta llegar a convergir con maxdelta = 0.01
			estados = problem.getAllStates();
			sumatorio=0;
			
			for (State state : estados) { //para cada uno de los estados, generamos sus acciones y por cada accion se calcula 
										//la suma de de las (prob*utilidad anterior para dicho estado) y obtiene
										//la utilidad y acción maximas.
				
				if (!problem.isFinal(state)) {
					utilidadMax = Double.NEGATIVE_INFINITY;
					
					for (Action action : problem.getPossibleActions(state)) {
						//System.out.println(sumatorio+"  "+utilidadMax);
						sumatorio = (((MDPLearningProblem) problem).getExpectedUtility(state, action, utilities,
								problem.gamma));
						
						if (sumatorio > utilidadMax) { 
							utilidadMax = sumatorio;
							maxAccion = action;
						}
						

					}

					solution.setAction(state, maxAccion); //asignamos la accion maxima que le corresponde al estado 'x'
					utilitiesCurrent.put(state, utilidadMax); //asignamos en el hashmap de utilidades actuales, a cada estado la utilidad max.

					if (Math.abs(utilitiesCurrent.get(state) - utilities.get(state)) > delta) {
						delta = Math.abs(utilitiesCurrent.get(state) - utilities.get(state)); //nos quedamos con el mayor factor de cambio en dicha iteración.
					}
				}
			}

			utilities = utilitiesCurrent; //para la ultima iteración, volcamos las ultimas utilidades obtenidas (actualizando el hashmap utilities).
			
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
