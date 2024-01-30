package com.mikealbert.accounting.processor.client.quote;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mikealbert.accounting.processor.service.BaseService;
import com.mikealbert.accounting.processor.vo.CapitalCostVO;

@Service("thisQuoteCostService")
public class ThisQuoteCostServiceImpl extends BaseService implements ThisQuoteCostService {
    @Resource RestTemplate restTemplate;
    
    @Value("${mafs.endpoints.quote.this-quote-costs}")
    protected String thisQuoteCostsURL;

    static final String TOKEN_QMD_ID = ":qmdId";
    
    @Override
    public CapitalCostVO thisQuoteCost(Long qmdId) throws Exception {
      String URL = thisQuoteCostsURL.replace(TOKEN_QMD_ID, qmdId.toString());
      
      return restTemplate.getForObject(URL, CapitalCostVO.class);
    }
    
}
