/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.twitter.api.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.social.twitter.api.Entities;
import org.springframework.social.twitter.api.ExtendedTweet;
import org.springframework.social.twitter.api.TickerSymbolEntity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Custom Jackson deserializer for extended tweets. Because the ticker symbols are treated as first class citizens,
 * we need to create a custom deserializer for the extended tweet.
 *
 * @author Mario Lopez
 */
public class ExtendedTweetDeserializer extends JsonDeserializer<ExtendedTweet> {
    @Override
    public ExtendedTweet deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        final JsonNode node = jp.readValueAs(JsonNode.class);
        if (null == node || node.isMissingNode() || node.isNull()) {
            return null;
        }
        ExtendedTweet extendedTweet = new ExtendedTweet();
        JsonNode fullText = node.get("full_text");
        if (fullText != null) {
            extendedTweet.setFullText(fullText.asText(null));
        }

        JsonNode displayRange = node.get("display_text_range");
        if (displayRange != null && displayRange.isArray()) {
            Iterator<JsonNode> it = displayRange.iterator();
            int i = 0;
            int[] range = new int[2];
            while (it.hasNext()) {
                range[i] = it.next().asInt();
                i++;
            }
            if (i != 2) {
                throw new IOException("Unexpected display_text_range size of " + i);
            }
            extendedTweet.setDisplayTextRange(range);
        }

        JsonNode entitiesNode = node.get("entities");
        if (entitiesNode != null && !entitiesNode.isNull() && !entitiesNode.isMissingNode()) {
            Entities entities = createMapper().readerFor(Entities.class).readValue(entitiesNode);
            if (extendedTweet.getFullText() != null) {
                extractTickerSymbolEntitiesFromText(extendedTweet.getFullText(), entities);
            }
            extendedTweet.setEntities(entities);
        }
        jp.skipChildren();
        return extendedTweet;
    }

    private void extractTickerSymbolEntitiesFromText(String text, Entities entities) {
        Pattern pattern = Pattern.compile("\\$[A-Za-z]+");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            MatchResult matchResult = matcher.toMatchResult();
            String tickerSymbol = matchResult.group().substring(1);
            String url = "https://twitter.com/search?q=%24" + tickerSymbol + "&src=ctag";
            entities.getTickerSymbols().add(new TickerSymbolEntity(tickerSymbol, url,
                    new int[] { matchResult.start(), matchResult.end() }));
        }
    }

    private ObjectMapper createMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new TwitterModule());
        return mapper;
    }

}
