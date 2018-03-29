package com.pszumans.nbastatsbackend;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
public class TeamSerializer extends JsonSerializer<Team> {

    @Override
    public void serialize(Team team, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", team.getName());
        map.put("tricode", team.getTricode());
        jsonGenerator.writeObject(map);
    }
}
