package it.polito.tdp.RisorsePerEsame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.RisorsePerEsame.dao.GOsalesDAO;

public class Model {
	
	private SimpleWeightedGraph<Products, DefaultWeightedEdge> grafo;
	private GOsalesDAO dao;
	private Map<Integer, Retailers> retailersIdMap;
	private List<Arco> cammino;
	
	public Model() {
		this.dao = new GOsalesDAO();
		this.retailersIdMap = new HashMap<Integer, Retailers>();
		
		List<Retailers> retailers = this.dao.getAllRetailers();
		for (Retailers r : retailers) {
			this.retailersIdMap.put(r.getCode(), r);
		}
	}
	
	
	public List<String> getColori(){
		return this.dao.getProductColors();
	}
	
	public void creaGrafo(Integer anno, String colore) {
		this.grafo = new SimpleWeightedGraph<Products, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//Vertici
		List<Products> vertici = this.dao.getVertici(colore);
		Graphs.addAllVertices(this.grafo, vertici);
		
		
		//Archi
		//Then, for each pair of vertices, we check the retailers that sold both products in the same day, 
		// and count of the unique dates of sales (unique across all retailers)
		for (int i =0; i < vertici.size(); i++) {
			Products v1 = vertici.get(i);
			for (int j =i+1; j < vertici.size(); j++) {
				Products v2 = vertici.get(j);	
				
				// if there are retailers in common, we count the number of distinct sales of the two products and add the edge
				int count = this.dao.countDaysSalesInCommonFromRetailerv2(v1, v2, anno);
				if (count > 0) {
					Graphs.addEdgeWithVertices(this.grafo, v1, v2, count);
				}
			}
		}
	}
	
	
	public int getNVertici(){
		return this.grafo.vertexSet().size();
	}
	
	public Set<Products> getVertici(){
		return this.grafo.vertexSet();
	}
	
	public int getNArchi(){
		return this.grafo.edgeSet().size();
	}
	
