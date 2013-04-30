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

(defn n-spaces [n] 
  (repeat-string n " "))

(defn title? [token]
  (= (:node-type token) :tcword))

(defn right-brace? [token]
  (= (:node-type token) :trbrace))

(defn ignore-title-words? [token]
  (contains? #{"\\documentstyle" "\\title" "\\author" "\\inst" "\\institute" "\\part" "\\chapter" 
         "\\section" "\\subsection" "\\subsubsection" "\\paragraph"}
       (:text token)))

(defn map-words [tokens]
  (-> (reduce (fn [result token] 
                (cond (word? token)
                      (if-not (:ignore-words result) 
                        (update-in result [:m] conj (:text token))
                        (update-in result [:m] conj (n-spaces (count (:text token)))))
                      ;; start ignoring words when the token is one of the ignored titles
                      (and (title? token) (ignore-title-words? token))
                      (-> result 
                          (update-in [:m] conj token)
                          (assoc :ignore-words true))
                      ;; stop ignoring words when } is found
                      (right-brace? token)
                      (-> result
                          (update-in [:m] conj token)
                          (assoc :ignore-words false))
                      :else (update-in result [:m] conj token)))
              {:ignore-words false :m []}
              tokens)
      :m))

(defn extract-text [tokens]
  (let [text (->> tokens
                  (map-words)
                  (map #(if (comment? %) "\n" %))
                  (map #(if-not (string? %)
                          (n-spaces (count (:text %)))
                          %))
                  (string/join))]
    text))