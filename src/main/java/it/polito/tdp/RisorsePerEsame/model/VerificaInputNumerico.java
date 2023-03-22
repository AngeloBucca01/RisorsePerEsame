package it.polito.tdp.RisorsePerEsame.model;

public class VerificaInputNumerico {
	public void verificaNumerica(String testo) {
    	String input= testo; 	// solitamente da inserire all'interno del file FXMLController insieme ad un txtArea.getText() o txtField.getText()
    	int inputNumerico=0;
    	try {
        	inputNumerico= Integer.parseInt(input);
    	} catch(NumberFormatException e) {
    		return;
    	}
    	if(inputNumerico<0 || inputNumerico>2) { //condizioni generiche sul numero stesso
    		
    	}

    }
}