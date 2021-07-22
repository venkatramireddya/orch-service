/*package com.ttech.orch.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.pepsi.assets.controller.AssetsStockController;
import com.pepsi.assets.model.AssetsDetails;

	@Repository
	public class AssetsStockDao {

		private static Logger logger = LoggerFactory.getLogger(AssetsStockController.class.getName());
		
		@Autowired
		JdbcTemplate jdbcTemplate;
		@Cacheable(value = "ticketCache", key = "#userId", unless = "#result==null" )
		public List<AssetsDetails> getAssetsDetails(String userId) throws Exception {
			
			logger.info("ASSETS STOCK-SERVICE : getAssetsDetails");
			
			ArrayList<AssetsDetails> details = new ArrayList<>();

			final List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM ALL_USERS where USER_ID =" +userId);
			rows.stream().forEach(rs -> details.add(mapAssetsDetails(rs)));

			return details;
		}

		public AssetsDetails mapAssetsDetails(Map<String, Object> results) {
			AssetsDetails assetsDetails = new AssetsDetails();
			assetsDetails.setAssetName((String) results.get("USERNAME"));
			assetsDetails.setAssetId((String)results.get("USER_ID"));
			return assetsDetails;
		}
	}*/

