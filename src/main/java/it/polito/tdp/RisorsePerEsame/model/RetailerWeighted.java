package it.polito.tdp.RisorsePerEsame.model;

import java.util.Objects;

public class RetailerWeighted  implements Comparable<RetailerWeighted> {
	private Integer retailerCode;
	private Integer peso;
	public RetailerWeighted(Integer retailerCode, Integer peso) {
		super();
		this.retailerCode = retailerCode;
		this.peso = peso;
	}
	public Integer getPeso() {
		return peso;
	}
	public void setPeso(Integer peso) {
		this.peso = peso;
	}
	public Integer getRetailerCode() {
		return retailerCode;
	}
	@Override
	public int hashCode() {
		return Objects.hash(peso, retailerCode);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RetailerWeighted other = (RetailerWeighted) obj;
		return Objects.equals(peso, other.peso) && Objects.equals(retailerCode, other.retailerCode);
	}
	@Override
	public String toString() {
		return "RetailersWeighted [retailerCode=" + retailerCode + ", peso=" + peso + "]";
	}
	@Override
	public int compareTo(RetailerWeighted other) {
		
		return this.peso-other.peso;
	}
	
}
