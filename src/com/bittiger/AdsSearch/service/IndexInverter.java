package com.bittiger.AdsSearch.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bittiger.AdsSearch.model.Ad;
import com.bittiger.AdsSearch.repository.AdDao;

@Service
public class IndexInverter {
    
    @Autowired
    private AdDao adDao;
    
    private Map<String, Set<Ad>> invertedIndex;
    
    public IndexInverter() {
        this.invertedIndex = new HashMap<>();
    }
    
    public void loadToMap(List<Ad> ads) {
        ads.stream()
            .forEach((ad) -> {
                this.processSingleAd(ad);
            }); 
    }
    
    @PostConstruct
    public void process() {
        List<Ad> allAds = adDao.findAllAds();
        
        this.loadToMap(allAds);
    }
    
    private void processSingleAd(Ad ad) {
        ad.getTokens()
            .stream()
            .map(token -> token.toLowerCase())
            .forEach(loweredToken -> {
                if (!this.invertedIndex.containsKey(loweredToken)) {
                    this.invertedIndex.put(loweredToken.toLowerCase(), new HashSet<Ad> ());
                }
                
                invertedIndex.get(loweredToken).add(ad);
            });
    }
}
