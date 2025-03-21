package com.example.backend.service;

import com.example.backend.model.Bid;
import com.example.backend.model.Tender;
import com.example.backend.repository.TenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class TenderService {

    @Autowired
    private TenderRepository tenderRepository;

    public List<Tender> getAllTenders() {
        return tenderRepository.findAll();
    }

    public Optional<Tender> getTenderById(String id) {
        return tenderRepository.findById(id);
    }

    public List<Tender> getTendersByClientId(String clientId) {
        return tenderRepository.findByClientId(clientId);
    }

    public List<Tender> getTendersByStatus(String status) {
        return tenderRepository.findByStatus(status);
    }

    public List<Tender> getTendersByCompanyBids(String companyId) {
        return tenderRepository.findByBidsCompanyId(companyId);
    }

    public Tender createTender(Tender tender) {
        tender.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        tender.setStatus("new");
        tender.setBidsCount(0);
        return tenderRepository.save(tender);
    }

    public Tender updateTender(String id, Tender tenderDetails) {
        return tenderRepository.findById(id)
                .map(tender -> {
                    tender.setTitle(tenderDetails.getTitle());
                    tender.setDescription(tenderDetails.getDescription());
                    tender.setBudget(tenderDetails.getBudget());
                    tender.setDeadline(tenderDetails.getDeadline());
                    tender.setStatus(tenderDetails.getStatus());
                    return tenderRepository.save(tender);
                })
                .orElseThrow(() -> new RuntimeException("Tender not found with id " + id));
    }

    public Tender addBidToTender(String tenderId, Bid bid) {
        return tenderRepository.findById(tenderId)
                .map(tender -> {
                    List<Bid> bids = tender.getBids();
                    bid.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
                    bid.setStatus("pending");
                    bids.add(bid);
                    tender.setBids(bids);
                    tender.setBidsCount(bids.size());

                    // Update lowest bid if applicable
                    if (tender.getLowestBid() == null || bid.getAmount() < tender.getLowestBid()) {
                        tender.setLowestBid(bid.getAmount());
                    }

                    return tenderRepository.save(tender);
                })
                .orElseThrow(() -> new RuntimeException("Tender not found with id " + tenderId));
    }

    public void deleteTender(String id) {
        tenderRepository.deleteById(id);
    }
}