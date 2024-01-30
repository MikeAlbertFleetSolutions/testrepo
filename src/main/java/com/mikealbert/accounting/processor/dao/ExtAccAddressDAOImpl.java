package com.mikealbert.accounting.processor.dao;

import javax.persistence.Query;

import com.mikealbert.accounting.processor.entity.ExtAccAddress;
import com.mikealbert.util.data.DataUtil;

public class ExtAccAddressDAOImpl extends GenericDAOImpl<ExtAccAddress, Long> implements ExtAccAddressDAOCustom{
	private static final long serialVersionUID = -4811203493132417828L;

	@Override
	public boolean isLinkedToVehicleMovement(Long eaaId) {
		StringBuilder stmt = new StringBuilder();
		stmt.append("SELECT decode(count(1), 0, 'N', 'Y') ");
		stmt.append("  FROM veh_movement_addr_links ");
		stmt.append("  WHERE eaa_eaa_id = :eaaId ");	

		Query query = entityManager.createNativeQuery(stmt.toString());
		query.setParameter("eaaId", eaaId);
		
		boolean hasMovement = DataUtil.convertToBoolean((String)query.getSingleResult());
				
		return hasMovement;
	}

}
