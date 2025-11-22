package com.timetracker.dao;

import com.timetracker.model.TimeLog;
import com.timetracker.util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TimeLogDAO {
    
    public List<TimeLog> getTimeLogsByUserId(int userId) {
        List<TimeLog> timeLogs = new ArrayList<>();
        String query = "SELECT * FROM time_logs WHERE user_id = ? ORDER BY date DESC";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeLogs.add(mapResultSetToTimeLog(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timeLogs;
    }
    
    public boolean createTimeLog(TimeLog timeLog) {
        String query = "INSERT INTO time_logs (task_id, user_id, date, hours_spent, description) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, timeLog.getTaskId());
            stmt.setInt(2, timeLog.getUserId());
            stmt.setDate(3, Date.valueOf(timeLog.getDate()));
            stmt.setDouble(4, timeLog.getHoursSpent());
            stmt.setString(5, timeLog.getDescription());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<TimeLog> getTimeLogsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<TimeLog> timeLogs = new ArrayList<>();
        String query = "SELECT * FROM time_logs WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    timeLogs.add(mapResultSetToTimeLog(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return timeLogs;
    }
    
    public double getTotalHoursByTaskId(int taskId) {
        String query = "SELECT SUM(hours_spent) FROM time_logs WHERE task_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, taskId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    private TimeLog mapResultSetToTimeLog(ResultSet rs) throws SQLException {
        TimeLog timeLog = new TimeLog();
        timeLog.setId(rs.getInt("id"));
        timeLog.setTaskId(rs.getInt("task_id"));
        timeLog.setUserId(rs.getInt("user_id"));
        
        Date date = rs.getDate("date");
        if (date != null) {
            timeLog.setDate(date.toLocalDate());
        }
        
        timeLog.setHoursSpent(rs.getDouble("hours_spent"));
        timeLog.setDescription(rs.getString("description"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            timeLog.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return timeLog;
    }
}