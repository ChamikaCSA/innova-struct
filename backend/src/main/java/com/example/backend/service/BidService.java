package com.example.backend.service;

import com.example.backend.model.Bid;
import com.example.backend.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

    public Optional<Bid> getBidById(String id) {
        return bidRepository.findById(id);
    }

    public List<Bid> getBidsByCompanyId(String companyId) {
        return bidRepository.findByCompanyId(companyId);
    }

    public List<Bid> getBidsByStatus(String status) {
        return bidRepository.findByStatus(status);
    }

    public Bid createBid(Bid bid) {
        bid.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        bid.setStatus("pending");
        return bidRepository.save(bid);
    }

    public Bid updateBidStatus(String id, String status) {
        return bidRepository.findById(id)
                .map(bid -> {
                    bid.setStatus(status);
                    return bidRepository.save(bid);
                })
                .orElseThrow(() -> new RuntimeException("Bid not found with id " + id));
    }

    public void deleteBid(String id) {
        bidRepository.deleteById(id);
    }
}