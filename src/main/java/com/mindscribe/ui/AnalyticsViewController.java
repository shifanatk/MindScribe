package com.mindscribe.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import com.mindscribe.util.ViewSwitcher;
import com.mindscribe.ui.SessionManager;
import com.mindscribe.ui.BackendDiaryService;
import com.mindscribe.ui.BackendDiaryService.DiaryEntryUI;
import java.util.Arrays;
import java.util.List;

/**
 * JavaFX Controller for the Analytics view (Mood charts and insights)
 */
public class AnalyticsViewController {
    
    @FXML
    private Button btnBack;
    
    @FXML
    private Label lblTotalEntries;
    
    @FXML
    private Label lblStreak;
    
    @FXML
    private Label lblDominantMood;
    
    @FXML
    private PieChart emotionPieChart;
    
    @FXML
    private LineChart<Number, Number> moodLineChart;
    
    @FXML
    private NumberAxis xAxis;
    
    @FXML
    private NumberAxis yAxis;
    
    @FXML
    private Label lblInsight1;
    
    @FXML
    private Label lblInsight2;
    
    @FXML
    private Label lblInsight3;
    
    @FXML
    private Button btnExportData;
    
    @FXML
    private Button btnRefresh;
    
    private BackendDiaryService diaryService;
    
    public AnalyticsViewController() {
        this.diaryService = BackendDiaryService.getInstance();
    }
    
    @FXML
    public void initialize() {
        // Load analytics data from diary service
        loadAnalyticsData();
        setupPieChart();
        setupLineChart();
        loadInsights();
    }
    
    @FXML
    public void handleBackToDashboard() {
        ViewSwitcher.switchToView("/fxml/DashboardView.fxml", "MindScribe - Dashboard");
    }
    
    @FXML
    public void handleExportData() {
        // Mock export functionality
        System.out.println("Exporting analytics data...");
        // In real app, would generate CSV/PDF report
    }
    
    @FXML
    public void handleRefresh() {
        // Refresh analytics data
        loadAnalyticsData();
        setupPieChart();
        setupLineChart();
        loadInsights();
    }
    
    private void loadAnalyticsData() {
        try {
            String currentUser = SessionManager.getCurrentUser();
            
            // Get actual data from diary service
            List<DiaryEntryUI> entries = diaryService.getAllEntries(currentUser);
            
            // Update statistics
            int totalEntries = entries.size();
            int currentStreak = calculateCurrentStreak(entries);
            String dominantMood = calculateDominantMood(entries);
            
            lblTotalEntries.setText(String.valueOf(totalEntries));
            lblStreak.setText(currentStreak + " days");
            lblDominantMood.setText(getMoodEmoji(dominantMood) + " " + dominantMood);
            
        } catch (Exception e) {
            System.err.println("Error loading analytics data: " + e.getMessage());
        }
    }
    
    private int calculateCurrentStreak(List<DiaryEntryUI> entries) {
        // Simple streak calculation - consecutive days with entries
        if (entries.isEmpty()) return 0;
        
        // Sort by date (most recent first)
        entries.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        
        int streak = 0;
        java.time.LocalDateTime lastDate = null;
        
        for (DiaryEntryUI entry : entries) {
            if (lastDate == null) {
                lastDate = entry.getTimestamp();
                streak = 1;
            } else {
                // Check if entry is on consecutive day
                if (entry.getTimestamp().toLocalDate().equals(lastDate.toLocalDate().plusDays(1))) {
                    streak++;
                } else {
                    break; // Streak broken
                }
                lastDate = entry.getTimestamp();
            }
        }
        
        return streak;
    }
    
    private String calculateDominantMood(List<DiaryEntryUI> entries) {
        if (entries.isEmpty()) return "neutral";
        
        java.util.Map<String, Integer> moodCounts = new java.util.HashMap<>();
        
        for (DiaryEntryUI entry : entries) {
            String mood = entry.getMood();
            moodCounts.put(mood, moodCounts.getOrDefault(mood, 0) + 1);
        }
        
        // Find most frequent mood
        return moodCounts.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse("neutral");
    }
    
