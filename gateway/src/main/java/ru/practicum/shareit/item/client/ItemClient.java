package ru.practicum.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(int userId, ItemDto newItemDto) {
        return post("", userId, newItemDto);
    }

    public ResponseEntity<Object> update(int itemId, int userId, ItemDto newItemDto) {
        return patch("/" + itemId, userId, newItemDto);
    }

    public ResponseEntity<Object> getItemById(int itemId, int userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getUserItems(int userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> searchItems(int userId, String text, int from, int size) {
        Map<String, Object> parameters = Map.of("text", text, "from", from, "size", size);
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }
}
