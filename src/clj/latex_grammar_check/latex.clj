(ns latex-grammar-check.latex
  (:require [clojure.string :as string])
  (:import [texparser.parser LatexLexer]
           [java.io PushbackReader StringReader]
           [texparser.node EOF TCpart TArgument TWord TWhitespace]))
  
(defn eof? [token]
  (instance? EOF token))
  
(defn tokenize
  [latex-markup]  
  (let [lexer (LatexLexer. (PushbackReader. (StringReader. latex-markup) 4096))]
    (loop [token (.next lexer) 
           result []]
      (if (eof? token) 
        result
        (recur (.next lexer) 
               (conj result {:node-type (keyword (.toLowerCase (.getSimpleName (class token))))
                             :text (.getText token) 
                             :line (.getLine token) 
                             :pos (.getPos token)}))))))

(defn word? [token]
  (or (= (:node-type token) :twhitespace)
      (= (:node-type token) :tword)))

(defn comment? [token]
  (= (:node-type token) :tcommentline))

(defn repeat-string [n s]
  (apply str (repeat n s)))

(defn extract-text [tokens]
  (let [text (->> tokens
                  (filter #(or (word? %) (comment? %)))
                  (map #(if (word? %) (:text %) %))
                  (map #(if (comment? %) "\n" %))
                  (map #(if-not (string? %) 
                          (repeat-string (count (:text %)) " ") 
                          %))
                  (string/join))]
    text))