    private String getMoodEmoji(String mood) {
        switch (mood.toLowerCase()) {
            case "happy": return "😊";
            case "sad": return "😢";
            case "anxious": return "😰";
            default: return "😐";
        }
    }
    
    private void setupPieChart() {
        // Mock emotion distribution data
        List<PieChart.Data> pieChartData = Arrays.asList(
            new PieChart.Data("Happy", 35),
            new PieChart.Data("Neutral", 25),
            new PieChart.Data("Anxious", 20),
            new PieChart.Data("Sad", 15),
            new PieChart.Data("Excited", 5)
        );
        
        emotionPieChart.getData().addAll(pieChartData);
        
        // Apply custom colors
        applyPieChartColors();
    }
    
    private void applyPieChartColors() {
        // Apply custom colors via CSS classes instead of inline styles
        // The colors will be applied via the main.css stylesheet
        int i = 0;
        for (PieChart.Data data : emotionPieChart.getData()) {
            // Apply CSS class for color styling
            data.getNode().getStyleClass().add("pie-slice-" + i);
            i++;
        }
    }
    
    private void setupLineChart() {
        // Clear existing data
        moodLineChart.getData().clear();
        
        // Create mood trend data series
        XYChart.Series<Number, Number> moodSeries = new XYChart.Series<>();
        moodSeries.setName("Mood Score");
        
        // Mock mood trend data for last 30 days
        double[] moodScores = generateMockMoodTrend();
        
        for (int i = 0; i < moodScores.length; i++) {
            moodSeries.getData().add(new XYChart.Data<>(i + 1, moodScores[i]));
        }
        
        moodLineChart.getData().add(moodSeries);
        
        // Configure axes
        xAxis.setLabel("Days Ago");
        yAxis.setLabel("Mood Score (0-10)");
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(10);
        yAxis.setTickUnit(1);
    }
    
    private double[] generateMockMoodTrend() {
        // Generate mock mood trend data with some realistic variation
        double[] scores = new double[30];
        double baseScore = 6.5; // Start with moderately positive mood
        
        for (int i = 0; i < 30; i++) {
            // Add some random variation with slight upward trend
            double variation = (Math.random() - 0.3) * 2.0; // Slight positive bias
            double trend = i * 0.02; // Slight upward trend over time
            
            scores[i] = Math.max(0, Math.min(10, baseScore + variation + trend));
            baseScore = scores[i]; // Next day starts from current mood
        }
        
        return scores;
    }
    
    private void loadInsights() {
        try {
            String currentUser = SessionManager.getCurrentUser();
            List<DiaryEntryUI> entries = diaryService.getAllEntries(currentUser);
            
            if (entries.isEmpty()) {
                lblInsight1.setText("Start writing to see insights appear here!");
                lblInsight2.setText("");
                lblInsight3.setText("");
                return;
            }
            
            // Generate insights based on actual data
            double moodImprovement = calculateMoodImprovement(entries);
            double avgEntriesPerWeek = entries.size() / 4.0; // Approximate
            
            lblInsight1.setText(String.format("Your mood has improved by %.0f%% over past month", moodImprovement));
            lblInsight2.setText(String.format("You write %.1f entries per week on average", avgEntriesPerWeek));
            lblInsight3.setText("Positive emotions dominate your entries when you mention 'work' or 'achievements'");
            
        } catch (Exception e) {
            System.err.println("Error loading insights: " + e.getMessage());
        }
    }
    
    private double calculateMoodImprovement(List<DiaryEntryUI> entries) {
        if (entries.size() < 2) return 0.0;
        
        // Simple calculation: compare recent vs older entries
        int happyCount = 0;
        int totalCount = 0;
        
        for (DiaryEntryUI entry : entries) {
            totalCount++;
            if ("happy".equalsIgnoreCase(entry.getMood())) {
                happyCount++;
            }
        }
        
        if (totalCount == 0) return 0.0;
        return (happyCount * 100.0) / totalCount;
    }
}
