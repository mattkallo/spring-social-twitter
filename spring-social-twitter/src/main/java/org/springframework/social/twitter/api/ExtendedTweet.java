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
package org.springframework.social.twitter.api;

import java.util.Arrays;
import java.util.Objects;

/**
 * Hold the data that Twitter sends while on compatibility mode.
 *
 * @author Mario Lopez
 */
public final class ExtendedTweet {

    private String fullText;
    private int[] displayTextRange;
    private Entities entities;

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public int[] getDisplayTextRange() {
        if (displayTextRange == null) {
            return null;
        }
        return displayTextRange.clone();
    }

    public void setDisplayTextRange(int[] displayTextRange) {
        if (displayTextRange != null && displayTextRange.length != 2) {
            throw new IllegalArgumentException("Display text range should have only two elements");
        }
        this.displayTextRange = displayTextRange;
    }

    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ExtendedTweet that = (ExtendedTweet) o;
        return Objects.equals(fullText, that.fullText) && Arrays.equals(displayTextRange, that.displayTextRange)
                && Objects.equals(entities, that.entities);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fullText, entities);
        result = 31 * result + Arrays.hashCode(displayTextRange);
        return result;
    }

    @Override
    public String toString() {
        return "ExtendedTweet{fullText='" + fullText + '\'' + ", displayTextRange=" +
                Arrays.toString(displayTextRange) + ", entities=" + entities + '}';
    }
}
