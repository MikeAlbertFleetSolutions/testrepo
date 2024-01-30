package com.mikealbert.accounting.processor.vo;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NgAssetsPerUnitVO {
	
	@JsonProperty(value = "unitNo")
	private String unitNo;
	
	@JsonProperty(value = "listNgAssetForUnit")
	private List<NgAssetVO> listNgAssetVO;

	public NgAssetsPerUnitVO() {
		this.listNgAssetVO = new ArrayList<NgAssetVO>();
	}
	
	public String getUnitNo() {
		return unitNo;
	}

	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	public List<NgAssetVO> getListNgAssetVO() {
		return listNgAssetVO;
	}

	public void setListNgAssetVO(List<NgAssetVO> listNgAssetVO) {
		this.listNgAssetVO = listNgAssetVO;
	}

}
