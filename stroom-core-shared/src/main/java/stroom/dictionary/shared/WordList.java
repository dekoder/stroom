/*
 * Copyright 2024 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.dictionary.shared;

import stroom.docref.DocRef;
import stroom.util.shared.GwtNullSafe;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A list of {@link Word} objects
 */
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(Include.NON_NULL)
public class WordList {

    /**
     * Immutable empty wordlist
     */
    public static final WordList EMPTY = new WordList(Collections.emptyList(), Collections.emptyMap());

    @JsonProperty
    private final List<Word> wordList;
    @JsonProperty
    private final Map<String, DocRef> sourceUuidToDocRefMap;

    @JsonCreator
    WordList(@JsonProperty("wordList") final List<Word> wordList,
             @JsonProperty("sourceUuidToDocRefMap") final Map<String, DocRef> sourceUuidToDocRefMap) {
        this.wordList = wordList;
        this.sourceUuidToDocRefMap = sourceUuidToDocRefMap;
    }

    public List<Word> getWordList() {
        return wordList;
    }

    Map<String, DocRef> getSourceUuidToDocRefMap() {
        return sourceUuidToDocRefMap;
    }

    @JsonIgnore
    public Set<DocRef> getSources() {
        return Collections.unmodifiableSet(new HashSet<>(sourceUuidToDocRefMap.values()));
    }

    @JsonIgnore
    public Optional<DocRef> getSource(final Word word) {
        return Optional.ofNullable(sourceUuidToDocRefMap.get(Objects.requireNonNull(word).getSourceUuid()));
    }

    /**
     * @return The word list sorted by word (case-insensitive), then by source UUID (if duplicates are allowed).
     */
    @JsonIgnore
    public List<Word> getSortedList() {
        return wordList.stream()
                .sorted(Word.CASE_INSENSE_WORD_COMPARATOR)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public Optional<Word> getWord(final String word) {
        if (GwtNullSafe.isNonBlankString(word)) {
            return wordList.stream()
                    .filter(wordObj -> Objects.equals(wordObj.getWord(), word))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    public boolean isEmpty() {
        return wordList.isEmpty();
    }

    public int size() {
        return wordList.size();
    }

    public int sourceCount() {
        return sourceUuidToDocRefMap.size();
    }

    /**
     * @return The complete wordList as a single string with words delimited by {@code \n}.
     * The last item may not have a trailing {@code \n}.
     */
    public String asString() {
        if (wordList.isEmpty()) {
            return "";
        } else {
            return wordList.stream()
                    .map(Word::getWord)
                    .collect(Collectors.joining("\n"));
        }
    }

    /**
     * @return The complete word list as a simple array or words.
     */
    public String[] asWordArray() {
        if (wordList.isEmpty()) {
            return new String[0];
        } else {
            return wordList.stream()
                    .map(Word::getWord)
                    .toArray(String[]::new);
        }
    }

    @Override
    public String toString() {
        return "WordList{" +
                "wordList=" + wordList +
                ", sourceUuidToDocRefMap=" + sourceUuidToDocRefMap +
                '}';
    }

    public static Builder builder(final boolean deDup) {
        return new Builder(deDup);
    }


    // --------------------------------------------------------------------------------


    public static class Builder {

        private final List<Word> wordObjList = new ArrayList<>();
        private final Set<String> wordSet = new HashSet<>();
        private final Map<String, DocRef> sourceUuidToDocRefMap = new HashMap<>();
        private final boolean deDup;

        private Builder(final boolean deDup) {
            this.deDup = deDup;
        }

        public Builder addWord(final String word, final DocRef source) {
            // Silently ignore blank/null words
            if (GwtNullSafe.isNonBlankString(word)) {
                final Word wordObj = new Word(
                        word, Objects.requireNonNull(source, "Source DocRef required for word").getUuid());
                boolean doAdd = true;
                // Assumes that words are added in precedence order, i.e. most important source first
                if (deDup) {
                    if (wordSet.contains(word)) {
                        doAdd = false;
                    }
                }
                if (doAdd) {
                    wordObjList.add(wordObj);
                    wordSet.add(word);
                    sourceUuidToDocRefMap.putIfAbsent(source.getUuid(), source);
                }
            }
            return this;
        }

        public WordList build() {
            return wordObjList.isEmpty()
                    ? EMPTY
                    : new WordList(wordObjList, sourceUuidToDocRefMap);
        }
    }
}
