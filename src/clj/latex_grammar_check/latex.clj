(ns latex-grammar-check.latex
  (:require [clojure.string :as string])
  (:import [texparser.parser LatexLexer]
           [java.io PushbackReader StringReader]
           [texparser.node EOF TCpart TArgument TWord TWhitespace]))
  
(defn eof? [token]
  (instance? EOF token))
  
;AG: improved structure
;  "- group text from all the tokens of type TWhitespace and TWord
;   - start a new group when a token of a type other than TWhitespace and TWord is found"

(defn tokenize
  [latex-markup]  
  (let [lexer (LatexLexer. (PushbackReader. (StringReader. latex-markup) 4096))]
    (loop [token (.next lexer) result []]
      (if (eof? token) 
        result
        (recur (.next lexer) 
               (conj result {:node-type (keyword (.toLowerCase (.getSimpleName (class token))))
                             :text (.getText token) 
                             :line (.getLine token) 
                             :pos (.getPos token)}))))))

(defn- word? [token]
  (or (= (:node-type token) :twhitespace)
      (= (:node-type token) :tword)))

(defn extract-text [tokens]
  (->> tokens
       (partition-by word?)
       (map #(if (word? (first %)) (concat %) []))
       (map #(map :text %))
       (map #(string/join %))
       (string/join)
       ))