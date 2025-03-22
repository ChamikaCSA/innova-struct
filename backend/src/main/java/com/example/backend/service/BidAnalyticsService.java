package com.example.backend.service;

import com.example.backend.model.Bid;
import com.example.backend.model.Tender;
import com.example.backend.repository.BidRepository;
import com.example.backend.repository.TenderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BidAnalyticsService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private TenderRepository tenderRepository;

    /**
     * Get success rate for a company over a period
     * @param companyId The company ID
     * @param months Number of months to look back
     * @return Map with month labels and success rate percentages
     */
    public Map<String, Object> getBidSuccessRate(String companyId, int months) {
        List<Bid> companyBids = bidRepository.findByCompanyId(companyId);

        // Get current date and calculate start date based on months parameter
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months);

        // Filter bids by date range
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        List<Bid> filteredBids = companyBids.stream()
                .filter(bid -> {
                    LocalDateTime bidDate = LocalDateTime.parse(bid.getCreatedAt(), formatter);
                    return !bidDate.isBefore(startDate) && !bidDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        // Group bids by month
        Map<String, List<Bid>> bidsByMonth = new LinkedHashMap<>();

        // Initialize months
        for (int i = 0; i < months; i++) {
            LocalDateTime date = endDate.minusMonths(months - 1 - i);
            String monthLabel = date.format(DateTimeFormatter.ofPattern("MMM"));
            bidsByMonth.put(monthLabel, new ArrayList<>());
        }

        // Populate bids by month
        for (Bid bid : filteredBids) {
            LocalDateTime bidDate = LocalDateTime.parse(bid.getCreatedAt(), formatter);
            String monthLabel = bidDate.format(DateTimeFormatter.ofPattern("MMM"));
            if (bidsByMonth.containsKey(monthLabel)) {
                bidsByMonth.get(monthLabel).add(bid);
            }
        }

        // Calculate success rate for each month
        List<String> labels = new ArrayList<>(bidsByMonth.keySet());
        List<Double> successRates = new ArrayList<>();

        for (String month : labels) {
            List<Bid> monthBids = bidsByMonth.get(month);
            if (monthBids.isEmpty()) {
                successRates.add(0.0);
            } else {
                long acceptedBids = monthBids.stream()
                        .filter(bid -> "accepted".equals(bid.getStatus()))
                        .count();
                double successRate = (double) acceptedBids / monthBids.size() * 100;
                successRates.add(successRate);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", successRates);
        return result;
    }

    /**
     * Get bid volume by month
     * @param companyId The company ID
     * @param months Number of months to look back
     * @return Map with month labels and bid counts
     */
    public Map<String, Object> getBidVolume(String companyId, int months) {
        List<Bid> companyBids = bidRepository.findByCompanyId(companyId);

        // Get current date and calculate start date based on months parameter
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months);

        // Filter bids by date range
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        List<Bid> filteredBids = companyBids.stream()
                .filter(bid -> {
                    LocalDateTime bidDate = LocalDateTime.parse(bid.getCreatedAt(), formatter);
                    return !bidDate.isBefore(startDate) && !bidDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        // Group bids by month
        Map<String, List<Bid>> bidsByMonth = new LinkedHashMap<>();

        // Initialize months
        for (int i = 0; i < months; i++) {
            LocalDateTime date = endDate.minusMonths(months - 1 - i);
            String monthLabel = date.format(DateTimeFormatter.ofPattern("MMM"));
            bidsByMonth.put(monthLabel, new ArrayList<>());
        }

        // Populate bids by month
        for (Bid bid : filteredBids) {
            LocalDateTime bidDate = LocalDateTime.parse(bid.getCreatedAt(), formatter);
            String monthLabel = bidDate.format(DateTimeFormatter.ofPattern("MMM"));
            if (bidsByMonth.containsKey(monthLabel)) {
                bidsByMonth.get(monthLabel).add(bid);
            }
        }

        // Count bids for each month
        List<String> labels = new ArrayList<>(bidsByMonth.keySet());
        List<Integer> bidCounts = new ArrayList<>();

        for (String month : labels) {
            bidCounts.add(bidsByMonth.get(month).size());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", bidCounts);
        return result;
    }

    /**
     * Get bid status distribution
     * @param companyId The company ID
     * @return Map with status labels and counts
     */
    public Map<String, Object> getBidDistribution(String companyId) {
        List<Bid> companyBids = bidRepository.findByCompanyId(companyId);

        long acceptedCount = companyBids.stream()
                .filter(bid -> "accepted".equals(bid.getStatus()))
                .count();

        long rejectedCount = companyBids.stream()
                .filter(bid -> "rejected".equals(bid.getStatus()))
                .count();

        long pendingCount = companyBids.stream()
                .filter(bid -> "pending".equals(bid.getStatus()))
                .count();

        List<String> labels = Arrays.asList("Won", "Lost", "Pending");
        List<Long> data = Arrays.asList(acceptedCount, rejectedCount, pendingCount);

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", data);
        return result;
    }

    /**
     * Get overall bid statistics
     * @param companyId The company ID
     * @return Map with various statistics
     */
    public Map<String, Object> getBidStatistics(String companyId) {
        List<Bid> companyBids = bidRepository.findByCompanyId(companyId);

        // Calculate success rate
        double successRate = 0;
        if (!companyBids.isEmpty()) {
            long acceptedBids = companyBids.stream()
                    .filter(bid -> "accepted".equals(bid.getStatus()))
                    .count();
            successRate = (double) acceptedBids / companyBids.size() * 100;
        }

        // Calculate average bid value
        OptionalDouble averageBidOpt = companyBids.stream()
                .mapToDouble(Bid::getAmount)
                .average();
        double averageBid = averageBidOpt.orElse(0);

        // Count total bids
        int totalBids = companyBids.size();

        // Count active (pending) bids
        long activeBids = companyBids.stream()
                .filter(bid -> "pending".equals(bid.getStatus()))
                .count();

        // Get previous period statistics for comparison
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        LocalDateTime twoMonthsAgo = now.minusMonths(2);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        // Current period bids
        List<Bid> currentPeriodBids = companyBids.stream()
                .filter(bid -> {
                    LocalDateTime bidDate = LocalDateTime.parse(bid.getCreatedAt(), formatter);
                    return bidDate.isAfter(oneMonthAgo) && bidDate.isBefore(now);
                })
                .collect(Collectors.toList());

        // Previous period bids
        List<Bid> previousPeriodBids = companyBids.stream()
                .filter(bid -> {
                    LocalDateTime bidDate = LocalDateTime.parse(bid.getCreatedAt(), formatter);
                    return bidDate.isAfter(twoMonthsAgo) && bidDate.isBefore(oneMonthAgo);
                })
                .collect(Collectors.toList());

        // Calculate success rate change
        double previousSuccessRate = 0;
        if (!previousPeriodBids.isEmpty()) {
            long previousAcceptedBids = previousPeriodBids.stream()
                    .filter(bid -> "accepted".equals(bid.getStatus()))
                    .count();
            previousSuccessRate = (double) previousAcceptedBids / previousPeriodBids.size() * 100;
        }
        double successRateChange = successRate - previousSuccessRate;

        // Calculate average bid value change
        OptionalDouble previousAverageBidOpt = previousPeriodBids.stream()
                .mapToDouble(Bid::getAmount)
                .average();
        double previousAverageBid = previousAverageBidOpt.orElse(0);
        double averageBidChange = previousAverageBid == 0 ? 0 : (averageBid - previousAverageBid) / previousAverageBid * 100;

        // Calculate total bids change
        int previousTotalBids = previousPeriodBids.size();
        double totalBidsChange = previousTotalBids == 0 ? 0 : (double) (totalBids - previousTotalBids) / previousTotalBids * 100;

        // Calculate active bids change
        long previousActiveBids = previousPeriodBids.stream()
                .filter(bid -> "pending".equals(bid.getStatus()))
                .count();
        double activeBidsChange = previousActiveBids == 0 ? 0 : (double) (activeBids - previousActiveBids) / previousActiveBids * 100;

        // Prepare result
        Map<String, Object> result = new HashMap<>();
        result.put("successRate", Math.round(successRate));
        result.put("successRateChange", Math.round(successRateChange));
        result.put("averageBid", Math.round(averageBid));
        result.put("averageBidChange", Math.round(averageBidChange));
        result.put("totalBids", totalBids);
        result.put("totalBidsChange", Math.round(totalBidsChange));
        result.put("activeBids", activeBids);
        result.put("activeBidsChange", Math.round(activeBidsChange));

        return result;
    }

    /**
     * Get performance metrics
     * @param companyId The company ID
     * @return Map with performance metrics
     */
    public Map<String, Object> getPerformanceMetrics(String companyId) {
        List<Bid> companyBids = bidRepository.findByCompanyId(companyId);

        // Calculate average response time (assuming createdAt is when the bid was created)
        // This is a placeholder - in a real system, you'd need to track when a tender was published
        double averageResponseTime = 2.5; // days

        // Calculate win rate by value
        double totalBidValue = companyBids.stream()
                .mapToDouble(Bid::getAmount)
                .sum();

        double wonBidValue = companyBids.stream()
                .filter(bid -> "accepted".equals(bid.getStatus()))
                .mapToDouble(Bid::getAmount)
                .sum();

        double winRateByValue = totalBidValue == 0 ? 0 : (wonBidValue / totalBidValue) * 100;

        // Competitive index (placeholder - in a real system, this would be more complex)
        double competitiveIndex = 8.5;

        // Average markup (placeholder - would need more data in a real system)
        double averageMarkup = 15.0;

        Map<String, Object> result = new HashMap<>();
        result.put("averageResponseTime", averageResponseTime);
        result.put("winRateByValue", Math.round(winRateByValue));
        result.put("competitiveIndex", competitiveIndex);
        result.put("averageMarkup", averageMarkup);

        return result;
    }

    /**
     * Get bid trends data by category
     * @param companyId The company ID
     * @param timeframe The timeframe to analyze (day, week, month, quarter, year)
     * @return Map with trend data categorized by bid amount ranges
     */
    public Map<String, Object> getBidTrends(String companyId, String timeframe) {
        List<Bid> companyBids = bidRepository.findByCompanyId(companyId);

        // Define the time period based on the timeframe parameter
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;

        switch (timeframe.toLowerCase()) {
            case "day":
                startDate = endDate.minusDays(30); // Last 30 days
                break;
            case "week":
                startDate = endDate.minusWeeks(12); // Last 12 weeks
                break;
            case "quarter":
                startDate = endDate.minusMonths(12); // Last 4 quarters (12 months)
                break;
            case "year":
                startDate = endDate.minusYears(3); // Last 3 years
                break;
            case "month":
            default:
                startDate = endDate.minusMonths(6); // Last 6 months (default)
                break;
        }

        // Filter bids by date range
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        List<Bid> filteredBids = companyBids.stream()
                .filter(bid -> {
                    LocalDateTime bidDate = LocalDateTime.parse(bid.getCreatedAt(), formatter);
                    return !bidDate.isBefore(startDate) && !bidDate.isAfter(endDate);
                })
                .collect(Collectors.toList());

        // Define bid amount categories
        Map<String, List<Bid>> bidsByCategory = new LinkedHashMap<>();
        bidsByCategory.put("Small (< $10K)", new ArrayList<>());
        bidsByCategory.put("Medium ($10K - $50K)", new ArrayList<>());
        bidsByCategory.put("Large ($50K - $100K)", new ArrayList<>());
        bidsByCategory.put("Very Large (> $100K)", new ArrayList<>());

        // Categorize bids by amount
        for (Bid bid : filteredBids) {
            double amount = bid.getAmount();
            if (amount < 10000) {
                bidsByCategory.get("Small (< $10K)").add(bid);
            } else if (amount < 50000) {
                bidsByCategory.get("Medium ($10K - $50K)").add(bid);
            } else if (amount < 100000) {
                bidsByCategory.get("Large ($50K - $100K)").add(bid);
            } else {
                bidsByCategory.get("Very Large (> $100K)").add(bid);
            }
        }

        // Calculate success rate for each category
        Map<String, Object> result = new HashMap<>();
        List<String> categories = new ArrayList<>(bidsByCategory.keySet());
        List<Integer> bidCounts = new ArrayList<>();
        List<Double> successRates = new ArrayList<>();
        List<Double> averageValues = new ArrayList<>();

        for (String category : categories) {
            List<Bid> categoryBids = bidsByCategory.get(category);
            bidCounts.add(categoryBids.size());

            if (categoryBids.isEmpty()) {
                successRates.add(0.0);
                averageValues.add(0.0);
            } else {
                // Calculate success rate
                long acceptedBids = categoryBids.stream()
                        .filter(bid -> "accepted".equals(bid.getStatus()))
                        .count();
                double successRate = (double) acceptedBids / categoryBids.size() * 100;
                successRates.add(successRate);

                // Calculate average bid value
                double averageValue = categoryBids.stream()
                        .mapToDouble(Bid::getAmount)
                        .average()
                        .orElse(0);
                averageValues.add(averageValue);
            }
        }

        result.put("categories", categories);
        result.put("bidCounts", bidCounts);
        result.put("successRates", successRates);
        result.put("averageValues", averageValues);

        return result;
    }

    /**
     * Get project timeline data by project type
     * @param projectType The type of project to analyze (optional)
     * @return Map with timeline data for projects
     */
    public Map<String, Object> getProjectTimelines(String projectType) {
        // Get all tenders (projects)
        List<Tender> allTenders = tenderRepository.findAll();

        // Filter by project type if specified
        List<Tender> filteredTenders = allTenders;
        if (projectType != null && !projectType.isEmpty() && !projectType.equalsIgnoreCase("all")) {
            // This is a simplified filter - in a real application, you might have a project type field
            // Here we're using the title as a proxy for project type
            filteredTenders = allTenders.stream()
                    .filter(tender -> tender.getTitle().toLowerCase().contains(projectType.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Calculate average timeline metrics
        Map<String, Object> result = new HashMap<>();

        // Calculate average time from tender creation to bid acceptance
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        List<Long> projectDurations = new ArrayList<>();
        List<String> projectNames = new ArrayList<>();
        List<Double> projectBudgets = new ArrayList<>();
        List<Double> actualCosts = new ArrayList<>();

        for (Tender tender : filteredTenders) {
            // Only include completed projects (with accepted bids)
            List<Bid> acceptedBids = tender.getBidIds() != null ?
                bidRepository.findAllById(tender.getBidIds()).stream()
                    .filter(bid -> "accepted".equals(bid.getStatus()))
                    .collect(Collectors.toList()) : Collections.emptyList();

            if (!acceptedBids.isEmpty()) {
                // Get the first accepted bid (in a real system, there should be only one)
                Bid acceptedBid = acceptedBids.get(0);

                // Calculate project duration in days (from tender creation to proposed deadline)
                LocalDateTime tenderCreationDate = LocalDateTime.parse(tender.getCreatedAt(), formatter);
                LocalDateTime proposedDeadline = LocalDateTime.parse(acceptedBid.getProposedDeadline(), formatter);
                long durationDays = java.time.Duration.between(tenderCreationDate, proposedDeadline).toDays();

                projectDurations.add(durationDays);
                projectNames.add(tender.getTitle());
                projectBudgets.add(tender.getBudget());
                actualCosts.add(acceptedBid.getAmount());
            }
        }

        // Calculate budget variance (difference between budget and actual cost)
        List<Double> budgetVariances = new ArrayList<>();
        for (int i = 0; i < projectBudgets.size(); i++) {
            double variance = ((actualCosts.get(i) - projectBudgets.get(i)) / projectBudgets.get(i)) * 100;
            budgetVariances.add(variance);
        }

        result.put("projectNames", projectNames);
        result.put("durations", projectDurations);
        result.put("budgets", projectBudgets);
        result.put("actualCosts", actualCosts);
        result.put("budgetVariances", budgetVariances);

        // Calculate averages for summary metrics
        OptionalDouble avgDurationOpt = projectDurations.stream().mapToLong(Long::longValue).average();
        OptionalDouble avgBudgetVarianceOpt = budgetVariances.stream().mapToDouble(Double::doubleValue).average();

        Map<String, Object> summaryMetrics = new HashMap<>();
        summaryMetrics.put("averageDuration", avgDurationOpt.orElse(0));
        summaryMetrics.put("averageBudgetVariance", avgBudgetVarianceOpt.orElse(0));
        summaryMetrics.put("totalProjects", projectNames.size());

        result.put("summary", summaryMetrics);

        return result;
    }
}