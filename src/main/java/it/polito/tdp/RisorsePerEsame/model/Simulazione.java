package it.polito.tdp.RisorsePerEsame.model;

public class Simulazione {
	/*
	// MODELLO DEL MONDO
	private List<People> listaGiocatoriSquadra;
	private List<Integer> anniAnalizzare;
	// PARAMETRI DI INGRESSO
	private BaseballDAO dao;
	private String squadra;
	private Integer annoInizio;
	private Integer numeroTifosiIniziali;
	// PARAMETRI IN USCITA
	private Integer numeroTifosiPersi;
	private List<People> tifosiFinaliGiocatori; 
	// TIPI DI EVENTI E CODA DEGLI EVENTI
	public PriorityQueue<Event> queue;
	
	public Simulator(BaseballDAO dao, String squadra, Integer annoInizio, Integer numeroTifosiIniziali) {
		super();
		this.dao = dao;
		this.squadra = squadra;
		this.annoInizio = annoInizio;
		this.numeroTifosiIniziali = numeroTifosiIniziali;
	}
	public void inizialize() {
		this.listaGiocatoriSquadra = new ArrayList<People>();
		this.anniAnalizzare = new ArrayList<Integer>();
		this.queue = new PriorityQueue<Event>();
		this.tifosiFinaliGiocatori = new ArrayList<People>();
		this.numeroTifosiPersi=0;
		this.listaGiocatoriSquadra.addAll(this.dao.getPlayersTeamYear(this.squadra, this.annoInizio));
		this.anniAnalizzare.addAll(this.dao.getAnni(squadra, annoInizio));
		for(People giocatore : this.listaGiocatoriSquadra) {
			giocatore.setTifosi((int) numeroTifosiIniziali/ this.listaGiocatoriSquadra.size());
		}
		for(Integer anno : anniAnalizzare) {
			List<People> squadraAnno = this.dao.getPlayersTeamYear(squadra, anno);
			for(People giocatore : this.listaGiocatoriSquadra) {
				for(int i = 0; i< giocatore.getTifosi();i++) {
					if(dao.getPlayer(giocatore.getPlayerID(), anno).isEmpty()) {
						double random1 = Math.random();
						if( random1 <= 0.1) {
							queue.add(new Event(anno,giocatore,EventType.tifosoCambiaSquadra));
						}
						else {
							double random2 = Math.random();
							if(random2 <= 0.25) {
							queue.add(new Event(anno,giocatore,EventType.tifosoCambiaIdeaGiocatoreVecchio));
							}
							else {
								queue.add(new Event(anno,giocatore,EventType.tifosoCambiaIdeaGiocatoreNuovo));
							}
						}
					}
				
					else {
						double random = Math.random();
						if(random <= 0.1) {
							queue.add(new Event(anno,giocatore,EventType.tifosoCambiaIdeaGiocatoreAnnoSuccessivo));

						}
					
						else {
							queue.add(new Event(anno,giocatore,EventType.tifosoSoddisfatto));
						}
					}
				}
			}
			this.run();
			this.listaGiocatoriSquadra = squadraAnno;
		}
		this.tifosiFinaliGiocatori= this.listaGiocatoriSquadra;
	}
	public void run() {
		while(! this.queue.isEmpty()) {
			Event e = this.queue.poll();
			People g = e.getGiocatore();
			Integer a = e.getAnno();
			EventType event = e.getEvento();
			switch(event) {
			case tifosoSoddisfatto : 
				break;
			case tifosoCambiaIdeaGiocatoreAnnoSuccessivo :
				g.setTifosi(g.getTifosi()-1);
				List<People> squadraFiltrataAnnoSuccessivo = this.dao.getPlayersTeamYear(squadra, a);
				squadraFiltrataAnnoSuccessivo.remove(g);
				int randomAS = (int)Math.floor(Math.random() *(squadraFiltrataAnnoSuccessivo.size()));
				squadraFiltrataAnnoSuccessivo.get(randomAS).setTifosi(squadraFiltrataAnnoSuccessivo.get(randomAS).getTifosi() + 1);
				break;
			case tifosoCambiaIdeaGiocatoreNuovo :
				g.setTifosi(g.getTifosi()-1);
				List<People> squadraFiltrataGiocatoreNuovo = this.dao.getPlayersTeamYear(squadra, a);
				List<People> giocatoriNuovi = new ArrayList<People>();
				squadraFiltrataGiocatoreNuovo.remove(g);
				for(People p : squadraFiltrataGiocatoreNuovo) {
					if(!this.dao.getPlayer(p.getPlayerID(), a-1).isEmpty()) {
						giocatoriNuovi.add(p);
					}
				}
				squadraFiltrataGiocatoreNuovo.removeAll(giocatoriNuovi);
				int randomGN = (int)Math.floor(Math.random() *(squadraFiltrataGiocatoreNuovo.size()));
				squadraFiltrataGiocatoreNuovo.get(randomGN).setTifosi(squadraFiltrataGiocatoreNuovo.get(randomGN).getTifosi() + 1);
				break;
			case tifosoCambiaIdeaGiocatoreVecchio :
				g.setTifosi(g.getTifosi()-1);
				List<People> squadraFiltrataGiocatoreVecchio = this.dao.getPlayersTeamYear(squadra, a);
				List<People> giocatoriVecchi = new ArrayList<People>();
				squadraFiltrataGiocatoreVecchio.remove(g);
				for(People p : squadraFiltrataGiocatoreVecchio) {
					if(this.dao.getPlayer(p.getPlayerID(), a-1).isEmpty()) {
						giocatoriVecchi.add(p);
					}
				}
				squadraFiltrataGiocatoreVecchio.removeAll(giocatoriVecchi);
				int randomGV = (int)Math.floor(Math.random() *(squadraFiltrataGiocatoreVecchio.size()));
				squadraFiltrataGiocatoreVecchio.get(randomGV).setTifosi(squadraFiltrataGiocatoreVecchio.get(randomGV).getTifosi() + 1);
				break;
			case tifosoCambiaSquadra:
				g.setTifosi(g.getTifosi()-1);
				this.numeroTifosiPersi = this.numeroTifosiPersi + 1 ;
			}
		}
	}
	public Integer getNumeroTifosiPersi() {
		return numeroTifosiPersi;
	}
	
	public List<Double> getTifosiFinaliGiocatori() {
		List<Double> result = new ArrayList<Double>();
		for(People p : tifosiFinaliGiocatori) {
			result.add((double) p.getTifosi());
		}
		return result;
	}
	public PriorityQueue<Event> getQueue() {
		return queue;
	}
	public List<Integer> getAnniAnalizzare() {
		return anniAnalizzare;
	}
	*/
}