	public List<Arco> getTop3Archi(){
		List<Arco> archi = new ArrayList<Arco>();
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			archi.add(new Arco(this.grafo.getEdgeSource(e), this.grafo.getEdgeTarget(e), (int)this.grafo.getEdgeWeight(e) ));
		}
		Collections.sort(archi);
		int N = Math.min(3, archi.size());
		return archi.subList(0, N);
	}
	
	public List<Set<Products>> calcolaComponentiConnesse(){
		ConnectivityInspector<Products,DefaultWeightedEdge> inspect = new ConnectivityInspector<Products,DefaultWeightedEdge>(this.grafo);
		return inspect.connectedSets();
		
	}
	
	public Set<Products> getNodiRipetutiInArchi(List<Arco> archi){
		Set<Products> nodi_presenti = new HashSet<Products>();
    	Set<Products> nodi_ripetuti = new HashSet<Products>();
    	
		for(Arco a: archi) {
			Products v1 = a.getV1();
			Products v2 = a.getV2();
			if (nodi_presenti.contains(v1)) {
				nodi_ripetuti.add(v1);
			} else {
				nodi_presenti.add(v1);
			}
			if (nodi_presenti.contains(v2)) {
				nodi_ripetuti.add(v2);
			} else {
				nodi_presenti.add(v2);
			}	
		}
		return nodi_ripetuti;
	}
	
	
	
	public List<Arco> trovaPercorso(Products start){
		this.cammino = new ArrayList<Arco>();
		
		List<Products> parziale_vertici = new ArrayList<Products>();
		parziale_vertici.add(start);
		List<Arco> parziale_arco = new ArrayList<Arco>();
		List<Arco> archi_rimanenti_connessi = calcolaArchiConnessiCrescenti(start, parziale_arco);

		ricorsione(parziale_vertici, parziale_arco, archi_rimanenti_connessi);
		return this.cammino;
	}
	
	
	// RICORSIONE COMPLICATA
	
	private void ricorsione(List<Products> parziale_vertici, List<Arco> parziale_arco, List<Arco> archi_rimanenti_connessi) {
		//condizione di terminazione: non ci sono più archi potenziali da controllare
		if(archi_rimanenti_connessi.isEmpty()) {
			if(parziale_arco.size()>this.cammino.size()) {
				this.cammino = new ArrayList<Arco>(parziale_arco);
			}
		}
		
		// altrimenti, vado avanti ad aggiungere archi. prendo l'ultimo vertice nel cammino parziale,
		// e guardo tra gli archi rimanenti se ce ne sono con quel vertice come estremo e se rispetta la condizione sul 
		// peso crescente
		for (Arco a : archi_rimanenti_connessi) {
			//aggiunta
			parziale_arco.add(a);
			parziale_vertici.add(a.getV2());
				
			//ricomputo i nuovi archi rimanenti e connessi
			Products lastVertex = parziale_vertici.get(parziale_vertici.size()-1);
			List<Arco> nuovi_archi_rimanenti_connessi = calcolaArchiConnessiCrescenti(lastVertex, parziale_arco);
				
			//ricorsione
			ricorsione(parziale_vertici, parziale_arco, nuovi_archi_rimanenti_connessi);
				
			//backtracking
			parziale_vertici.remove(parziale_vertici.size()-1);
			parziale_arco.remove(parziale_arco.size()-1);
		}
	}
	
	
	
	private boolean isConnected(DefaultWeightedEdge e, Products vertice) {
		boolean result = false;
		if (this.grafo.getEdgeSource(e).equals(vertice) || this.grafo.getEdgeTarget(e).equals(vertice) ) {
			result = true;
		}
		return result;
	}
	
	private boolean isNotDecreasing(Arco a, List<Arco> parziale) {
		//caso in cui siamo al primo arco.
		if(parziale.isEmpty()) {
			return true;
		}
		
		boolean result = false;
		int lastPeso = parziale.get(parziale.size()-1).getPeso();
		if (a.getPeso()>= lastPeso ) {
			result = true;
		}
		return result;
	}
	
	
	private boolean isNotAlreadyTraversed(Arco a, List<Arco> parziale) {
		boolean result = false;
		Arco inverso = new Arco(a.getV2(), a.getV1(), a.getPeso());
		if (!parziale.contains(a) && !parziale.contains(inverso) ) {
			result = true;
		}
		return result;
	}
	
	
	private List<Arco> calcolaArchiConnessiCrescenti(Products start, List<Arco> parziale_arco){
		List<Arco> archi_rimanenti_connessi = new ArrayList<Arco>();
		
		for(DefaultWeightedEdge e: this.grafo.edgeSet()) {
			//prima verifico se l'arco è connesso al cammino, altrimenti non lo considero
			if( isConnected(e,start)){
				Products end;
				if (this.grafo.getEdgeTarget(e).equals(start)) {
					end = this.grafo.getEdgeSource(e);
				} else {
					end = this.grafo.getEdgeTarget(e);
				}
				Arco a = new Arco(start, end, (int)this.grafo.getEdgeWeight(e));
					
				//adesso faccio i controlli per vedere se l'arco non sia decrescente e che non sia già stato percorso.
				// Qualora ancora non abbiamo nessun arco in parziale (primo step) allora questo controllo non
				// deve essere fatto 
				if (parziale_arco.isEmpty()) {
					archi_rimanenti_connessi.add(a);
				} else {
					// verifico se non è decrescente e se non è già contenuto
					if ( isNotDecreasing(a, parziale_arco) && isNotAlreadyTraversed(a, parziale_arco)) {
						archi_rimanenti_connessi.add(new Arco(start, end, (int)this.grafo.getEdgeWeight(e) ));
					}
				}
					
					
				}
			}
		return archi_rimanenti_connessi;
	}
	/*
	 * 	RICERCA A LIVELLI: SOLO COMPONENTE INIZIALE
	 * 
	public Set<Album> ricercaSetMassimo(Album a1, double dTot) {
		
			if(a1.getDurata()>dTot) {
				return null ;
			}
		
		List<Album> parziale = new ArrayList<>() ;
		parziale.add(a1) ;
		
		List<Album> tutti = new ArrayList<>(getComponente(a1)) ;
		tutti.remove(a1);
		
		dimMax = 1 ;
		setMax = new ArrayList<>(parziale) ;
		
		cerca(parziale, 0, 0.0, dTot-a1.getDurata(), tutti) ;
		
		Set<Album> result = new HashSet<>(setMax) ;
		result.add(a1) ;
		return  result ;
		
		}
	
	private void cerca(List<Album> parziale, int livello, double durataParziale, double dTot, List<Album> tutti) {
		
		if(parziale.size() > dimMax) {
			dimMax = parziale.size() ;
			setMax = new ArrayList<>(parziale) ;
		}
		
		for(Album nuovo: tutti) {
			if( (livello==0 || nuovo.getAlbumId()>parziale.get(parziale.size()-1).getAlbumId()) && 
					durataParziale+nuovo.getDurata()<=dTot ) {
				parziale.add(nuovo) ;
				cerca(parziale, livello+1, durataParziale+nuovo.getDurata(), dTot, tutti) ;
				parziale.remove(parziale.size()-1) ;
			}
		}
	}
	 */
	/*
	 * RICERCA CON TARGET : INIZIO-FINE 
	 * 
	public List<Album> getPath(Album source, Album target, int threshold) {
		List<Album> parziale = new ArrayList<>();
		this.bestPath = new ArrayList<>();
		this.bestScore = 0;
		parziale.add(source);
		
		ricorsione(parziale, target, threshold );
		
		return this.bestPath;
	}
	private void ricorsione(List<Album> parziale, Album target, int threshold) {
		Album current = parziale.get(parziale.size()-1);
		
		//condizione di uscita
		if(current.equals(target)) {
			//controllo se questa soluzione è migliore del best 
			if ( getScore(parziale) > this.bestScore ) {
				this.bestScore = getScore(parziale);
				this.bestPath = new ArrayList<>(parziale);
			}
			return;
		}
			
		//continuo ad aggiungere elementi in parziale
		List<Album> successors = Graphs.successorListOf(this.graph, current);
		
		for (Album a : successors) {
			if( this.graph.getEdgeWeight(this.graph.getEdge(current, a)) >= threshold && !parziale.contains(a)) {
				parziale.add(a);
				ricorsione(parziale, target, threshold);
				parziale.remove(a); //backtracking
			}
		}
	}
	private int getScore(List<Album> parziale) {
		int score = 0;
		Album source = parziale.get(0);
		
		for (Album a : parziale.subList(1, parziale.size()-1)) {
			if (getBilancio(a) > getBilancio(source))
				score += 1;
		}
		
		return score;
		
	}

	 */
	
	
}
