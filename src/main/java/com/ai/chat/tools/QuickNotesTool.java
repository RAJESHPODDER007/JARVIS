package com.ai.chat.tools;

import com.ai.chat.model.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class QuickNotesTool {

    private final JdbcTemplate jdbcTemplate;

    @Tool(description = "Save quick note information in database")
    public void saveQuickNote(String note){
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String sql= "INSERT INTO notes (note, date) VALUES (?, ?)";
        jdbcTemplate.update(sql, note, today);
    }

    @Tool(description = "Result all notes based on specified date (format: YYYY-MM-DD)")
    public List<Note> getAllNotes(String date){
        String sql= "SELECT note, date FROM notes WHERE date=?";
        RowMapper<Note> rowMapper = (rs, rowNum)->
                new Note(rs.getString("note"),
                        rs.getString("date"));
        return jdbcTemplate.query(sql, rowMapper, date);
    }
}
