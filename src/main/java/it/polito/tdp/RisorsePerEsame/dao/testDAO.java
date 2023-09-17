package it.polito.tdp.RisorsePerEsame.dao;

import it.polito.tdp.RisorsePerEsame.model.Model;

public class testDAO {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GOsalesDAO dao = new GOsalesDAO();
		Model model = new Model();
		model.creaGrafo(2015, "Blue");
		System.out.println(dao.getRetailerTypes().size());
		System.out.println(dao.getRetailerCountries().size());
		System.out.println(dao.getMethods().size());
		System.out.println(dao.getYears().size());
		System.out.println(dao.getRetailersByQuantitySoldPerYear(2015, 25).size());
		System.out.println(model.calcolaComponentiConnesse());

		
		
	}

}
