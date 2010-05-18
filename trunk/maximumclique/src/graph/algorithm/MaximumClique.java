package graph.algorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;

/**
 * 
 * @author bbarczynski
 * 
 * @param <V>
 * @param <E>
 */
public class MaximumClique<V, E> {
	private Graph<V, E> graph;
	private Map<Integer, V> nodeMap;

	private List<Set<V>> maximalCliques;

	public MaximumClique(Graph<V, E> graph) {
		this.graph = graph;
		maximalCliques = new LinkedList<Set<V>>();
		// index nodes for fast searching
		Collection<V> vertices = graph.getVertices();
		nodeMap = new HashMap<Integer, V>(graph.getVertexCount());
		Integer i = 1;
		for (V v : vertices) {
			nodeMap.put(i++, v);
		}
	}

	public List<Set<V>> getCliques() {
		maximalCliques.clear();
		TIAS(new HashSet<V>(), 1);
		sortMaximalCliques();
		printAllMaximalCliques();
		removeNoMaximumCliques();
		return maximalCliques;
	}

	private void TIAS(HashSet<V> maximalClique, Integer nodeIndex) {

		if (nodeIndex > graph.getVertexCount()) {
			System.out.println("!!! Znaleziono masymalna kilke : " + printClique(maximalClique));
			// TODO [bbarczynski] sprawdzenie czy dodajemy druga taka sama klike
			// maksylana
			maximalCliques.add(maximalClique);
			return;
		}
		V node = nodeMap.get(nodeIndex);
		System.out.println(getPrefix(node) + "maximalClique=" + maximalClique.toString());
		Collection<V> neighbors = graph.getNeighbors(node);
		if (neighbors.containsAll(maximalClique)) {
			System.out.println(getPrefix(node) + "maximalClique zawiera sie w neighbors");
			TIAS(cloneSetAndAddNode(maximalClique, node), nodeIndex + 1);
		} else {
			System.out.println(getPrefix(node) + "maximalClique - brak zawierania");
			TIAS(cloneSet(maximalClique), nodeIndex + 1);

			System.out.println(getPrefix(node) + "maximalClique=" + maximalClique);
			System.out.println(getPrefix(node) + "neighbors=" + neighbors);
			HashSet<V> intersection = getIntersection(neighbors, maximalClique);
			System.out.println(getPrefix(node) + "intersection=" + intersection);

			Collection<V> lexiClique = getLexicographicallySmallestMaximalClique(intersection, nodeIndex - 1);
			System.out.println(getPrefix(node) + "lexiClique=" + lexiClique);
			if (maximalClique.equals(lexiClique)) {
				System.out.println(getPrefix(node)
						+ "lexiClique jest rowne maximalClique. Rekurencja zostaje rozdwojona.");
				TIAS(cloneSetAndAddNode(intersection, node), nodeIndex + 1);
			} else {
				System.out.println(getPrefix(node) + "lexiClique jest rozne od maximalClique ");
			}

		}

	}

	/**
	 * Metoda kopiuje zbior wejsciowy i dodaje do niego nowy element
	 * 
	 * @param set
	 * @param node
	 * @return
	 */
	private HashSet<V> cloneSetAndAddNode(Collection<V> set, V node) {
		HashSet<V> clone = new HashSet<V>(set);
		clone.add(node);
		return clone;
	}

	/**
	 * Metoda kopiuje zbior wejsciowy
	 * 
	 * @param set
	 * @return
	 */
	private HashSet<V> cloneSet(Collection<V> set) {
		return new HashSet<V>(set);
	}

	/**
	 * Metoda znajduje czesc wspolna 2 zbiorow.
	 * 
	 * @param set1
	 * @param set2
	 * @return
	 */
	private HashSet<V> getIntersection(Collection<V> set1, Collection<V> set2) {
		HashSet<V> cloneSet = cloneSet(set1);
		cloneSet.retainAll(set2);
		return cloneSet;
	}

	/**
	 * Metoda zwraca najmniejszą leksygograficznie (czyli sortowanie rosnace
	 * wierzcholkow) klike maksymalna, ktora zawiera klike wejsciowa. Metoda
	 * probuje dodawac do clique kolejne wiercholka i spr czy clique ciagle
	 * bedzie klika
	 * 
	 * @param clique
	 *            klika poczatkowa
	 * @param maxIndex
	 *            numer ostatniego wierzcholka, ktory mozna dolaczyc
	 * @return
	 */
	private HashSet<V> getLexicographicallySmallestMaximalClique(Collection<V> clique, Integer maxIndex) {
		HashSet<V> lexiClique = cloneSet(clique);
		for (int i = 1; i <= maxIndex; ++i) {
			V node = nodeMap.get(i);
			Collection<V> neighbors = graph.getNeighbors(node);
			// mozna dolaczyc nowy wierzcholek gdy sasiedzi Vi zawieraja
			// wszystkie wierzcholki z kliki. Czyli po dolaczenieu Vi ciagle
			// bedzie istniala klika
			if (neighbors.containsAll(lexiClique))
				lexiClique.add(node);// powiekszamy klike
		}
		return lexiClique;
	}

	private String getPrefix(V node) {
		return node.toString() + ": ";
	}

	private void printAllMaximalCliques() {
		System.out.println("Kliki maksymalne:");
		for (Collection<V> element : maximalCliques) {
			System.out.println(printClique(element));
		}
	}

	public String printClique(Collection<V> clique) {
		return "(" + clique.size() + ")" + clique.toString();
	}

	private void sortMaximalCliques() {
		Collections.sort(maximalCliques, new Comparator<Collection<V>>() {

			@Override
			public int compare(Collection<V> o1, Collection<V> o2) {
				return o2.size() - o1.size();
			}
		});
	}

	private void removeNoMaximumCliques() {
		int firstSize = maximalCliques.get(0).size();
		for (int i = maximalCliques.size() - 1; i >= 0; --i) {
			if (maximalCliques.get(i).size() < firstSize)
				maximalCliques.remove(i);
		}
	}
}
