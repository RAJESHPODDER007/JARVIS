package com.ai.chat.tools;

import com.ai.chat.model.Contact;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContactTool {

    private final JdbcTemplate jdbcTemplate;

    @Tool(description = "Find contacts in a given city")
    public List<Contact> findContactsByCity(String city){
        String sql= "SELECT name, city, email FROM contacts WHERE city=?";
        RowMapper<Contact> rowMapper = (rs,rowNum)->
                new Contact(rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("city"));
        return jdbcTemplate.query(sql,rowMapper,city);
    }

    @Tool(description = "Converts contacts into CSV format with header name,email,city")
    public String formatToCsv(List<Contact> contacts){
        StringBuilder sb = new StringBuilder();
        sb.append("name,email,city\n");
        for (Contact contact:contacts){
            sb.append(contact.name());
            sb.append(",");
            sb.append(contact.email());
            sb.append(",");
            sb.append(contact.city());
            sb.append("\n");
        }
        return sb.toString();
    }
}
